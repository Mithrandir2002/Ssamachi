# Ingestion / ETL — kế hoạch triển khai

Subtask đầu tiên của core-service: đưa dữ liệu động đất từ USGS vào SQL Server qua Kafka.
Mọi thứ khác (query/analytics, alert, report, websocket) đều chờ bước này có dữ liệu.

## Quyết định đã chốt

| Hạng mục | Chốt | Lý do |
|---|---|---|
| Nguồn realtime | `all_hour.geojson` | không có feed 15 phút; cửa sổ 60 phút cho overlap an toàn |
| Chu kỳ poll | 5 phút, `@Scheduled` | overlap 12 lần, lỡ nhịp vẫn vá được |
| Nguồn lịch sử | FDSN `query` API | feed tĩnh chỉ có 4 cửa sổ trượt, không có dữ liệu năm cũ |
| Phạm vi backfill | 2020-01-01 → nay, **M ≥ 2.5** | ~189.857 trận (số đo thật) — đủ nặng để index có ý nghĩa, đủ nhẹ cho laptop |
| Kích thước batch | theo **tháng**, ~80 request | FDSN giới hạn cứng **20.000 event/query**; M≥2.5 ≈ 2.400/tháng |
| Trigger backfill | thủ công `POST /api/admin/ingestion/backfill` | không ai muốn 80 request lịch sử chạy mỗi lần khởi động |
| Message Kafka | **1 trận = 1 message**, key = USGS event id | tránh vỡ giới hạn 1MB, và đảm bảo thứ tự revise theo từng trận |
| Bản gốc | lưu nguyên FeatureCollection vào MongoDB | để replay khi transform sai |

Chọn M≥2.5 không khoá đường lui: sau này chạy lại backfill với `minmagnitude=1.0`
(736.545 trận) là idempotent, vì consumer upsert theo event id.

## Luồng

```
USGS
 ├─ all_hour.geojson        (@Scheduled 5 phút)
 └─ fdsnws/event/1/query    (backfill thủ công, cắt theo tháng, chạy song song)
        │
        ├──── lưu nguyên FeatureCollection ────▶ MongoDB  raw_ingestion_payloads
        │
        │  tách từng Feature, key = event id
        ▼
   [ raw-earthquakes ]  3 partition
        │
        ▼
   @KafkaListener  — parse, chuẩn hoá, upsert
        ├──────▶ SQL Server  earthquakes
        ▼
   [ processed-earthquakes ]  ──▶ (alert engine, làm ở subtask sau)
```

## Mapping GeoJSON → bảng `earthquakes`

Mẫu thật lấy từ feed (`id: nc75435452`):

| Nguồn | Cột | Kiểu | Ghi chú |
|---|---|---|---|
| `id` | `id` | VARCHAR(50) | natural key, dùng làm Kafka message key luôn |
| `properties.mag` | `magnitude` | FLOAT | **có thể null** |
| `properties.place` | `place` | NVARCHAR(500) | |
| `properties.time` | `event_time` | DATETIME2 | epoch **millis** UTC |
| `properties.updated` | `updated_time` | DATETIME2 | epoch millis — dùng để so sánh revise |
| `geometry.coordinates[0]` | `longitude` | FLOAT | **lon đứng trước** |
| `geometry.coordinates[1]` | `latitude` | FLOAT | |
| `geometry.coordinates[2]` | `depth_km` | FLOAT | |
| — | `location` | GEOGRAPHY | không map bằng JPA, xem T5 |
| `properties.magType` | `magnitude_type` | VARCHAR(10) | `md`, `ml`, `mww`… |
| `properties.status` | `status` | VARCHAR(20) | `automatic` / `reviewed` |
| `properties.tsunami` | `tsunami_flag` | BIT | 0/1 |
| `properties.felt` | `felt_reports` | INT | **thường null** |
| — | `ingested_at` | DATETIME2 | thời điểm ghi |

Thứ tự `[longitude, latitude, depth]` là bẫy kinh điển của GeoJSON — ngược với thói quen
đọc "lat, lon". Đảo nhầm thì mọi trận động đất sẽ nằm sai vị trí trên bản đồ mà không có
lỗi nào báo.

