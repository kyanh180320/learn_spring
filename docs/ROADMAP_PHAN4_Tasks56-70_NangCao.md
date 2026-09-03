# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN (CHUẨN SINGLE RESPONSIBILITY & KHUNG 5D)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn 20%**: Mỗi bước tiến lên một nấc thang tự nhiên, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---

## 🔒 GIAI ĐOẠN 10: Xử Lý Phân Tán, Khóa Redisson & Realtime (Tasks 56 - 59)

### 📌 Task 56: Khóa Phân Tán (Distributed Lock) Với Redisson
* **Hành động code:** Tích hợp `redisson-spring-boot-starter`, viết service/aspect `@DistributedLock(key = "#orderId", leaseTime = 5s, waitTime = 2s)`.
* **Mục tiêu duy nhất:** Bảo vệ tài nguyên dùng chung khi ứng dụng chạy trên nhiều cụm server (Multi-instance Cluster) mà `synchronized` của Java bị vô hiệu hóa.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao từ khóa `synchronized` và `ReentrantLock` của Java chỉ có tác dụng trong 1 JVM mà bất lực khi app chạy 3 server đằng sau Load Balancer?
  2. 🔬 *[Cơ chế Redisson & Lua Script]:* Redisson sử dụng lệnh Lua Script nguyên tử (`SET key value NX PX`) và cơ chế Watchdog (tự động gia hạn khóa) khi tác vụ chưa xử lý xong ra sao?
  3. ⚠️ *[Rủi ro Deadlock & Lease Time]:* Nếu server đang giữ lock bị mất điện/crash đột ngột, cấu hình `leaseTime` (thời gian tự giải phóng khóa) cứu toàn bộ hệ thống khỏi bị treo vĩnh viễn thế nào?
  4. ⚖️ *[So sánh]:* Pessimistic Lock DB (`SELECT FOR UPDATE`) vs Redis Distributed Lock. Khi nào nên dùng loại nào (Ví dụ: Giữ chỗ vé xem phim / flash sale trong 5 phút)?
  5. 🏢 *[Thực tế]:* Viết test đa luồng giả lập 2 instance cùng tranh mua 1 vé cuối cùng qua Redisson Lock.
* **Từ khóa:** `Redisson Distributed Lock`, `Redis Lua Script Atomicity`, `Watchdog Lock Renewal`, `SET NX PX`.

---

### 📌 Task 57: Rate Limiting Phân Tán Bằng Redis Lua Script Nguyên Tử
* **Hành động code:** Viết file script `rate_limiter.lua` và tích hợp `StringRedisTemplate.execute(...)` để giới hạn tần suất gọi API phân tán.
* **Mục tiêu duy nhất:** Thực thi thuật toán Rate Limiting (Token Bucket) hoàn toàn trên RAM của Redis với tính nguyên tử 100%.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Lua Script]:* Tại sao thực thi script Lua bên trong Redis đảm bảo tính Atomic (Nguyên tử) mà không xảy ra Race Condition giữa nhiều luồng đọc và ghi số lượng token?
  2. ⚠️ *[Vấn đề Round-trip Network]:* Nếu viết bằng code Java: (1) `GET count` $\rightarrow$ (2) `if (count > limit)` $\rightarrow$ (3) `INCR count`, tại sao vừa chậm (tốn 3 lượt mạng) vừa bị sai lệch dữ liệu đồng thời?
  3. ⚖️ *[So sánh]:* Thuật toán **Fixed Window Counter** (Dễ bị đột biến gấp đôi ở ranh giới) vs **Sliding Window Log / Token Bucket**.
  4. 🔄 *[Đánh đổi]:* Đẩy logic vào Redis Lua Script giúp tốc độ dưới 1ms nhưng nhược điểm về khả năng debug và bảo trì script Lua là gì?
  5. 🏢 *[Thực tế]:* Cấu hình nạp mã SHA của script (`scriptLoad`) để Redis tái sử dụng mà không cần truyền toàn bộ chuỗi script qua mỗi request.
* **Từ khóa:** `Redis Lua Scripting`, `Token Bucket in Redis`, `Atomic Rate Limiting`, `Sliding Window Rate Limiter`.

---

### 📌 Task 58: Thông Báo Biến Động Số Dư Realtime Qua WebSocket & Redis Pub/Sub
* **Hành động code:** Cấu hình Spring WebSocket STOMP kết hợp Redis Message Listener (`RedisMessageListenerContainer`).
* **Mục tiêu duy nhất:** Bắn thông báo realtime đến đúng trình duyệt/mobile app của User khi hệ thống chạy đa server.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất WebSocket]:* WebSocket duy trì kết nối Full-Duplex TCP 2 chiều khác gì với cơ chế Short-Polling (Client liên tục gửi request 2 giây/lần)?
  2. ⚠️ *[Bài toán Đa Server]:* User A kết nối WebSocket vào Server 1, nhưng Webhook cộng tiền lại kích hoạt ở Server 2. Làm sao Server 2 báo cho User A nếu không có Redis Pub/Sub làm cầu nối trung gian?
  3. ⚖️ *[So sánh]:* STOMP protocol trên nền WebSocket vs Server-Sent Events (SSE). Khi nào SSE là đủ (chỉ nhận thông báo 1 chiều từ server)?
  4. 🔄 *[Đánh đổi]:* Giữ hàng triệu kết nối WebSocket chiếm bao nhiêu File Descriptor và RAM của server? Giải pháp mở rộng (Scale-out)?
  5. 🏢 *[Thực tế]:* Gọi API nạp tiền và quan sát trình duyệt nhận ngay thông báo biến động số dư trong vòng dưới 10ms.
