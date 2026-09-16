# T8 — Backfill lịch sử

Kéo toàn bộ động đất từ 2020 tới nay về hệ thống, chạy song song, không bắt client chờ,
theo dõi được tiến độ.

## Số liệu thật (đo từ API USGS)

| Ngưỡng | Số trận 2020 → nay |
|---|---|
| M ≥ 4.5 | 51.117 |
| **M ≥ 2.5** (đã chốt) | **189.857** |
| M ≥ 1.0 | 736.545 |

Giới hạn cứng của FDSN: **20.000 event mỗi query**. Vượt là trả HTTP 400, **không** cắt bớt.
Đây là ràng buộc bắt buộc phải chia nhỏ khoảng thời gian, không phải lựa chọn thiết kế.

## Từ vựng — đọc kỹ phần này trước

| Từ | Nghĩa ở đây |
|---|---|
| **event** | **một trận động đất**. USGS gọi mỗi trận là "seismic event" |
| **chunk** | **một cửa sổ thời gian** (vd 30 ngày), *không phải* một nhóm bản ghi |
| **message** | một message Kafka = **một trận động đất** |

Chuỗi tương đương:
```
1 trận động đất  =  1 phần tử trong mảng "features"  =  1 UsgsFeature
                 =  1 message Kafka (key = event id)  =  1 row trong earthquakes
```

Chunk được cắt **trước khi** gọi API — vì một request không thể xin quá 20.000 trận.

## Toàn cảnh một lần bấm nút

```
POST /api/admin/ingestion/backfill  {startDate, endDate, minMagnitude}
 │
 ├── INSERT ingestion_jobs → id=42, PENDING
 ├── trả 202 Accepted + {id: 42}          ← client nhận response NGAY tại đây
 │
 └── (thread nền) cắt thời gian thành 80 cửa sổ 30 ngày
      │   UPDATE status = RUNNING
      │
      ├─ chunk 1  [2020-01-01 → 2020-01-31)  ─► 1 request ─► ~2400 trận ─► 2400 message
      ├─ chunk 2  [2020-01-31 → 2020-03-01)  ─► 1 request ─► ~2300 trận ─► 2300 message
      ├─ ...                                    4 chunk chạy song song
      └─ chunk 80                             ─► 1 request ─► ~2500 trận ─► 2500 message
      │
      │   mỗi chunk xong: UPDATE records_processed += n
      └── UPDATE status = SUCCESS / FAILED
```

Tổng: **1 row** job, **80 request**, **~190.000 message**, **~190.000 row** trong `earthquakes`.

## Ranh giới trách nhiệm

```
BackfillService (producer)          Kafka          RawEarthquakeConsumer
──────────────────────────────────────────────────────────────────────────
ghi ingestion_jobs                    │           chỉ upsert earthquakes
gọi USGS                              │           không biết job nào tồn tại
publish message            ──────────►│──────────► không đụng ingestion_jobs
```

`ingestion_jobs` thuộc **hoàn toàn** về phía producer. Consumer không đọc, không ghi bảng này.

Hệ quả: `records_processed` đếm **số trận đã đẩy vào Kafka**, không phải số row đã nằm
trong DB. Hai con số luôn lệch nhau vì consumer chạy sau. Muốn đếm số row thật thì consumer
phải báo ngược lại producer — làm vậy là dán hai bên vào nhau, mất sạch lợi ích của Kafka.

## Trạng thái job

| Trạng thái | Khi nào | Ai ghi |
|---|---|---|
| `PENDING` | vừa INSERT, chưa thread nào nhận | controller |
| `RUNNING` | bắt đầu chunk đầu tiên | thread nền |
| `SUCCESS` | cả 80 chunk xong, không lỗi | thread nền |
| `FAILED` | ít nhất 1 chunk lỗi | thread nền |

Chunk lỗi thì ghi danh sách cửa sổ vào cột `error_message` để chạy lại đúng phần đó:
```
2 of 80 chunks failed: [2021-03-01..2021-03-31], [2023-07-01..2023-07-31]
```
Chạy lại an toàn tuyệt đối vì consumer upsert theo event id — không sinh bản ghi trùng.

---

## Các bước

### B1 — Chuẩn bị nền ✅ ĐÃ XONG

| File | Đã đổi |
|---|---|
| `dto/BackfillRequest` | `(startDate, endDate, minMagnitude)` + validation |
| `config/IngestionExecutorConfig` | `corePoolSize` 2 → **4**, thêm `CallerRunsPolicy` |
| `domain/JobStatus` | enum mới, 4 trạng thái |
| `domain/IngestionJob` | `status` thành enum, thêm `@PrePersist`/`@PreUpdate` |
| `repository/IngestionJobRepository` | `existsByStatus`, `findAllByOrderByCreatedAtDesc`, `addRecordsProcessed` |

