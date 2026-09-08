# Earthquake Monitoring Platform

Project ôn tập: ETL pipeline (Kafka) + Spring Security (JWT) + SQL Server indexing/exec plan + Redis + RabbitMQ + WebSocket + React.

**Trạng thái: skeleton only.** Toàn bộ business logic (JWT generate/verify, security rules, CRUD, ingestion, alert, report, form React, route guard...) cố tình để `TODO` — đây là project luyện tập, logic sẽ được viết dần cùng nhau.

## Services

- `auth-service` (port 8081) — đăng ký/đăng nhập/JWT, SQL Server (`auth_db`) + Redis (refresh token).
- `core-service` (port 8082) — ingestion (USGS earthquake API) qua Kafka, query/analytics, alert (RabbitMQ), report async, WebSocket. SQL Server (`core_db`) + MongoDB (`core_raw_db`) + Redis + Kafka + RabbitMQ.
- `frontend` (port 3000) — React SPA, gọi cả 2 service trên.

## Hạ tầng (docker-compose)

| Service | Port | Ghi chú |
|---|---|---|
| sqlserver | 1433 | 1 instance, 2 database: `auth_db`, `core_db` |
| mongodb | 27017 | `core_raw_db` |
| redis | 6379 | cache + refresh token + pub/sub |
| kafka | 9092 | KRaft mode, không cần Zookeeper |
| kafka-ui | 8090 | xem topic/message trực quan |
| rabbitmq | 5672 / 15672 (UI) | user/pass mặc định `guest/guest` |

## Chạy

```bash
cp .env.example .env   # sửa MSSQL_SA_PASSWORD, JWT_SECRET
docker compose up -d --build
```

Đợi `sqlserver-init` chạy xong (tạo 2 database) trước khi auth-service/core-service start — đã khai báo `depends_on` trong compose.

## Ghi chú thiết kế

- JWT dùng HS256, secret share qua env `JWT_SECRET` giữa 2 service.
- `user_id` trong core_db (bảng `alert_subscriptions`, `report_requests`) là tham chiếu logic sang `auth_db.users`, không có FK vật lý (khác database/service).
- Schema SQL Server quản lý bằng Flyway (`src/main/resources/db/migration`) ở mỗi service.