* **Từ khóa:** `Spring WebSocket STOMP`, `Redis Pub/Sub Channel`, `Multi-Node Realtime Notification`, `Server-Sent Events (SSE) vs WebSocket`.

---

### 📌 Task 59: (MỚI — Task Đệm) Tổng Hợp Redis Nâng Cao — Pipeline, TTL Strategy & Monitoring
* **Hành động code:** Viết service dùng `RedisTemplate.executePipelined()` để gom nhiều lệnh Redis thành 1 round-trip duy nhất, cấu hình TTL động theo loại dữ liệu, xem metrics qua `redis-cli INFO stats`.
* **Mục tiêu duy nhất:** Tối ưu hiệu năng Redis và nắm vững chiến lược TTL trước khi bước vào Kafka và Fintech phức tạp hơn.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Pipeline]:* `executePipelined()` gom $N$ lệnh Redis thành 1 round-trip duy nhất thay vì $N$ round-trip riêng lẻ — tiết kiệm latency mạng thế nào khi đọc 100 key cùng lúc?
  2. ⚠️ *[Bẫy lỗi Memory Leak]:* Key không có TTL tích lũy vĩnh viễn trong RAM — dùng `redis-cli --scan --pattern "*"` phối hợp kiểm tra TTL để phát hiện key nào đang không có TTL ra sao?
  3. ⚖️ *[So sánh TTL Strategy]:* TTL cố định (30 phút) vs TTL động (Session = 30 phút, OTP = 5 phút, Distributed Lock = 5 giây). Nguyên tắc nào quyết định TTL phù hợp?
  4. 🔄 *[Đánh đổi]:* **Redis Cluster** vs **Redis Sentinel**. Khi nào cần Cluster (Sharding dữ liệu), khi nào Sentinel (Chỉ cần High Availability)?
  5. 🏢 *[Thực tế]:* Dùng lệnh `redis-cli INFO stats` đọc `keyspace_hits` và `keyspace_misses` để tính Cache Hit Rate của hệ thống đang chạy.
* **Từ khóa:** `Redis Pipeline executePipelined`, `TTL Strategy by Data Type`, `Redis Cluster vs Sentinel`, `Cache Hit Rate Monitoring`.

---

## 💳 GIAI ĐOẠN 11: Fintech, Cổng Thanh Toán & Idempotency (Tasks 60 - 65)

> 💡 **Lý do thứ tự thiết kế:** Học Payment Gateway $\rightarrow$ IPN Webhook trước để có ngữ cảnh thực tế, sau đó mới học Idempotency vì đó chính là giải pháp cốt lõi cho bài toán Webhook bị bắn trùng lặp.

### 📌 Task 60: Tích Hợp Cổng Thanh Toán MoMo / VNPay & Ký Chữ Ký Số HMAC-SHA256
* **Hành động code:** Viết service tạo yêu cầu thanh toán (Redirect URL) kèm thuật toán sinh chữ ký `signature = HMAC-SHA256(rawHash, secretKey)`.
* **Mục tiêu duy nhất:** Nắm vững cấu trúc payload thanh toán chuẩn doanh nghiệp và quy tắc bảo toàn dữ liệu bằng chữ ký mật mã.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Chữ ký số]:* Tại sao phải sắp xếp các trường theo bảng chữ cái alphabet (`accessKey=...&amount=...&orderId=...`) trước khi băm chữ ký?
  2. ⚠️ *[Lỗ hổng Giả mạo Tham số (Parameter Tampering)]:* Nếu không có chữ ký số, Hacker chặn gói tin đổi `amount=1000000` thành `amount=1000` rồi thanh toán thì sao?
  3. ⚖️ *[So sánh]:* **HMAC-SHA256** (Khóa đối xứng - Cả 2 bên giữ chung Secret Key) vs **RSA / PKI** (Khóa bất đối xứng - Ký bằng Private Key, kiểm tra bằng Public Key).
  4. 🔄 *[Đánh đổi]:* HMAC tính toán siêu nhanh nhưng nếu làm lộ `secretKey` thì bên kia có thể tự sinh chữ ký giả mạo.
  5. 🏢 *[Thực tế]:* Tạo URL thanh toán MoMo/VNPay Sandbox, click mở giao diện thanh toán quét mã QR thành công.
* **Từ khóa:** `Payment Gateway Integration`, `HMAC-SHA256 Signature`, `Alphabetical Canonical String`, `Parameter Tampering Prevention`.

---