**Cần quyết thêm**: `properties.type` không phải lúc nào cũng là `"earthquake"` — feed còn
có `quarry blast`, `explosion`, `ice quake`. Bảng hiện chưa có cột này. Hai lựa chọn: lọc
bỏ ở consumer (chỉ giữ `type == "earthquake"`), hoặc thêm cột `event_type` và giữ hết.

## Các task

### T1 — USGS client + config
- **File**: `config/UsgsProperties.java` (`@ConfigurationProperties(prefix = "usgs")`), `ingestion/UsgsClient.java`
- Đưa base URL, feed URL, `minMagnitude`, `backfillStartDate` vào `application.yml`, không hardcode.
- `UsgsClient` dùng `RestTemplate` (bean đã có ở `RestTemplateConfig`), trả về DTO `UsgsFeatureCollection`.
- Đặt timeout connect/read tường minh — mặc định của `RestTemplate` là *vô hạn*, một lần USGS treo là thread `@Scheduled` treo theo mãi mãi.
- **Kiểm chứng**: viết một test gọi thật feed, assert `features` không rỗng.

### T2 — DTO cho GeoJSON
- **File**: `ingestion/dto/UsgsFeatureCollection.java`, `UsgsFeature.java`, `UsgsProperties.java`, `UsgsGeometry.java`
- Dùng `@JsonIgnoreProperties(ignoreUnknown = true)` — feed có 26 field trong `properties`, mình chỉ cần 8.
- `coordinates` là `List<Double>`, tự viết helper `longitude()/latitude()/depthKm()` để không ai phải nhớ index.
- **Kiểm chứng**: unit test parse một file JSON mẫu đã lưu sẵn.

### T3 — Producer đẩy vào Kafka
- **File**: `kafka/KafkaProducerService.java` (đang là stub)
- `kafkaTemplate.send(RAW_EARTHQUAKES_TOPIC, feature.id(), feature)`.
- Cấu hình `spring.kafka.producer.*`: JSON serializer, `acks=all`, `linger-ms: 20` để gom lô.
- **Kiểm chứng**: publish thử, mở kafka-ui `localhost:8090` xem message và key.

### T4 — Poll realtime
- **File**: `ingestion/RealtimeIngestionScheduler.java`
- `@Scheduled(fixedDelayString = "${usgs.poll-interval-ms:300000}")` → gọi `all_hour` → tách từng Feature → publish.
- Dùng `fixedDelay` chứ không `fixedRate`: nếu một lần chạy lâu hơn chu kỳ, `fixedRate` sẽ dồn việc chồng lên nhau.
- Bật `@EnableScheduling` (kiểm tra xem `CoreServiceApplication` đã có chưa).
- **Kiểm chứng**: chạy app 10 phút, kafka-ui thấy message tăng dần.

### T5 — Consumer: transform + upsert
- **File**: `kafka/ProcessedEarthquakeConsumer.java` → đổi tên thành `RawEarthquakeConsumer`, và tạo mới consumer cho `processed-earthquakes` sau.
- Parse → map sang entity `Earthquake` → **upsert theo `id`**:
  ```
  nếu chưa có       -> insert
  nếu đã có và updated_time mới hơn  -> update
  nếu đã có và updated_time cũ hơn   -> bỏ qua (message tới trễ)
  ```
- So sánh `updated_time` là bắt buộc: Kafka giao *at-least-once*, message có thể lặp và có thể tới không đúng thứ tự giữa các lần retry.
- **Kiểm chứng**: publish lại đúng một message 2 lần, bảng vẫn chỉ 1 row.

### T6 — Cột `location` GEOGRAPHY
- **File**: `repository/EarthquakeRepository.java` (thêm native query)
- JPA không map được `GEOGRAPHY` nếu không có `hibernate-spatial`, nên cập nhật bằng native SQL sau khi insert/update:
  ```sql
  UPDATE earthquakes
  SET location = geography::Point(:lat, :lon, 4326)
  WHERE id = :id
  ```
- Chú ý `geography::Point` nhận **(lat, lon)** — ngược thứ tự với GeoJSON.
- **Kiểm chứng**: `SELECT TOP 5 id, location.ToString() FROM earthquakes` ra `POINT (lon lat)`.

