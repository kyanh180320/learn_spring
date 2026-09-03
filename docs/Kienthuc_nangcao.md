# 🚀 TỔNG HỢP CÁC KỸ THUẬT & KIẾN THỨC NÂNG CAO TRONG DỰ ÁN VIETLOTT SMS BACKEND
*(Vượt xa các thao tác CRUD cơ bản — Cẩm nang học tập kiến trúc & kỹ thuật Backend Enterprise)*

---

## 📌 MỤC LỤC
1. [Tổng Quan Kiến Trúc Tổng Thể (System Architecture & Hexagonal Design)](#1-tổng-quan-kiến-trúc-tổng-thể)
2. [Hệ Thống Fintech, Cổng Thanh Toán & Ngân Hàng (Fintech & Banking Integrations)](#2-hệ-thống-fintech-cổng-thanh-toán--ngân-hàng)
3. [Engine Đối Soát Tài Chính Đa Chiều (Financial Reconciliation System)](#3-engine-đối-soát-tài-chính-đa-chiều)
4. [Viễn Thông & Giao Thức Mạng Chuyên Sâu (Telecom, SMPP & SMS Routing)](#4-viễn-thông--giao-thức-mạng-chuyên-sâu)
5. [Hệ Thống Phân Tán, Cache & Xử Lý Đồng Thời (Distributed Systems & Redis Concurrency)](#5-hệ-thống-phân-tán-cache--xử-lý-đồng-thời)
6. [Message Broker & Kiến Trúc Bất Đồng Bộ (Kafka, RabbitMQ & Worker Queues)](#6-message-broker--kiến-trúc-bất-đồng-bộ)
7. [Bảo Mật Doanh Nghiệp, Chữ Ký Số & Mã Hóa (Enterprise Security & PKI)](#7-bảo-mật-doanh-nghiệp-chữ-ký-số--mã-hóa)
8. [Phân Quyền Động & Tự Động Quét Metadata Endpoint (Dynamic RBAC & Reflection)](#8-phân-quyền-động--tự-động-quét-metadata-endpoint)
9. [Tích Hợp eKYC & AI Computer Vision (OCR Căn Cước Công Dân)](#9-tích-hợp-ekyc--ai-computer-vision)
10. [Polyglot Persistence & Migration Dữ Liệu Lớn (Oracle + MySQL)](#10-polyglot-persistence--migration-dữ-liệu-lớn)
11. [Design Patterns Thực Chiến Áp Dụng Trong Codebase](#11-design-patterns-thực-chiến-áp-dụng-trong-codebase)
12. [Lộ Trình Đọc Code & Học Kỹ Năng Nâng Cao](#12-lộ-trình-đọc-code--học-kỹ-năng-nâng-cao)

---

## 1. TỔNG QUAN KIẾN TRÚC TỔNG THỂ

### 🏛️ Hexagonal Architecture (Ports and Adapters) & Clean Architecture
Dự án không đi theo mô hình 3 tầng truyền thống (Controller - Service - Repository) gộp chung, mà tách biệt nghiêm ngặt theo mô hình **Ports & Adapters**:
- `*-domain`: Chứa Business Logic thuần túy, Core Entities, Use Cases, và định nghĩa các Port (`CheckRateLimitPort`, `PaymentPort`, `SmsPort`...). Lớp này **không phụ thuộc** vào framework bên ngoài hay database cụ thể.
- `*-adapter`: Chứa các triển khai cụ thể (Implementation) của Ports (Ví dụ: `RedisCheckRateLimitAdapter`, `KafkaEventPublisherAdapter`, `MomoPaymentAdapter`...).
- `*-api / application`: Lớp giao tiếp với bên ngoài (REST Controller, CLI, Consumer).

> **💡 Bạn học được gì:**
> - Cách giảm thiểu phụ thuộc (Decoupling) tối đa: có thể thay đổi nhà cung cấp SMS, cổng thanh toán hoặc database mà không ảnh hưởng tới core business.
> - Tư duy thiết kế phần mềm độc lập framework (Framework-independent architecture).

### 📦 Multi-Module Maven Enterprise (> 190 modules)
Quản lý một hệ sinh thái lớn với hàng trăm micro-modules phụ thuộc lẫn nhau, tái sử dụng thư viện chung (`common`, `common-domain`, `entity`, `mysqlEntity`).

---

## 2. HỆ THỐNG FINTECH, CỔNG THANH TOÁN & NGÂN HÀNG

Dự án này là một tài liệu học tập toàn diện về tích hợp hệ sinh thái tài chính điện tử tại Việt Nam:

### 💳 Tích Hợp Đa Cổng Thanh Toán & Ví Điện Tử
- **Các module thực tế:** `ext-momo-payment`, `ext-zalopay`, `ext-viettelpay`, `ext-vnpayment`, `ext-shopee-pay`, `ext-appota-pay`, `ext-moca-client`, `ext-mobifone-money`...
- **Kỹ thuật chuyên sâu:**
  - Tạo URL thanh toán (Payment Gateway Redirect / App-to-App Deeplink).
  - Ký chữ ký số giao dịch bằng **HMAC-SHA256 / RSA** với Public/Private Key.
  - Xử lý **IPN (Instant Payment Notification) Webhook** bất đồng bộ với tính chất **Idempotency** (chống xử lý trùng lặp giao dịch).
  - Tự động truy vấn trạng thái đơn hàng (Query Order Status / Polling) khi timeout.

### 🏦 Tích Hợp Ngân Hàng & Tài Khoản Ảo (VAN - Virtual Account Number)
- **Các module:** `ext-van-identification`, `bidv-connect`, `pvcombank-connect`, `ext-vietabank`, `ext-seabank`, `ext-shinhan-bank`, `ext-vpbank-payment`, `woori-bank-connect`.
- **Kỹ thuật chuyên sâu:**
  - **Virtual Account (Số tài khoản định danh):** Cơ chế sinh số tài khoản ngân hàng ảo cho từng user hoặc từng đơn hàng; khi tiền vào ngân hàng, webhook của ngân hàng gọi về hệ thống `ext-van-identification` để tự động khớp tiền và nạp ví/xuất vé.
  - **Pay on Behalf (Chi hộ / Thu hộ):** Tự động trả thưởng trực tiếp vào số tài khoản ngân hàng của khách hàng (`ext-viettel-payonbehalf`).

---

## 3. ENGINE ĐỐI SOÁT TÀI CHÍNH ĐA CHIỀU (RECONCILIATION)

Trong các hệ thống tài chính/thương mại lớn, đối soát (Reconciliation) là bài toán cực kỳ hóc búa để phát hiện lệch số liệu giữa các bên.

- **Các module:** `customer-account.reconciliation.daily`, `reward.reconciliation.daily`, `recon-ticket-sale`, `reconciliation-beneficiary`, `reconciliation-hmdt`, `vnptmoney.reconciliation`...
- **Kỹ thuật học được:**
  - **Đối soát 3 bên (Three-way Matching):** Hệ thống Core Vietlott ⟷ Cổng thanh toán/Ngân hàng ⟷ Hệ sinh thái Đại lý.
  - **Xử lý File dữ liệu lớn qua SFTP/FTP:** Tự động kết nối FTP (`ext-ftp-vietlott-client`), tải các file log giao dịch định dạng `.csv`, `.txt`, `.xlsx` dung lượng lớn từ đối tác hàng đêm.
  - **Batch Processing & Pipeline:** Đọc streaming file (tránh Out-Of-Memory), parse dữ liệu, đối soát từng dòng và sinh báo cáo lệch (Discrepancy Report) cùng file điều chỉnh (Adjustment).

---

## 4. VIỄN THÔNG & GIAO THỨC MẠNG CHUYÊN SÂU

Một điểm đặc biệt hiếm thấy ở các web thông thường là tầng xử lý viễn thông giao thức thấp:

### 📡 Giao thức SMPP (Short Message Peer-to-Peer Protocol)
- **Module:** `smsgw-smpp`
- **Kỹ thuật học được:**
  - Kết nối trực tiếp qua Socket TCP đến SMSC (Short Message Service Center) của các nhà mạng (Viettel, Mobifone, Vinaphone).
  - Tự cài đặt và xử lý các gói tin chuẩn viễn thông: **PDU (Protocol Data Unit)** (`submit_sm`, `deliver_sm`, `enquire_link` giữ kết nối keep-alive).
  - **Ghép tin nhắn dài (Multipart / Long SMS Reassembly):** Khi khách hàng gửi SMS mua nhiều vé vượt quá 160 ký tự, SMS bị chia nhỏ qua UDH (User Data Header). `ManageMsgLongQueue` và `LongSmsDetect` chịu trách nhiệm giữ các mảnh tin nhắn trong bộ nhớ/queue và ghép lại thành một lệnh mua vé hoàn chỉnh.

### 🔄 Xử lý Luồng MO (Mobile Originated) & MT (Mobile Terminated)
- **Module:** `ticket-sms-handler`, `sms-gateway`, `sms-helper-adapter`
- Xử lý SMS nhận từ khách hàng (MO) -> Phân tích cú pháp đặt vé -> Gọi Core Engine quay số -> Trả SMS phản hồi kết quả (MT).
- Tích hợp **RBM (RCS Business Messaging)** - công nghệ tin nhắn đa phương tiện thế hệ mới thay thế SMS truyền thống (`vietlott.rbm-api`).

---

## 5. HỆ THỐNG PHÂN TÁN, CACHE & XỬ LÝ ĐỒNG THỜI

### 🔒 Distributed Lock (Khóa phân tán)
- **File:** [DistributeLockServices.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ext-redis/src/main/java/com/vietlott/redis/lock/DistributeLockServices.java), [RedissonPoolImpl.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ext-redis/src/main/java/com/vietlott/redis/lock/RedissonPoolImpl.java)
- **Kỹ thuật:** Sử dụng **Redisson** để khóa tài nguyên (khóa theo User ID, khóa theo Kỳ quay số, khóa theo Giao dịch nạp tiền) nhằm tránh **Race Condition**, bảo đảm 1 người không thể bấm mua vé hoặc rút tiền 2 lần cùng 1 mili-giây.

### ⏱️ Token Bucket Rate Limiting bằng Redis Lua Script
- **File:** [RedisCheckRateLimitAdapter.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/rate-limit-adapter/src/main/java/com/vietlott/adapter/ratelimit/RedisCheckRateLimitAdapter.java)
- **Kỹ thuật:**
  - Nạp mã nguồn Lua Script (`rateLimit.lua`) vào Redis engine bằng SHA (`jedis.scriptLoad`).
  - Thực thi thuật toán **Token Bucket** hoàn toàn trên RAM của Redis một cách nguyên tử (Atomicity - không lo race condition đa thread), bảo vệ API khỏi nguy cơ spam / tấn công từ chối dịch vụ (DDoS).

### ⚡ WebSocket Realtime & Redis Pub/Sub
- **File:** [PushWebsocketNotifyUserService.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ext-redis/src/main/java/com/vietlott/redis/websocket/service/PushWebsocketNotifyUserService.java)
- Bắn thông báo biến động số dư, trúng thưởng cho hàng trăm ngàn người dùng realtime qua cơ chế Redis Queue / PubSub kết nối WebSocket server.

---

## 6. MESSAGE BROKER & KIẾN TRÚC BẤT ĐỒNG BỘ

### 📨 Apache Kafka & RabbitMQ Đa Mô Hình
- **Module:** `ext-kafka`, `ext-rabbit`, `ticket-sms-handler`
- **Kỹ thuật chuyên sâu:**
  - **Kafka Consumer/Producer Config:** Phân vùng (Partitions), Consumer Groups, Acknowledgement mode để xử lý luồng sự kiện trúng thưởng, luồng token BGT (`BgtToken`).
  - **RabbitMQ:** Sử dụng cho các hàng đợi nhiệm vụ yêu cầu định tuyến mềm dẻo (Direct, Topic Exchange, Dead Letter Queue - DLQ cho các tin nhắn lỗi cần thử lại).

### ⚙️ Worker Tasks & Cloud Tasks
- **Module:** `worker`, `worker-saleLimit`, `cloud-task`
- Giới hạn hạn mức bán vé theo từng đại lý trong ngày (`worker-saleLimit`), tự động chạy các job định kỳ tính toán doanh thu, hết hạn giải thưởng (`WinningResultExpiredTask`).

---

## 7. BẢO MẬT DOANH NGHIỆP, CHỮ KÝ SỐ & MÃ HÓA

### ✍️ Chữ Ký Số & Chứng Thư Số Doanh Nghiệp (PKI / VNPT-CA / HSM)
- **Module:** `ext-vnpt-ca`
- **File:** [SignatureService.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ext-vnpt-ca/src/main/java/com/vietlott/vnptca/service/SignatureService.java), [ExcelSignerService.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ext-vnpt-ca/src/main/java/com/vietlott/vnptca/service/ExcelSignerService.java)
- **Kỹ thuật học được:**
  - Tích hợp chuẩn **PKI (Public Key Infrastructure)** qua API của VNPT-CA.
  - Ký số trực tiếp vào cấu trúc XML của file Excel (`Apache POI`) để xuất các báo cáo tài chính/biên bản đối soát có giá trị pháp lý không thể chỉnh sửa.

### 🔐 Secret Manager & Mã Hóa Biến Môi Trường Động
- **Module:** `util-secret-manager-client`, `util-secure-env-properties`
- Thay vì lưu password database, private key thanh toán dạng plaintext trong file `.properties`/`.yml`, hệ thống tự động fetch và giải mã secret từ **Google Cloud Secret Manager / Vault** lúc khởi động.

### 🛡️ Bảo Mật Kênh Mobile & Anti-Bot
- Mã hóa payload truyền tải giữa Mobile App và Server (AES-256 + RSA Key Exchange).
- Tích hợp **Google reCAPTCHA Enterprise** (`ext-google-captcha`) chống bot quét vé.

---

## 8. PHÂN QUYỀN ĐỘNG & TỰ ĐỘNG QUÉT METADATA ENDPOINT

- **File tiêu biểu:** [ApiConfig.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/customer-service/src/main/java/com/vietlott/customerService/config/ApiConfig.java)
- **Kỹ thuật:**
  - Triển khai `ApplicationListener<ContextRefreshedEvent>` để bắt sự kiện Spring Context sẵn sàng.
  - Sử dụng **Reflection & Spring HandlerMapping** (`RequestMappingHandlerMapping`) để tự động scan toàn bộ Controller, trích xuất danh sách API URL, Method, Swagger `@Tag`, `@Operation`.
  - Tự động đồng bộ cây phân quyền (Permissions / API Groups) vào Database mà **không cần tạo migration tay**, giúp hệ thống phân quyền RBAC luôn đồng bộ 100% với code.

---

## 9. TÍCH HỢP eKYC & AI COMPUTER VISION

- **Module:** `ocr`
- **File:** [VietlottOcrService.java](file:///Users/kyanh/Documents/GitHub/vietlottsms_backend/backend-spring/vietlott-sms/ocr/src/main/java/com/vietlott/ocr/service/VietlottOcrService.java)
- **Kỹ thuật:**
  - Nhận diện ký tự quang học (OCR) trên ảnh Căn cước công dân (CCCD) và CCCD gắn chip.
  - Tự động trích xuất: Họ tên, Số CCCD, Ngày sinh, Địa chỉ thường trú, Ngày cấp.
  - Tích hợp quy trình xác minh danh tính người chơi (eKYC) hợp chuẩn pháp lý xổ số điện toán.

---

## 10. POLYGLOT PERSISTENCE & MIGRATION DỮ LIỆU LỚN

- **Module:** `entity`, `mysqlEntity`, `migration-oracle-data`
- **Kỹ thuật:**
  - **Đa hệ cơ sở dữ liệu song song (Oracle + MySQL):** Hệ thống cũ chạy Oracle DB, hệ thống mới chuyển dịch dần sang MySQL.
  - Quản lý **Multi-Datasource** và Routing Datasource.
  - Pipeline ETL chuyển đổi dữ liệu lịch sử từ Oracle sang MySQL (`migration-oracle-data`) bảo toàn tính toàn vẹn khóa ngoại và trạng thái vé.

---

## 11. DESIGN PATTERNS THỰC CHIẾN ÁP DỤNG TRONG CODEBASE

| Pattern | Vị trí áp dụng trong dự án | Mục đích thực tế |
| :--- | :--- | :--- |
| **Ports & Adapters (Hexagonal)** | Toàn bộ các module `*-domain` & `*-adapter` | Tách biệt logic nghiệp vụ khỏi database/framework |
| **Strategy Pattern** | Payment Gateway, SMS Gateway Providers | Đổi nhà cung cấp thanh toán/SMS linh hoạt theo cấu hình |
| **Adapter Pattern** | Các module `ext-*` | Bọc API đối tác (MoMo, ZaloPay, BIDV) thành interface nội bộ thống nhất |
| **Template Method Pattern** | Tác vụ đối soát (`ReconciliationTask`) | Khung sườn quy trình: Đọc file -> Đối soát -> Xuất báo cáo |
| **Distributed Lock Pattern** | `DistributeLockServices` | Đảm bảo tính nhất quán dữ liệu khi xử lý giao dịch đồng thời |
| **Outbox / Event Queue Pattern** | `PushEventProcessQueueService`, Redis Queue | Đảm bảo không mất mát sự kiện khi hệ thống gặp sự cố |
| **Factory Pattern** | Quản lý kết nối Redis / Kafka / SMPP Sessions | Khởi tạo và quản lý vòng đời connection pool tối ưu RAM |

---

## 12. LỘ TRÌNH ĐỌC CODE & HỌC KỸ NĂNG NÂNG CAO

Nếu bạn muốn nâng trình độ từ **Junior/Mid CRUD** lên **Senior Backend / Architect**, hãy học theo lộ trình sau:

```
[Tuần 1: Kiến trúc & Concurrency]
  └── Đọc: rate-limit-adapter (Token Bucket Lua script)
  └── Đọc: ext-redis (Redisson Distributed Lock & Redis Queue)

[Tuần 2: Tích hợp Cổng thanh toán & VAN]
  └── Đọc: ext-momo-payment / ext-zalopay (Chữ ký HMAC/RSA, IPN Webhook)
  └── Đọc: ext-van-identification (Tài khoản ảo ngân hàng)

[Tuần 3: Giao thức thấp & Message Queues]
  └── Đọc: smsgw-smpp (Giao thức TCP viễn thông & ghép tin dài PDU)
  └── Đọc: ext-kafka, ticket-sms-handler (Kafka & RabbitMQ worker)

[Tuần 4: Nghiệp vụ tài chính nâng cao & Chữ ký số]
  └── Đọc: ext-vnpt-ca (Ký số tài liệu POI Excel PKI)
  └── Đọc: customer-account.reconciliation.daily (Engine đối soát dòng tiền)

[Tuần 5: Reflection & Spring Internals]
  └── Đọc: customer-service/src/.../ApiConfig.java (Tự động scan endpoint & RBAC)
```

---
*Tài liệu được tổng hợp từ cấu trúc mã nguồn thực tế của dự án Vietlott SMS Backend.*