### 📌 Task 61: Xử Lý IPN (Instant Payment Notification) Webhook & Chống Giả Mạo
* **Hành động code:** Endpoint `POST /api/v1/payments/momo/ipn` nhận thông báo từ MoMo, xác thực chữ ký trả về và cập nhật trạng thái đơn hàng.
* **Mục tiêu duy nhất:** Xử lý Webhook an toàn, xác minh nguồn gốc và đảm bảo tính chính xác tuyệt đối của số tiền thực trả.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất IPN]:* Tại sao không bao giờ được tin tưởng dữ liệu redirect của trình duyệt người dùng (Return URL) để gạch nợ đơn hàng mà bắt buộc phải dựa vào IPN Server-to-Server?
  2. ⚠️ *[Bẫy lỗi Kiểm tra Số tiền]:* Tại sao sau khi verify chữ ký thành công, **BẮT BUỘC PHẢI SO SÁNH** `ipn.amount == order.amount` trong DB trước khi cập nhật thành công?
  3. 🔬 *[Cơ chế Retry của Cổng thanh toán]:* Nếu server bị timeout không trả lời `204 No Content`, cổng thanh toán sẽ retry gửi lại IPN theo thuật toán Exponential Backoff thế nào?
  4. ⚖️ *[Bài toán nảy sinh]:* MoMo bắn cùng 1 IPN 3 lần do mạng không ổn định $\rightarrow$ Đơn hàng bị cộng tiền 3 lần. Đây chính xác là bài toán sẽ giải quyết ở Task 62.
  5. 🏢 *[Thực tế]:* Sử dụng ngrok tạo tunnel nhận webhook IPN từ MoMo/VNPay Sandbox và gạch nợ đơn hàng tự động.
* **Từ khóa:** `IPN (Instant Payment Notification)`, `Server-to-Server Webhook`, `Return URL vs IPN Security`, `Webhook Signature Verification`.

---

### 📌 Task 62: Xây Dựng Engine Xử Lý Yêu Cầu Đơn Định (Idempotency Key Engine)
* **Hành động code:** Tạo `@Idempotent` annotation và Interceptor/Filter kiểm tra Header `Idempotency-Key` lưu trong Redis.
* **Mục tiêu duy nhất:** Đảm bảo khi MoMo bắn IPN trùng 3 lần hoặc Client bấm thanh toán 2 lần do lag mạng, server chỉ xử lý đúng 1 lần duy nhất.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Idempotency]:* Tính đơn định (Idempotent) trong HTTP là gì? Tại sao các phương thức `GET`, `PUT`, `DELETE` mặc định là Idempotent còn `POST` thì không?
  2. ⚠️ *[Kịch bản từ Task 61]:* MoMo bắn IPN lần 1 $\rightarrow$ Server đang xử lý thì mạng chập chờn $\rightarrow$ MoMo bắn IPN lần 2 $\rightarrow$ Nếu không có Idempotency Key thì tiền bị cộng 2 lần, đơn hàng thành công 2 lần.
  3. 🔬 *[Cơ chế Trạng thái 3 Bước]:* (1) Lưu Key trạng thái `PROCESSING` $\rightarrow$ (2) Thực thi logic $\rightarrow$ (3) Cập nhật kết quả JSON kèm `COMPLETED` vào Redis. Nếu request thứ 2 đến trong lúc đang `PROCESSING` thì trả về mã gì (`409 Conflict`)?
  4. ⚖️ *[Thời gian sống TTL]:* `Idempotency-Key` nên lưu trong Redis bao lâu (ví dụ: 24 - 48 giờ) và tại sao không nên lưu vĩnh viễn?
  5. 🏢 *[Thực tế]:* Mô phỏng Postman gửi 2 request trùng `Idempotency-Key: pay_abc123` trong 100ms: Request 2 nhận ngay kết quả của Request 1 mà không chạy lại logic nghiệp vụ.
* **Từ khóa:** `Idempotency Key Pattern`, `Safe Retry in Distributed Systems`, `HTTP 409 for In-flight Requests`, `Stripe-style Idempotency`.

---

### 📌 Task 63: Tích Hợp Tài Khoản Định Danh (Virtual Account / VietQR Chuyển Khoản Tự Động)
* **Hành động code:** Tạo mã VietQR động chứa nội dung chuyển khoản định danh (`DH123456`) và endpoint Webhook đón biến động số dư ngân hàng (SeABank/BIDV/Casso).
* **Mục tiêu duy nhất:** Tự động hóa hoàn toàn luồng chuyển khoản ngân hàng truyền thống thành luồng gạch nợ tự động không cần can thiệp tay.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Virtual Account]:* Tài khoản ảo (VAN) hoạt động ra sao: Ngân hàng cấp 1 đầu số mẹ, hệ sinh thái sinh hàng triệu số tài khoản con gắn với từng User/Đơn hàng?
  2. ⚠️ *[Bẫy lỗi Cú pháp Chuyển khoản]:* Người dùng chuyển khoản quên ghi mã đơn hàng hoặc viết sai cú pháp (`DH 123456` thành `D H 123456`), kiến trúc xử lý "Dòng tiền treo / Cần tra soát" được thiết kế ra sao?
  3. ⚖️ *[So sánh]:* Quét mã VietQR động (Số tiền cố định) vs VietQR tĩnh (Khách tự gõ tiền).
  4. 🔄 *[Đánh đổi]:* Tự động hóa qua Webhook ngân hàng giúp trải nghiệm tức thì nhưng đòi hỏi cơ chế bảo vệ chống **Replay Attack** (Tấn công gửi lại gói tin cũ) nghiêm ngặt.
  5. 🏢 *[Thực tế]:* Xử lý Regex bóc tách mã đơn hàng từ nội dung giao dịch ngân hàng và tự động mở khóa đơn hàng.