### T7 — Lưu bản gốc vào MongoDB
- **File**: `raw/RawIngestionPayloadRepository.java` (đã có), gọi từ ingestion service
- Mỗi lần fetch lưu **một** document: `{source, fetchType, requestParams, rawResponse, fetchedAt, jobId}`.
- Không lưu từng Feature — mục đích là replay được nguyên mẻ, không phải nhân đôi dữ liệu.
- **Kiểm chứng**: `docker exec mongodb mongosh core_raw_db --eval "db.raw_ingestion_payloads.countDocuments()"`

### T8 — Backfill song song
- **File**: `ingestion/BackfillService.java`, `web/AdminIngestionController.java` (đang là stub)
- `POST /api/admin/ingestion/backfill` nhận `{startDate, endDate, minMagnitude}`:
  1. Tạo row `ingestion_jobs` status `PENDING`, **trả về ngay** id job (không bắt client chờ 80 request).
  2. Cắt khoảng thời gian thành từng tháng → mỗi tháng là một task.
  3. Submit vào `ingestionExecutor` (bean đã có ở `IngestionExecutorConfig`).
  4. `CompletableFuture.allOf(...)` chờ tất cả xong → cập nhật job `SUCCESS` / `FAILED` + `records_processed`.
- Giới hạn số task chạy song song (pool size 4–6 là đủ) — đừng bắn 80 request đồng thời vào USGS.
- Mỗi request thêm `orderby=time-asc`, và **kiểm tra số lượng trả về**: nếu chạm 20.000 nghĩa là tháng đó bị cắt cụt, phải chia nhỏ tiếp theo tuần.
- **Kiểm chứng**: `GET /api/admin/ingestion/jobs/{id}` thấy status chuyển PENDING → RUNNING → SUCCESS, `records_processed` khớp với `SELECT COUNT(*) FROM earthquakes`.

### T9 — Publish sang `processed-earthquakes`
- Sau khi upsert thành công, publish bản đã chuẩn hoá sang topic thứ hai.
- Đây là đầu vào cho alert engine ở subtask sau; làm luôn ở đây để topic có dữ liệu sẵn.
- **Kiểm chứng**: kafka-ui thấy cả hai topic đều tăng.

## Cạm bẫy đã biết

1. **`auto-offset-reset` mặc định là `latest`** — consumer group mới sẽ bỏ qua toàn bộ message đã có trong topic. Đặt `spring.kafka.consumer.auto-offset-reset=earliest` cho môi trường dev, không thì backfill xong mà bảng vẫn trống và không hiểu vì sao.
2. **At-least-once** — message *sẽ* lặp. Upsert theo `id`, đừng bao giờ `INSERT` trần.
3. **USGS revise dữ liệu** — cùng một `id` được cập nhật magnitude sau vài giờ. Đó là lý do phải so `updated_time`, và cũng là lý do key = event id (mọi bản revise vào cùng partition, xử lý đúng thứ tự).
4. **Giới hạn 20.000/query** — không phải khuyến nghị, query vượt sẽ bị từ chối thẳng kèm lỗi `maxAllowed`.
5. **`[lon, lat, depth]`** — đảo nhầm không gây lỗi, chỉ sai âm thầm.
6. **`mag` và `felt` có thể null** — dùng `Double`/`Integer` chứ không `double`/`int`.
7. **Timeout RestTemplate mặc định là vô hạn.**
8. **Đổi partition count sau khi đã có dữ liệu sẽ đổi cách phân bổ key** — chốt 3 partition ngay từ đầu, chỉ tăng được chứ không giảm.

## Thứ tự thực hiện

Làm theo chiều dọc, mỗi bước chạy được và kiểm chứng được rồi mới sang bước sau:

1. **T1 + T2** — gọi được USGS và parse ra object. Chưa đụng Kafka.
2. **T3 + T4** — realtime poll đẩy được message vào Kafka. Nhìn thấy message trong kafka-ui.
3. **T5 + T6** — consumer ghi được xuống SQL Server. Đây là lúc luồng ETL đầu tiên khép kín.
4. **T7** — thêm lưu trữ bản gốc.
5. **T8** — backfill, bơm ~190k row. Sau bước này mới có dữ liệu đủ lớn để nghịch index.
6. **T9** — nối sang topic thứ hai.

Sau T8 là có thể chuyển sang subtask query/analytics và bắt đầu phần chính: đánh index,
đọc execution plan, so sánh seek với scan.