### B2 — `TimeWindow` + hàm cắt cửa sổ

File mới: `ingestion/TimeWindow.java`

```java
public record TimeWindow(Instant from, Instant to) {

    public Duration length() {
        return Duration.between(from, to);
    }

    /** Dùng cho B5: chia đôi khi FDSN từ chối vì vượt trần 20k. */
    public List<TimeWindow> halve() {
        Instant mid = from.plus(length().dividedBy(2));
        return List.of(new TimeWindow(from, mid), new TimeWindow(mid, to));
    }
}
```

```java
static List<TimeWindow> split(LocalDate startDate, LocalDate endDate, int chunkDays) {
    List<TimeWindow> windows = new ArrayList<>();

    Instant cursor = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant limit  = endDate.atStartOfDay(ZoneOffset.UTC).toInstant();

    while (cursor.isBefore(limit)) {
        Instant next = cursor.plus(Duration.ofDays(chunkDays));
        if (next.isAfter(limit)) {
            next = limit;              // chunk cuối thường ngắn hơn
        }
        windows.add(new TimeWindow(cursor, next));
        cursor = next;
    }
    return windows;
}
```

Cửa sổ là **nửa mở** `[from, to)` — `to` của chunk này là `from` của chunk sau, không sót
không đè. Nhưng **FDSN tính cả hai đầu là bao gồm**, nên khi gọi phải trừ đi 1 giây:

```java
usgsClient.queryEvents(w.from(), w.to().minusSeconds(1), minMagnitude);
```

Không trừ cũng không sai dữ liệu (upsert), chỉ làm `records_processed` đếm dư vài trận.

**Kiểm chứng**: unit test thuần, không cần Docker. 2020-01-01 → 2020-03-01 với chunk 30 ngày
phải ra đúng 2 cửa sổ, cửa sổ cuối kết thúc đúng `limit`, không có khoảng trống giữa các cửa sổ.

### B3 — Trả response ngay, xử lý ở thread nền

Có **hai tầng bất đồng bộ**, đừng lẫn:

- **Tầng ngoài**: một task "chủ job" để controller return 202 ngay lập tức
- **Tầng trong**: N task chunk chạy song song trên `ingestionTaskExecutor`

Nếu submit chunk từ chính thread controller rồi `allOf().join()` thì đã block đúng cái thread
đáng lẽ phải trả 202 về.

```java
// Controller
IngestionJob job = backfillService.createJob(request);   // INSERT, PENDING
backfillService.runAsync(job.getId(), request);          // trả về ngay, không chờ
return ResponseEntity.accepted().body(toResponse(job));
```

### B4 — Xử lý một chunk

```java
List<UsgsFeature> features = usgsClient.queryEvents(w.from(), w.to().minusSeconds(1), minMag);

List<CompletableFuture<SendResult<String, Object>>> futures = features.stream()
        .map(kafkaProducerService::sendRawEarthquake)
        .toList();

CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

return features.size();
```

`kafkaTemplate.send()` **không** gửi ngay — nó nhét message vào buffer rồi return, một thread
nền mới thực sự đẩy lên broker. `CompletableFuture` là **tờ biên nhận**: lát nữa hỏi lại xem
broker có nhận không. `join()` là đứng đợi câu trả lời.

Vì sao backfill phải đợi (realtime thì không):

1. `records_processed` phải đúng sự thật — broker từ chối 300/2400 mà job khoe 2400 là số vô nghĩa
2. Quan trọng hơn: không đợi thì task chunk return trong khi message còn nằm trong buffer.
   JVM tắt lúc đó là **mất trắng**, mà job đã kịp ghi SUCCESS

### B5 — Vượt trần 20k: chia đôi đệ quy

```java
private int fetchWindow(TimeWindow w, double minMag, int depth) {
    try {
        return publishAll(usgsClient.queryEvents(w.from(), w.to().minusSeconds(1), minMag));
    } catch (HttpClientErrorException.BadRequest e) {
        if (depth >= MAX_SPLIT_DEPTH || w.length().compareTo(Duration.ofHours(1)) <= 0) {
            throw e;                                  // không phải do vượt trần
        }
        int total = 0;
        for (TimeWindow half : w.halve()) {
            total += fetchWindow(half, minMag, depth + 1);
        }
        return total;
    }
}
```

Cái hay: không phải đoán trước chunk size cho từng ngưỡng magnitude. Để 30 ngày, tháng nào
đông quá thì tự chia 15 → 7 → 3. Sau này hạ `minMagnitude` xuống 1.0 cũng không phải sửa config.

Phải chặn đệ quy vô hạn (`MAX_SPLIT_DEPTH` ~10, hoặc cửa sổ nhỏ hơn 1 giờ) vì HTTP 400 cũng
có thể do request sai chứ không riêng gì vượt trần.