* **Từ khóa:** `Virtual Account Number (VAN)`, `VietQR Dynamic Payload`, `Bank Transfer Auto Reconciliation`, `Replay Attack Defense`.

---

### 📌 Task 64: (MỚI — Task Đệm) Giới Thiệu Hexagonal Architecture Qua Bài Toán Payment
* **Hành động code:** Tách module Payment hiện tại thành 3 package: `domain` (interface `PaymentPort`, business logic thuần Java), `adapter` (`MomoAdapter`, `VnPayAdapter`), `application` (Use Case).
* **Mục tiêu duy nhất:** Trực tiếp thấy lý do tồn tại của Hexagonal ngay trên code vừa viết ở Task 60-63 — không phải học lý thuyết trừu tượng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Nhìn lại code Task 60 — `OrderService` đang gọi thẳng `MomoPaymentService`. Nếu MoMo tăng phí và phải chuyển sang VNPay, phải sửa bao nhiêu chỗ trong `OrderService`?
  2. 🔬 *[Quy tắc Dependency Inversion]:* Domain định nghĩa interface `PaymentPort`. Adapter `MomoPaymentAdapter` implement interface đó. Tại sao chiều phụ thuộc bây giờ ngược lại so với trước?
  3. ⚠️ *[Quy tắc Domain thuần]:* Lớp domain **KHÔNG ĐƯỢC** chứa bất kỳ annotation Spring hay Hibernate nào (`@Service`, `@Entity`, `@Autowired`). Viết Unit Test cho Domain mà không cần khởi động Spring Context ra sao?
  4. ⚖️ *[So sánh]:* Mô hình 3 tầng truyền thống (Service phụ thuộc trực tiếp vào Adapter) vs **Hexagonal** (Domain không biết Adapter tồn tại).
  5. 🏢 *[Thực tế]:* Viết thêm `ZaloPayPaymentAdapter` mới mà không chạm vào 1 dòng code nào trong domain — đây là biểu hiện của Open/Closed Principle thực tế.
* **Từ khóa:** `Hexagonal Architecture Introduction`, `Dependency Inversion Principle`, `Domain Independence`, `PaymentPort Interface`.

---

### 📌 Task 65: Áp Dụng Strategy & Adapter Pattern Thiết Kế Hệ Thống Đa Cổng Thanh Toán
* **Hành động code:** Tạo `PaymentStrategyFactory` quản lý `Map<PaymentMethod, PaymentPort>` — Spring tự động inject danh sách tất cả Adapter vào Factory.
* **Mục tiêu duy nhất:** Mở rộng thêm cổng thanh toán mới mà không cần sửa 1 dòng code nghiệp vụ lõi (tuân thủ triệt để Open/Closed Principle).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Strategy Pattern]:* `PaymentStrategyFactory` nạp danh sách `Map<PaymentMethod, PaymentPort>` thông qua Spring DI tự động như thế nào (inject `List<PaymentPort>` rồi tự build Map)?
  2. ⚠️ *[Rủi ro Vendor Lock-in]:* Nếu code trực tiếp API của MoMo vào `OrderService`, khi MoMo tăng phí hoặc gặp sự cố thì việc chuyển sang VNPay mất bao nhiêu công sức so với Task 64?
  3. ⚖️ *[So sánh]:* **Adapter Pattern** (Chuyển đổi giao diện SDK MoMo/VNPay thành DTO chuẩn nội bộ) vs **Strategy Pattern** (Lựa chọn thuật toán thanh toán lúc Runtime).
  4. 🔄 *[Đánh đổi]:* Việc bọc qua nhiều tầng Interface làm tăng số lượng class nhưng đem lại khả năng Switch cổng thanh toán động qua cấu hình DB chỉ trong 1 giây.
  5. 🏢 *[Thực tế]:* Viết API `POST /api/v1/payments/checkout` nhận `{ "method": "MOMO" }` hoặc `{ "method": "VNPAY" }` và điều hướng chính xác qua Factory.
* **Từ khóa:** `Strategy Pattern Spring Boot`, `Adapter Pattern Enterprise`, `PaymentPort & PaymentAdapter`, `Vendor Lock-in Prevention`.

---

## 📨 GIAI ĐOẠN 12: Message Broker, Kafka & Outbox Pattern (Tasks 66 - 70)

### 📌 Task 66: Tích Hợp Apache Kafka — Producer, Consumer & Partitioning Cơ Bản
* **Hành động code:** Bật Docker Kafka, viết `KafkaProducerService` bắn `OrderPaidEvent` và `KafkaConsumerService` lắng nghe cập nhật điểm thưởng / xuất hóa đơn.
* **Mục tiêu duy nhất:** Phân tách triệt để các tác vụ phụ trợ ra khỏi luồng chính bằng Message Broker và nắm cơ chế Partition/Consumer Group.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kafka]:* Kafka là một Distributed Append-Only Commit Log trên đĩa cứng. Tại sao tốc độ ghi đĩa tuần tự (Sequential I/O) của Kafka nhanh ngang ngửa RAM?
  2. 🔬 *[Cơ chế Partitions & Consumer Groups]:* Muốn tăng gấp 3 tốc độ xử lý sự kiện thanh toán, tại sao cần chia Topic thành 3 Partitions và chạy 3 Consumer cùng 1 Consumer Group?
  3. ⚠️ *[Đảm bảo Thứ tự Sự kiện]:* Làm sao để các sự kiện của cùng 1 Khách hàng (`OrderCreated` $\rightarrow$ `OrderPaid` $\rightarrow$ `OrderCancelled`) luôn đến đúng thứ tự (Gán Kafka Message Key = `customerId`)?
  4. ⚖️ *[So sánh]:* Spring Event nội bộ (Task 51 — chỉ chạy trong 1 server, chết server là mất) vs **Apache Kafka** (Phân tán, lưu trữ bền vững, replay được dữ liệu cũ).
  5. 🏢 *[Thực tế]:* Đặt hàng thành công $\rightarrow$ Kafka bắn event $\rightarrow$ Consumer nhận event và in log xử lý độc lập.
* **Từ khóa:** `Apache Kafka Producer/Consumer`, `Consumer Groups & Partitions`, `Message Key Ordering`, `Sequential Disk I/O`.

---

### 📌 Task 67: Xử Lý Lỗi Kafka & Xây Dựng Dead Letter Queue (DLQ)
* **Hành động code:** Cấu hình `DefaultErrorHandler` với `FixedBackOff` (thử lại 3 lần) và tự động chuyển tin nhắn lỗi sang Topic `order-paid.DLQ`.
* **Mục tiêu duy nhất:** Ngăn chặn tin nhắn độc (Poison Pill) làm nghẽn toàn bộ luồng xử lý và xây dựng cơ chế phục hồi lỗi.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Poison Pill]:* Khi 1 tin nhắn bị lỗi cú pháp JSON hoặc lỗi NullPointer ném Exception liên tục, Consumer bị kẹt không thể commit offset ra sao? Toàn bộ hàng ngàn tin nhắn phía sau bị chặn thế nào?
  2. ⚠️ *[Cơ chế Dead Letter Queue (DLQ)]:* Sau khi thử lại 3 lần thất bại, đẩy tin nhắn sang topic `.DLQ` giúp giải phóng luồng chính để xử lý hàng ngàn tin nhắn phía sau thế nào?
  3. ⚖️ *[AckMode]:* Phân biệt `AckMode.RECORD` vs `AckMode.BATCH` vs `AckMode.MANUAL_IMMEDIATE` trong cấu hình Spring Kafka.
  4. 🔄 *[Đánh đổi]:* Đẩy vào DLQ giúp hệ thống không bị tắc nghẽn, nhưng cần xây dựng Dashboard hoặc API gì để kỹ sư bấm "Re-drive / Thử lại" các tin nhắn trong DLQ sau khi đã fix bug?
  5. 🏢 *[Thực tế]:* Cố tình gửi payload sai định dạng $\rightarrow$ Consumer thử lại 3 lần $\rightarrow$ Tin nhắn tự động lọt vào topic DLQ an toàn.
* **Từ khóa:** `Kafka Dead Letter Queue (DLQ)`, `Poison Pill Handling`, `Kafka Retry Mechanism`, `Acknowledgment Mode (AckMode)`.

---

### 📌 Task 68: (MỚI — Task Đệm) RabbitMQ & Topic Exchange — So Sánh Thực Chiến Với Kafka
* **Hành động code:** Bật Docker RabbitMQ, tạo Topic Exchange `notification.exchange` với routing key `notification.email.*` và `notification.sms.*`, viết Producer gửi và 2 Consumer nhận theo routing.
* **Mục tiêu duy nhất:** Hiểu cơ chế định tuyến linh hoạt của RabbitMQ và khi nào dùng RabbitMQ thay vì Kafka.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Exchange & Binding]:* RabbitMQ dùng mô hình Exchange $\rightarrow$ Binding $\rightarrow$ Queue để định tuyến message, khác hoàn toàn với Kafka dùng Topic $\rightarrow$ Partition. Ai quyết định message đi đâu trong mỗi hệ thống?
  2. ⚖️ *[So sánh Kafka vs RabbitMQ]:* Kafka phù hợp khi cần replay event lịch sử và throughput triệu message/giây. RabbitMQ phù hợp khi cần routing phức tạp và xác nhận từng message. Bài toán nào của dự án nên dùng cái nào?
  3. ⚠️ *[Bẫy lỗi Unacked Messages]:* Khi Consumer nhận message nhưng không `ack()` trước khi crash, RabbitMQ xử lý message đó thế nào (Requeue hay mất vĩnh viễn tùy `autoAck`)?
  4. 🔄 *[Dead Letter Exchange (DLX)]:* RabbitMQ cũng có cơ chế tương tự Kafka DLQ — cấu hình `x-dead-letter-exchange` để message hết hạn hoặc bị reject tự động chuyển sang Exchange xử lý lỗi.
  5. 🏢 *[Thực tế]:* Gửi 1 notification event $\rightarrow$ Email Consumer nhận $\rightarrow$ SMS Consumer cũng nhận cùng lúc nhờ Topic Exchange routing.