### B6 — Vòng đời job

```java
job.setStatus(JobStatus.RUNNING);
repository.save(job);

List<String> failedWindows = new ArrayList<>();
// ... chạy 80 chunk, chunk nào ném exception thì ghi vào failedWindows ...

if (failedWindows.isEmpty()) {
    job.setStatus(JobStatus.SUCCESS);
} else {
    job.setStatus(JobStatus.FAILED);
    job.setErrorMessage(failedWindows.size() + " of " + total + " chunks failed: "
            + String.join(", ", failedWindows));
}
repository.save(job);
```

Một chunk lỗi **không** được làm chết cả job — 79 chunk còn lại vẫn phải chạy xong.

### B7 — Tiến độ nhích dần

```java
jobRepository.addRecordsProcessed(jobId, count);   // sau MỖI chunk
```

Chỉ cập nhật lúc kết thúc thì suốt vài phút job trông như treo. Và vì 4 thread cùng ghi vào
một row, phải cộng dồn **trong SQL** (`records_processed = records_processed + :count`).
Đọc entity ra rồi `setRecordsProcessed(x + n)` rồi save sẽ mất số đếm khi hai chunk xong
cùng lúc.

### B8 — Chặn chạy trùng

```java
if (jobRepository.existsByStatus(JobStatus.RUNNING)) {
    throw new BackfillAlreadyRunningException();     // → 409 Conflict
}
```

Admin bấm nhầm hai lần, hoặc request timeout rồi bấm lại = 160 request tới một API miễn phí,
dễ ăn throttle. Dữ liệu vẫn đúng nhờ upsert, nên đây là chuyện lịch sự với USGS và tiết kiệm
thời gian, không phải chuyện toàn vẹn dữ liệu.

Chặn thô (có job RUNNING là từ chối, không so khoảng thời gian) là đủ cho một màn admin dùng một mình.

### B9 — Nối vào controller

3 endpoint trong `AdminIngestionController` hiện đều là stub:

| Endpoint | Việc |
|---|---|
| `POST /backfill` | validate → chặn trùng → tạo job → chạy nền → **202** + job id |
| `GET /jobs` | `findAllByOrderByCreatedAtDesc()` → list response |
| `GET /jobs/{id}` | `findById` → response, không thấy thì 404 |

`IngestionJobResponse` có sẵn rồi nhưng `status` đang là `String` — đổi sang `JobStatus`
cho khớp entity.

---

## Bốn cái bẫy

**1. Thread pool không nở như tưởng.** `ThreadPoolExecutor` chỉ tạo thread vượt `corePoolSize`
**khi hàng đợi đã đầy**. Cấu hình cũ (core 2, max 4, queue 100) với 80 task thì queue không bao
giờ đầy → vĩnh viễn chỉ 2 thread chạy. Đã sửa ở B1: `corePoolSize = 4`.

**2. `@Async` gọi trong cùng bean thì không async.** Spring chặn qua proxy; `this.method()` bỏ
qua proxy, chạy đồng bộ ngay trên thread hiện tại. Phải gọi xuyên qua bean khác.

**3. `@Transactional` không đi theo thread.** Transaction gắn với thread hiện tại. Task chạy trên
`ingestionTaskExecutor` không thừa hưởng transaction của thread gọi — mỗi chunk cần transaction riêng.

**4. Rejection policy mặc định là `AbortPolicy`** — queue đầy thì ném exception, mất task.
Đã đổi sang `CallerRunsPolicy` ở B1: chạy trên thread gọi, chậm lại chứ không mất việc.

## Thứ tự làm và cách kiểm chứng

| Bước | Kiểm chứng |
|---|---|
| B2 `TimeWindow` + `split` | unit test thuần, không cần Docker |
| B4 xử lý 1 chunk | gọi tay 1 cửa sổ nhỏ, xem message trong kafka-ui |
| B5 chia đôi đệ quy | ép lỗi bằng cửa sổ 1 năm + M≥1.0, xem log có chia nhỏ không |
| B6+B7 vòng đời job | `GET /jobs/{id}` thấy `records_processed` tăng dần, status chuyển đúng |
| B8 chặn trùng | bấm 2 lần liên tiếp, lần 2 phải nhận 409 |
| B9 chạy thật | `SELECT COUNT(*) FROM earthquakes` ≈ `records_processed` sau khi consumer đuổi kịp |

## Việc còn treo

- **T7 (lưu raw payload vào Mongo)** đang cân nhắc bỏ — không chặn T8. Nếu bỏ thì `jobId`
  trong `RawIngestionPayload` cũng bỏ theo.
- **`/api/admin/**` chưa được bảo vệ**: `SecurityConfig` đang `anyRequest().permitAll()`.
  Ai cũng gọi được endpoint backfill. Cần siết trước khi coi là xong.