* **Từ khóa:** `RabbitMQ Topic Exchange`, `Message Routing vs Kafka Partitioning`, `Dead Letter Exchange (DLX)`, `Ack vs Nack`.

---

### 📌 Task 69: Đảm Bảo Không Mất Event Bằng Transactional Outbox Pattern
* **Hành động code:** Tạo bảng `outbox_events` (`id`, `aggregate_type`, `payload`, `status`), lưu Event vào bảng này **TRONG CÙNG TRANSACTION DB** của đơn hàng, sau đó dùng Polling Job bắn sang Kafka.
* **Mục tiêu duy nhất:** Giải quyết triệt để vấn đề Dual-Write (Lưu DB thành công nhưng Kafka bị sập làm mất mát dữ liệu đồng bộ).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Vấn đề Dual-Write]:* Tại sao đoạn code: `orderRepo.save(order);` `kafkaTemplate.send(event);` là một sai lầm chết người trong hệ thống phân tán (Nếu mạng rớt ngay giữa 2 dòng)?
  2. 🔬 *[Bản chất Outbox Pattern]:* Lưu Event vào bảng `outbox_events` cùng transaction với bảng `orders` đảm bảo tính chất ACID (Cùng thành công hoặc cùng rollback 100%) ra sao?
  3. ⚖️ *[So sánh 2 cách đọc Outbox]:* **Polling Publisher** (Dùng Scheduled Job quét bảng mỗi 5 giây) vs **Change Data Capture / Debezium** (Đọc thẳng transaction log của DB — zero latency).
  4. ⚠️ *[Quy tắc At-Least-Once Delivery]:* Outbox Pattern cam kết bắn tin nhắn "Ít nhất một lần" (có thể bị trùng do retry), đòi hỏi Consumer bắt buộc phải có tính Idempotent (Task 62) thế nào?
  5. 🏢 *[Thực tế]:* Đặt hàng $\rightarrow$ Quan sát dòng event được ghi vào bảng `outbox_events` $\rightarrow$ Polling Job quét và gửi sang Kafka $\rightarrow$ Đổi trạng thái sang `PUBLISHED`.
* **Từ khóa:** `Transactional Outbox Pattern`, `Dual-Write Problem`, `At-Least-Once Delivery`, `Change Data Capture (CDC) Concept`.

---

### 📌 Task 70: Xây Dựng Engine Đối Soát Dữ Liệu Tài Chính (Reconciliation Engine)
* **Hành động code:** Viết tác vụ Batch Job đọc file log giao dịch đối tác (`partner_transactions.csv`), đối soát 3 bên với bảng `payments` nội bộ và xuất file báo cáo lệch (`Discrepancy Report`).
* **Mục tiêu duy nhất:** Áp dụng Template Method Pattern xây dựng pipeline đối soát tự động hàng đêm để phát hiện sai lệch dòng tiền.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Đối soát]:* Khái niệm **Three-way Matching** (Đối soát 3 chiều): Hệ thống nội bộ (Core) $\leftrightarrow$ Cổng thanh toán (Gateway) $\leftrightarrow$ Ngân hàng (Bank) giải quyết rủi ro tài chính gì?
  2. ⚠️ *[Xử lý File lớn Tránh Out-Of-Memory]:* Khi file đối soát hàng đêm có 500.000 dòng (~200MB), tại sao tuyệt đối không dùng `Files.readAllLines()` mà phải dùng `BufferedReader` hoặc Java Stream Chunking?
  3. ⚖️ *[Các trạng thái Lệch]:* Phân biệt: **Success-Fail** (Bên mình báo thành công, đối tác báo thất bại), **Fail-Success** (Khách bị trừ tiền nhưng chưa nhận hàng), **Amount-Mismatch** (Lệch số tiền).
  4. 🔬 *[Template Method Pattern]:* Thiết kế class trừu tượng `AbstractReconciliationService` định nghĩa khung sườn: `downloadFile()` $\rightarrow$ `parseFile()` $\rightarrow$ `matchTransactions()` $\rightarrow$ `exportReport()`.
  5. 🏢 *[Thực tế]:* Chạy thử batch job với file CSV giả lập có 3 giao dịch lệch $\rightarrow$ Xuất ra file Excel báo cáo đúng 3 giao dịch cần hoàn tiền/tra soát.
* **Từ khóa:** `Financial Reconciliation Engine`, `Three-way Matching`, `Streaming Large File Processing (OOM Prevention)`, `Template Method Pattern`.

---

## 🏛️ GIAI ĐOẠN 13: Spring Internals, Dynamic Security & Kiến Trúc Cấp Cao (Tasks 71 - 75)

### 📌 Task 71: Tự Động Quét Metadata Endpoint Bằng Reflection & Dynamic RBAC
* **Hành động code:** Triển khai `ApplicationListener<ContextRefreshedEvent>`, inject `RequestMappingHandlerMapping` để tự động scan toàn bộ Controller, Method, URL, Permission nạp vào DB khi khởi động.
* **Mục tiêu duy nhất:** Tự động đồng bộ 100% cây phân quyền API vào cơ sở dữ liệu mà không bao giờ cần viết migration phân quyền thủ công bằng tay.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Spring Event]:* Sự kiện `ContextRefreshedEvent` được bắn ra ở giai đoạn nào trong vòng đời của Spring IoC Container (Tất cả Bean đã sẵn sàng)?
  2. 🔬 *[Cơ chế RequestMappingHandlerMapping]:* `handlerMapping.getHandlerMethods()` chứa những thông tin gì (URL Pattern, HTTP Method, Tên Controller, Method Reflection)?
  3. ⚠️ *[Lợi ích Doanh nghiệp]:* Lập trình viên chỉ cần thêm 1 Controller mới kèm `@PreAuthorize("hasAuthority('ORDER_EXPORT')")`, hệ thống tự phát hiện và tạo quyền trên màn hình Admin phân quyền cho nhân viên ra sao?
  4. ⚖️ *[So sánh]:* Quét tự động bằng Reflection lúc khởi động (tốn 0.5 giây lúc start app) vs Nhập quyền thủ công bằng tay vào file migration SQL (Dễ sai sót, lệch tên quyền).
  5. 🏢 *[Thực tế]:* Khởi động ứng dụng, quan sát bảng `permissions` trong DB tự động điền đầy đủ danh sách tất cả API hiện có trong project.
* **Từ khóa:** `ContextRefreshedEvent`, `RequestMappingHandlerMapping`, `Java Reflection Endpoint Scanning`, `Dynamic RBAC Synchronization`.

---

### 📌 Task 72: Bảo Mật Cấu Hình Nâng Cao Với Dynamic Secret Manager / Vault
* **Hành động code:** Tích hợp client mô phỏng HashiCorp Vault / Secret Manager để giải mã các khóa bí mật (DB Password, Payment Secret Key) lúc runtime thay vì lưu file cấu hình tĩnh.
* **Mục tiêu duy nhất:** Xóa bỏ hoàn toàn nguy cơ rò rỉ thông tin bảo mật (Zero Plaintext Secrets in Config Files).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Secret Manager]:* Tại sao trong các doanh nghiệp tài chính/ngân hàng lớn, việc lưu Secret trong file `.yml` (kể cả biến môi trường OS) vẫn bị coi là vi phạm chuẩn bảo mật PCI-DSS?
  2. 🔬 *[Cơ chế Bootstrap & EnvironmentPostProcessor]:* Spring Boot nạp Secret từ Vault vào Environment trước khi khởi tạo các DataSource Bean thế nào?
  3. ⚠️ *[Cơ chế Xoay Vòng Khóa (Key Rotation)]:* Khi một khóa bí mật bị nghi lộ, Secret Manager đổi key mới và Spring Boot cập nhật lại qua `@RefreshScope` mà không cần Restart server ra sao?
  4. ⚖️ *[So sánh]:* GitOps mã hóa bằng SOPS/Sealed Secrets vs Dùng Vault Server tập trung.
  5. 🏢 *[Thực tế]:* Chạy ứng dụng đọc thông tin kết nối từ một Vault Mock Service bảo mật, xác nhận không có plaintext nào trong file `.yml`.
* **Từ khóa:** `Secret Management Best Practices`, `HashiCorp Vault / Cloud Secret Manager`, `PCI-DSS Secret Compliance`, `Dynamic Secret Rotation (@RefreshScope)`.

---

### 📌 Task 73: (MỚI — Task Đệm) Database Migration Chuyên Nghiệp Với Flyway
* **Hành động code:** Tích hợp Flyway, tạo các file migration `V1__init_schema.sql` $\rightarrow$ `V2__add_outbox_table.sql` $\rightarrow$ `V3__add_permissions_table.sql` tương ứng với toàn bộ schema đã tạo trong 72 task trước.
* **Mục tiêu duy nhất:** Quản lý lịch sử thay đổi schema DB có version, đảm bảo mọi môi trường (dev/staging/production) đồng bộ schema tự động.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Version Control cho DB]:* Flyway ghi lại lịch sử migration vào bảng `flyway_schema_history` (checksum, version, thời gian chạy). Tại sao điều này giống hệt `git log` nhưng dành cho schema DB?
  2. ⚠️ *[Tại sao `ddl-auto=update` bị cấm ở Production]:* Hibernate `update` chỉ THÊM cột mới, không bao giờ XÓA cột cũ hay đổi kiểu dữ liệu — dẫn đến schema DB bị "drift" khỏi Entity sau nhiều lần deploy.
  3. ⚖️ *[So sánh]:* **Flyway** (SQL thuần, kiểm soát chính xác từng lệnh DDL) vs **Liquibase** (XML/YAML format, hỗ trợ rollback tốt hơn).
  4. 🔄 *[Đánh đổi]:* Nếu migration `V3` bị lỗi giữa chừng (thêm cột bị fail), Flyway đánh dấu migration đó là `FAILED` — cách sửa và chạy lại mà không bị conflict version?
  5. 🏢 *[Thực tế]:* Chạy ứng dụng trên môi trường fresh (DB trống), quan sát Flyway tự động chạy toàn bộ migration theo đúng thứ tự mà không cần can thiệp tay.
* **Từ khóa:** `Flyway Database Migration`, `Schema Version Control`, `flyway_schema_history`, `ddl-auto=validate Production`.

---

### 📌 Task 74: (MỚI — Task Đệm) Tối Ưu Hiệu Năng — Database Index Nâng Cao & Query Plan
* **Hành động code:** Phân tích toàn bộ các query chậm trong project bằng `EXPLAIN ANALYZE`, thêm Composite Index, Partial Index và đo kết quả trước/sau.
* **Mục tiêu duy nhất:** Nắm vững cách PostgreSQL thực thi query và ra quyết định Index dựa trên dữ liệu thực thay vì phỏng đoán.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Đọc EXPLAIN ANALYZE]:* Phân biệt **Seq Scan** (Quét toàn bộ bảng — nguy hiểm) vs **Index Scan** vs **Index Only Scan** (Nhanh nhất — chỉ đọc index không đụng bảng). Cost và actual rows nói lên điều gì?
  2. ⚠️ *[Composite Index — Thứ tự cột]:* Index `(category_id, price, is_deleted)` giúp gì cho query `WHERE category_id = 1 AND is_deleted = false ORDER BY price`? Tại sao đảo thứ tự cột trong index lại vô tác dụng?
  3. ⚖️ *[Partial Index]:* `CREATE INDEX ON orders(customer_id) WHERE status = 'PENDING'` nhỏ hơn full index bao nhiêu lần và tại sao chỉ hữu ích khi phần lớn đơn hàng đã ở trạng thái `COMPLETED`?
  4. 🔄 *[Đánh đổi Index]:* Index tăng tốc `SELECT` nhưng làm chậm `INSERT`/`UPDATE`/`DELETE` vì phải cập nhật cấu trúc B-Tree. Bảng `audit_log` chỉ ghi không đọc thì có nên đánh index không?
  5. 🏢 *[Thực tế]:* Chèn 100.000 dòng dữ liệu giả vào bảng `products`, chạy `EXPLAIN ANALYZE` trước và sau khi thêm Composite Index — chụp màn hình so sánh cost giảm từ Seq Scan `cost=50000` xuống Index Scan `cost=12`.
* **Từ khóa:** `EXPLAIN ANALYZE PostgreSQL`, `Composite Index Column Order`, `Partial Index`, `Index Overhead on Write Operations`.

---

### 📌 Task 75: Tái Cấu Trúc Hoàn Chỉnh 1 Module Sang Hexagonal Architecture (Ports & Adapters)
* **Hành động code:** Chọn module Order, tái cấu trúc hoàn chỉnh thành 3 layer: `domain` (Plain Java Business Logic & Port Interfaces), `adapter` (JPA, Kafka, REST), `application` (Use Cases & Orchestration).
* **Mục tiêu duy nhất:** Làm chủ toàn bộ tư duy Hexagonal Architecture trên 1 module nghiệp vụ phức tạp thực tế sau khi đã có nền tảng từ Task 64.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[So sánh với Task 64]:* Task 64 chỉ tách Payment đơn giản. Task này tách Order phức tạp hơn (có Transaction, Lock, Kafka Event, Outbox) — thách thức mới nào xuất hiện khi Domain phải phối hợp nhiều Adapter cùng lúc?
  2. 🔬 *[Quy tắc Domain thuần]:* Lớp domain **KHÔNG ĐƯỢC** chứa bất kỳ annotation Spring hay Hibernate nào (`@Entity`, `@Table`, `@Autowired`). Viết Unit Test cho Domain Use Case bằng 100% Java thuần — chạy trong dưới 1ms.
  3. ⚖️ *[So sánh]:* Mô hình 3 tầng truyền thống (Domain bị dính chặt vào JPA/DB) vs **Hexagonal** (Có thể đổi PostgreSQL sang MongoDB chỉ bằng việc viết 1 Adapter mới mà không chạm vào Domain logic).
  4. 🔄 *[Đánh đổi]:* Hexagonal đòi hỏi nhiều Mapper (Domain Entity $\leftrightarrow$ JPA Entity $\leftrightarrow$ DTO) và nhiều Interface hơn, nhưng mang lại lợi ích gì cho các hệ thống tồn tại 10-20 năm?
  5. 🏢 *[Thực tế]:* Viết Unit Test cho `PlaceOrderUseCase` không cần Spring Context, không cần DB, không cần Kafka — chỉ cần Mock các Port Interface bằng Java thuần.
* **Từ khóa:** `Hexagonal Architecture Complete Refactor`, `Clean Architecture in Java`, `Dependency Inversion Principle (DIP)`, `Framework-Independent Domain`.