# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN — PHẦN 3: CACHING, ASYNC, DEVOPS & REALTIME (TASKS 52 - 71)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn $\le 20\%$**: Mỗi bước tiến lên một nấc thang tự nhiên, chuyển tiếp mượt mà, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---

## ⚡ GIAI ĐOẠN 8: Caching, File Storage & Bất Đồng Bộ (Tasks 52 - 58)

### 📌 Task 52: Tích Hợp Redis & Bật `@EnableCaching`
* **Hành động code:** Thêm `spring-boot-starter-data-redis`, cấu hình `RedisCacheManager` với TTL mặc định 10 phút, tuần tự hóa JSON bằng `GenericJackson2JsonRedisSerializer`.
* **Mục tiêu duy nhất:** Thay thế cấu hình cache mặc định (JDK Binary Serializer khó đọc) bằng chuẩn JSON Serializer có thể đọc được trên Redis GUI.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Tuần tự hóa]:* Tại sao mặc định Spring Redis dùng `JdkSerializationRedisSerializer` (dữ liệu lưu vào Redis có tiền tố nhị phân `\xac\xed\x00\x05...`) gây khó khăn gì khi debug bằng `redis-cli`?
  2. ⚠️ *[Bẫy lỗi Class Cast]:* Khi dùng `GenericJackson2JsonRedisSerializer`, tại sao đối tượng lưu vào Redis phải có constructor mặc định (No-args constructor) và thông tin `@class`?
  3. ⚖️ *[So sánh]:* In-memory Cache (Caffeine/Guava - chỉ trong 1 server) vs **Distributed Cache (Redis)**. Khi nào dùng Redis?
  4. 🔄 *[Thời gian sống TTL]:* Tại sao mọi key trong Redis **BẮT BUỘC PHẢI CÓ TTL** (Time-To-Live)? Điều gì xảy ra nếu cache không bao giờ hết hạn (Tràn RAM server)?
  5. 🏢 *[Thực tế]:* Khởi động Redis bằng Docker, chạy ứng dụng và kiểm tra kết nối `PONG` từ Spring Boot tới Redis.
* **Từ khóa:** `@EnableCaching`, `RedisCacheManager`, `GenericJackson2JsonRedisSerializer`, `Cache TTL Best Practices`.

---

### 📌 Task 53: Áp Dụng `@Cacheable` Và `@CacheEvict` Cho Danh Mục
* **Hành động code:** Gắn `@Cacheable(value = "categories")` vào hàm `getAllCategories()`, và `@CacheEvict(value = "categories", allEntries = true)` vào các hàm thêm/sửa/xóa danh mục.
* **Mục tiêu duy nhất:** Giảm tải 99% truy vấn Database cho dữ liệu ít thay đổi nhưng đọc liên tục, và đảm bảo tính nhất quán dữ liệu khi có cập nhật.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cơ chế Spring Cache AOP]:* Khi gọi hàm có `@Cacheable`, Spring AOP chặn lại kiểm tra trong Redis trước: Nếu có (Cache Hit) thì trả về ngay không chạy code method; nếu chưa có (Cache Miss) thì mới gọi DB rồi lưu vào Redis ra sao?
  2. ⚠️ *[Hiện tượng Dữ liệu Rác (Stale Data)]:* Nếu thêm danh mục mới vào DB mà quên gọi `@CacheEvict`, khách hàng sẽ nhìn thấy danh mục cũ trong bao lâu (cho đến khi TTL hết hạn)?
  3. ⚖️ *[So sánh]:* `@CachePut` (luôn chạy hàm rồi cập nhật cache) vs `@Cacheable` (chỉ chạy hàm khi cache miss).
  4. 🔄 *[Đánh đổi]:* Caching làm tăng tốc độ phản hồi từ 50ms xuống 2ms nhưng đánh đổi bằng việc phải quản lý tính nhất quán (Cache Invalidation) - một trong hai bài toán khó nhất của ngành CNTT!
  5. 🏢 *[Thực tế]:* Gọi API lấy danh mục lần 1 (log SQL hiện ra). Gọi lần 2 (không có log SQL nào sinh ra, dữ liệu trả về ngay lập tức)!
* **Từ khóa:** `@Cacheable`, `@CacheEvict(allEntries = true)`, `Cache Hit vs Cache Miss`, `Cache Invalidation Challenge`.

---

### 📌 Task 54: Cache Dữ Liệu Chi Tiết Sản Phẩm Kèm Key Động (`@Cacheable(key = "#id")`)
* **Hành động code:** Gắn `@Cacheable(value = "products", key = "#id")` cho hàm `getProductById(Long id)`, và `@CacheEvict(value = "products", key = "#id")` cho hàm cập nhật sản phẩm đó.
* **Mục tiêu duy nhất:** Nắm vững cách sinh Cache Key động bằng cú pháp SpEL theo từng ID cụ thể thay vì xóa toàn bộ cache của bảng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cú pháp SpEL Key]:* Biểu thức `key = "#id"` hoặc `key = "'product:' + #id"` được Spring phân giải từ tham số của hàm như thế nào?
  2. ⚠️ *[Thảm họa Cache Stampede / Dog-piling]:* Khi 1 sản phẩm cực hot vừa hết hạn TTL cache, cùng 1 giây có 10.000 request cùng ùa vào DB để query $\rightarrow$ Database bị đánh sập lập tức. Cách dùng `sync = true` trong `@Cacheable(sync = true)` để chống lại ra sao?
  3. ⚖️ *[Cache Penetration]:* Hacker cố tình gửi ID không tồn tại (`id = -9999`) liên tục để ép server query DB. Giải pháp: Cache cả giá trị `null` với TTL ngắn (ví dụ: 1 phút) để bảo vệ DB.
  4. 🔄 *[Tối ưu]:* Tại sao chỉ nên xóa đúng key của sản phẩm bị sửa (`key = "#id"`) mà không nên xóa toàn bộ cache sản phẩm (`allEntries = true`)?
  5. 🏢 *[Thực tế]:* Mở phần mềm RedisInsight quan sát: Key `products::1` xuất hiện với cấu trúc JSON hoàn chỉnh của sản phẩm ID 1.
* **Từ khóa:** `@Cacheable(key = "#id")`, `Cache Stampede Defense (sync = true)`, `Cache Penetration`, `RedisInsight Cache Inspection`.

---

### 📌 Task 55: Kiểm Tra Bảo Mật File Upload (Magic Bytes & Chống Path Traversal)
* **Hành động code:** Viết Service upload file ảnh sản phẩm, kiểm tra kích thước tối đa (5MB), kiểm tra đuôi mở rộng (`.jpg`, `.png`), kiểm tra **Magic Bytes** bằng thư viện `Apache Tika`, và chuẩn hóa tên file chống **Path Traversal**.
* **Mục tiêu duy nhất:** Ngăn chặn tin tặc tải mã độc Web Shell (`shell.php`, `exploit.sh`) hoặc dùng kỹ thuật `../../etc/passwd` để ghi đè tập tin hệ thống.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Magic Bytes]:* Tại sao kiểm tra đuôi file `.jpg` hay MIME-Type ở header `Content-Type` là vô nghĩa (Hacker có thể đổi tên file `virus.exe` thành `virus.jpg` và sửa request header)? Magic Bytes là gì (2 byte đầu tiên của ảnh JPEG luôn là `0xFF 0xD8`)?
  2. ⚠️ *[Lỗ hổng Path Traversal]:* Nếu hacker gửi tên file là `../../../../var/www/malicious.jsp`, server lưu thẳng vào ổ cứng thì chuyện gì xảy ra? Cách dùng `FilenameUtils.getName()` hoặc sinh tên file bằng `UUID.randomUUID()`.
  3. ⚖️ *[Kích thước File]:* Cấu hình `spring.servlet.multipart.max-file-size=5MB` và `max-request-size=10MB` trong `application.yml` bảo vệ server khỏi tấn công cạn kiệt ổ đĩa (Disk Exhaustion DoS) ra sao?
  4. 🔄 *[Lưu trữ]:* Tại sao trong kiến trúc Microservices/Cloud, **KHÔNG BAO GIỜ LƯU ẢNH TRÊN Ổ CỨNG CỤC BỘ CỦA SERVER**?
  5. 🏢 *[Thực tế]:* Thử upload 1 file text đổi tên thành `.png` $\rightarrow$ Hệ thống lập tức từ chối với lỗi: "Nội dung tập tin không phải là ảnh hợp lệ".
* **Từ khóa:** `Magic Bytes Inspection (Apache Tika)`, `Path Traversal Defense`, `Multipart Security`, `Disk Exhaustion Prevention`.

---

### 📌 Task 56: Tích Hợp Cloud Storage (Cloudinary / MinIO SDK) Cho Ảnh Sản Phẩm
* **Hành động code:** Tích hợp SDK Cloudinary hoặc MinIO (tương thích AWS S3), viết hàm `uploadImage(MultipartFile file)` trả về đường dẫn CDN công khai `https://...`.
* **Mục tiêu duy nhất:** Tách biệt hoàn toàn tầng lưu trữ tài nguyên tĩnh (Static Assets / Object Storage) ra khỏi Web Server theo chuẩn Cloud-Native 12-Factor App.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Kiến trúc Cloud-Native]:* Nguyên tắc "Stateless Server" (Server không lưu trạng thái trên ổ đĩa): Server có thể bị tắt, bật, nhân bản thành 10 container bất cứ lúc nào mà không bị mất file người dùng đã tải lên.
  2. ⚠️ *[Hạn chế Local Storage]:* Khi chạy 3 server sau Load Balancer, tại sao lưu ảnh ở ổ cứng Server 1 thì Server 2 và Server 3 sẽ báo lỗi `404 Not Found` khi khách xem ảnh?
  3. 🔄 *[Đánh đổi]:* Lưu ảnh vào DB dạng BLOB (`byte[]`) là tối kỵ vì làm DB phình to hàng chục GB và làm sập cache RAM của DB.
  4. ⚖️ *[Tối ưu CDN]:* Cloudinary tự động nén ảnh WebP và resize ảnh động theo kích thước màn hình điện thoại/máy tính giúp tiết kiệm băng thông ra sao?
  5. 🏢 *[Thực tế]:* Chạy API upload ảnh lên Cloudinary và nhận về link ảnh CDN `https://res.cloudinary.com/...` hiển thị trực tiếp lên trình duyệt!
* **Từ khóa:** `Cloudinary / AWS S3 SDK`, `Object Storage Architecture`, `CDN Image Delivery Optimization`, `Multi-Instance File Storage Solution`.

---

### 📌 Task 57: Bật Tính Năng Chạy Ngầm Bất Đồng Bộ (`@EnableAsync` & `ThreadPoolTaskExecutor`)
* **Hành động code:** Bật `@EnableAsync`, cấu hình Bean `ThreadPoolTaskExecutor` (với `corePoolSize`, `maxPoolSize`, `queueCapacity`), viết service gửi email thông báo chạy ngầm bằng `@Async`.
* **Mục tiêu duy nhất:** Cấu hình Thread Pool chuẩn cho tác vụ chạy ngầm bất đồng bộ để request người dùng không bị nghẽn (Non-blocking I/O).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `@Async`]:* `@Async` dùng Spring AOP đẩy việc thực thi method sang một Thread Worker riêng biệt từ `TaskExecutor` ra sao?
  2. ⚠️ *[Bẫy lỗi Thread Pool Mặc định]:* Mặc định Spring dùng `SimpleAsyncTaskExecutor` (Tạo mới 1 Thread cho mỗi request ngầm và không tái sử dụng) $\rightarrow$ 10.000 đơn hàng sẽ làm sập server do cạn RAM tạo Thread của OS.
  3. ⚖️ *[Cấu hình chuẩn]:* Ý nghĩa và mối quan hệ giữa `corePoolSize` (Số thread nuôi thường trực), `queueCapacity` (Hàng đợi khi core bận), `maxPoolSize` (Mở rộng khi hàng đợi đầy)?
  4. 🔄 *[Xử lý lỗi Fire-and-Forget]:* Hàm `@Async` trả về `void` (bắn xong bỏ mặc), làm sao bắt lỗi mất mạng/sai SMTP bằng `AsyncUncaughtExceptionHandler`?
  5. 🏢 *[Thực tế]:* Gọi API đặt hàng: API trả về kết quả 201 ngay sau 10ms, trong khi dòng log gửi email xuất hiện sau đó 2 giây trên một Thread riêng biệt `[async-worker-1]`.
* **Từ khóa:** `@EnableAsync & @Async`, `ThreadPoolTaskExecutor vs SimpleAsyncTaskExecutor`, `AsyncUncaughtExceptionHandler`, `Non-blocking Background Task`.

---

### 📌 Task 58: (MỚI) Xây Dựng Luồng Quên/Đặt Lại Mật Khẩu Bằng Email Async
* **Hành động code:** Tạo API `POST /api/v1/auth/forgot-password` sinh mã Token đặt lại mật khẩu bảo mật (UUID có TTL 15 phút lưu trong Redis), gửi email chứa link đặt lại (`/reset-password?token=...`) bất đồng bộ qua `@Async`, và API `POST /api/v1/auth/reset-password` nhận token cùng mật khẩu mới.
* **Mục tiêu duy nhất:** Hoàn thiện luồng phục hồi tài khoản chuẩn sản xuất, kết hợp nhuần nhuyễn giữa Redis TTL, bảo mật một lần (Single-use Token) và xử lý gửi mail ngầm.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Single-use Token]:* Tại sao Token đặt lại mật khẩu phải bị XÓA NGAY LẬP TỨC khỏi Redis sau khi người dùng đổi mật khẩu thành công (Chống Replay Attack đổi pass lần 2)?
  2. ⚠️ *[Bảo mật Chống User Enumeration]:* Khi người dùng nhập một email không tồn tại trong DB vào ô "Quên mật khẩu", API nên trả về lỗi `404 Không tìm thấy email` hay trả về thông báo chung: *"Nếu email tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn"*?
  3. 🔬 *[Tại sao gửi Email phải Async]:* Quá trình kết nối SMTP Server (Gmail/SendGrid) thường mất từ 1.5 đến 3 giây. Nếu chạy đồng bộ (Synchronous), người dùng bấm nút sẽ bị đơ màn hình chờ 3 giây ra sao?
  4. ⚖️ *[Mã OTP 6 số vs Link Token UUID]:* Khi nào nên dùng mã OTP 6 số (gửi qua SMS/Zalo) và khi nào dùng Link Token bí mật (gửi qua Email)?
  5. 🏢 *[Thực tế]:* Yêu cầu quên mật khẩu $\rightarrow$ Kiểm tra Redis có key `reset_token:...` với TTL 900s $\rightarrow$ Gọi API reset pass $\rightarrow$ Đăng nhập bằng mật khẩu mới thành công, token trong Redis tự động biến mất!
* **Từ khóa:** `Password Reset Flow`, `Async Email Notification`, `Single-use Security Token`, `User Enumeration Defense`, `Redis TTL Verification`.

---

## 🧪 GIAI ĐOẠN 9: Event-Driven Nội Bộ, Docker & Testing Nâng Cao (Tasks 59 - 66)

### 📌 Task 59: Tách Rời Logic Bằng Spring Event (`ApplicationEventPublisher` & `@TransactionalEventListener`)
* **Hành động code:** Khi tạo đơn hàng thành công, bắn `OrderCreatedEvent` bằng `eventPublisher.publishEvent(...)`; tạo listener lắng nghe bằng `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`.
* **Mục tiêu duy nhất:** Áp dụng mô hình Event-Driven Architecture nội bộ để phân rã phụ thuộc giữa các module và bảo đảm tính toàn vẹn Transaction.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kiến trúc]:* Event-Driven Architecture nội bộ giúp giảm phụ thuộc (Decoupling) giữa `OrderService` và `EmailService` thế nào (`OrderService` không cần tiêm `EmailService`)?
  2. ⚠️ *[Bẫy lỗi Transaction]:* Nếu đơn hàng lưu DB bị lỗi và rollback, làm sao tránh việc email chúc mừng vẫn bị gửi đi bằng `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`?
  3. ⚖️ *[So sánh]:* Đồng bộ (`@EventListener`) vs Bất đồng bộ (`@Async @EventListener`).
  4. 🔄 *[Đánh đổi]:* Tuân thủ nguyên lý Open/Closed (thêm tính năng tích điểm, gửi SMS mà không sửa code OrderService) nhưng khó lần theo vết luồng code hơn khi debug.
  5. 🏢 *[Thực tế]:* Tạo `OrderCreatedEvent`, dùng `eventPublisher.publishEvent(...)` trong `OrderServiceImpl`, và tạo `OrderNotificationListener` gửi mail sau khi commit thành công.
* **Từ khóa:** `ApplicationEventPublisher`, `@TransactionalEventListener AFTER_COMMIT`, `Event-Driven Loose Coupling`, `Open/Closed Principle`.

---

### 📌 Task 60: Nâng Cấp Integration Test Với Testcontainers (Test PostgreSQL Thật)
* **Hành động code:** Tích hợp `testcontainers-postgresql` để chạy integration test với database PostgreSQL thật trong Docker container thay vì dùng H2 in-memory.
* **Mục tiêu duy nhất:** Kiểm thử ứng dụng trên môi trường Database thật tự động khởi tạo bằng Docker, loại bỏ hoàn toàn bẫy lỗi "chạy được trên H2 nhưng lỗi trên Prod".
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Testcontainers]:* Testcontainers tự động kéo Docker Image PostgreSQL và khởi chạy container ảo trong lúc chạy test, sau đó tự hủy container ra sao?
  2. ⚠️ *[Rủi ro H2 Database]:* Tại sao không nên dùng H2 Database in-memory để test cho dự án production chạy PostgreSQL (Khác biệt về cú pháp SQL Dialect, hàm JSONB, Regex, Stored Procedures)?
  3. ⚖️ *[So sánh]:* Test Mock (Mockito) vs Test với Testcontainers.
  4. 🔄 *[Đánh đổi]:* Testcontainers cực kỳ chân thực nhưng tốn thêm 5-10 giây để kéo và bật container Docker trong lần chạy đầu tiên.
  5. 🏢 *[Thực tế]:* Viết Integration Test hoàn chỉnh tạo Order và lưu vào PostgreSQL container ảo, chạy `./mvnw test` thành công rực rỡ!
* **Từ khóa:** `Testcontainers PostgreSQL Spring Boot`, `H2 Database vs Real Database Pitfalls`, `@Testcontainers & @Container`, `Production Parity Testing`.

---

### 📌 Task 61: (MỚI) Đo Độ Phủ Kiểm Thử Với JaCoCo & Thiết Lập Quality Gate CI
* **Hành động code:** Thêm `jacoco-maven-plugin` vào `pom.xml`, cấu hình goal `prepare-agent` và `report`, đặt ngưỡng kiểm soát chất lượng (Rule Violation) chặn build nếu độ phủ dòng code (`LINE`) dưới 80%.
* **Mục tiêu duy nhất:** Tự động đo lường độ bao phủ kiểm thử của toàn bộ codebase và biến nó thành cổng chặn chất lượng tự động trước khi triển khai.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất JaCoCo]:* JaCoCo can thiệp vào Bytecode Java (`Java Agent`) lúc chạy test để đếm số dòng (`Instruction/Line`) và số nhánh rẽ điều kiện (`Branch Coverage: if/else`) được thực thi như thế nào?
  2. ⚠️ *[Loại trừ Code Rác]:* Tại sao các DTO, Entity, Configuration class hoặc Mapper sinh tự động nên được loại trừ khỏi báo cáo JaCoCo (`<exclude>com/example/**/dto/**</exclude>`)?
  3. ⚖️ *[Line Coverage vs Branch Coverage]:* Tại sao đạt 100% Line Coverage vẫn có thể bị sót bug nếu Branch Coverage thấp (Chưa test các nhánh `if (x == null)` hoặc nhánh ném ngoại lệ)?
  4. 🔄 *[Quality Gate]:* Cấu hình thẻ `<rule>` trong plugin: Nếu độ phủ trung bình dưới 0.80 thì lệnh `mvn clean verify` tự động báo `BUILD FAILURE`.
  5. 🏢 *[Thực tế]:* Chạy `./mvnw clean verify`, mở file `target/site/jacoco/index.html` trên trình duyệt xem báo cáo đồ họa trực quan từng package màu xanh (đã test) và màu đỏ (chưa test).
* **Từ khóa:** `JaCoCo Maven Plugin`, `Line vs Branch Coverage`, `Quality Gate Enforcement`, `Bytecode Instrumentation`, `Automated Coverage Report`.

---

### 📌 Task 62: Tích Hợp Health Check & Metrics Với Spring Boot Actuator
* **Hành động code:** Thêm `spring-boot-starter-actuator` và cấu hình endpoint giám sát sức khỏe hệ thống trong `application.yml`, viết một `CustomHealthIndicator` kiểm tra kết nối dịch vụ bên ngoài.
* **Mục tiêu duy nhất:** Giám sát sức khỏe ứng dụng và bảo mật các endpoint nhạy cảm của Actuator.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Endpoint `/actuator/health` kiểm tra Database Connection Pool, Redis, Ổ đĩa Disk Space ra sao?
  2. ⚠️ *[Bảo mật Actuator]:* Endpoint `/actuator/env` (Lộ mật khẩu DB) hoặc `/actuator/heapdump` (Tải toàn bộ RAM) cực kỳ nguy hiểm. Tại sao ở Production chỉ nên mở `health, info, metrics`?
  3. 🔬 *[Hệ sinh thái Giám sát]:* **Actuator/Micrometer** (Thu thập Metrics) $\rightarrow$ **Prometheus** (Lưu trữ Time-Series) $\rightarrow$ **Grafana** (Vẽ biểu đồ CPU, RAM, Latency, TPS).
  4. ⚖️ *[Liveness vs Readiness]:* Phân biệt **Liveness Probe** (App còn sống không để K8s restart) vs **Readiness Probe** (App đã khởi động xong để nhận traffic chưa) cho Kubernetes.
  5. 🏢 *[Thực tế]:* Cấu hình mở `show-details=always` ở môi trường Dev để xem chi tiết tình trạng kết nối DB và Redis.
* **Từ khóa:** `Spring Boot Actuator`, `Actuator Security Best Practices`, `Prometheus & Grafana Ecosystem`, `Liveness and Readiness Probes`.

---

### 📌 Task 63: (MỚI) Bắt & Báo Cáo Lỗi Tập Trung Với Sentry SDK
* **Hành động code:** Tích hợp `sentry-spring-boot-starter-jakarta`, cấu hình `sentry.dsn` trong `application-prod.yml`, tích hợp vào `GlobalExceptionHandler` để tự động đẩy unhandled exception lên Sentry kèm thông tin Trace-Id và User context.
* **Mục tiêu duy nhất:** Chuyển từ giám sát lỗi bị động (chờ khách hàng phản ánh) sang chủ động nhận thông báo lỗi kèm Stacktrace tức thì trong 1 giây.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Error Tracking]:* Tại sao đọc file log trên server thủ công bằng lệnh `grep/tail` là bất khả thi khi chạy 50 container trên Kubernetes? Sentry gom nhóm các lỗi giống nhau (Error Grouping) ra sao?
  2. ⚠️ *[Bảo mật Dữ liệu Riêng tư (PII)]:* Làm sao để cấu hình Sentry không thu thập các thông tin nhạy cảm như Mật khẩu, Số thẻ ngân hàng, Authorization Header (Data Scrubbing)?
  3. 🔬 *[Gắn Context Vào Báo Cáo]:* Dùng `Sentry.setTag("traceId", traceId)` và `Sentry.setUser(...)` giúp lập trình viên tái hiện chính xác lỗi của khách hàng như thế nào?
  4. ⚖️ *[Sampling Rate]:* Tại sao ở hệ thống hàng chục triệu request/ngày, cấu hình `sentry.traces-sample-rate` chỉ nên để 0.1 (10%) hoặc 0.05 (5%) để tránh nghẽn mạng và tốn chi phí gói Sentry?
  5. 🏢 *[Thực tế]:* Cố tình tạo 1 lỗi NullPointerException chưa bắt $\rightarrow$ Điện thoại/Email của bạn nhận ngay thông báo từ Sentry kèm đường link xem đúng dòng code bị lỗi!
* **Từ khóa:** `Sentry Exception Tracking`, `Centralized Error Monitoring`, `PII Data Scrubbing`, `Sampling Rate Optimization`, `Proactive Bug Alerting`.

---

### 📌 Task 64: (MỚI) Triển Khai Cơ Chế Graceful Shutdown Khi Deploy Ứng Dụng
* **Hành động code:** Thêm cấu hình `server.shutdown=graceful` và `spring.lifecycle.timeout-per-shutdown-phase=30s` vào `application.yml`, tạo kịch bản gọi API xử lý lâu 5 giây và gửi tín hiệu `SIGTERM` tắt server.
* **Mục tiêu duy nhất:** Đảm bảo quá trình Zero-Downtime Deployment: Server không bao giờ ngắt ngang xương các giao dịch khách hàng đang thanh toán dở dang khi bị tắt để deploy phiên bản mới.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Graceful Shutdown]:* Khi nhận tín hiệu tắt `SIGTERM` từ Docker/Kubernetes, Tomcat Graceful Shutdown hoạt động ra sao: (1) Ngừng tiếp nhận request mới $\rightarrow$ (2) Cho phép các request đang xử lý dở dang (in-flight) tiếp tục chạy tối đa 30s $\rightarrow$ (3) Đóng DB connection $\rightarrow$ (4) Tắt JVM an toàn?
  2. ⚠️ *[Hậu quả của Tắt Đột ngột (SIGKILL)]:* Nếu không có Graceful Shutdown, khách hàng đang quẹt thẻ trừ tiền 10 triệu thì server bị kill ngang $\rightarrow$ Tiền bị trừ nhưng đơn hàng chưa lưu vào DB!
  3. ⚖️ *[SIGTERM vs SIGKILL]:* Lệnh `docker stop` gửi tín hiệu `SIGTERM` (cho 10s dọn dẹp) trước khi gửi `SIGKILL` (giết cưỡng chế) như thế nào?
  4. 🔄 *[Phối hợp với K8s Readiness Probe]:* Khi bắt đầu tắt, Actuator Health đổi trạng thái sang `OUT_OF_SERVICE` để Load Balancer ngừng điều hướng khách mới vào server này ra sao?
  5. 🏢 *[Thực tế]:* Gọi API ngủ 5 giây $\rightarrow$ Gõ `kill -15 <pid>` trên Terminal $\rightarrow$ Quan sát log: Server đợi API trả về 200 OK thành công rồi mới in dòng "Graceful shutdown complete" và dừng tiến trình.
* **Từ khóa:** `server.shutdown=graceful`, `SIGTERM vs SIGKILL`, `In-flight Request Completion`, `Zero-Downtime Deployment`, `Kubernetes Pod Lifecycle`.

---

### 📌 Task 65: Đóng Gói Ứng Dụng Với Docker & Docker Compose
* **Hành động code:** Viết `Dockerfile` Multi-stage và file `docker-compose.yml` chạy trọn vẹn Backend + PostgreSQL + Redis bằng 1 lệnh.
* **Mục tiêu duy nhất:** Container hóa toàn bộ hệ thống bằng Docker Compose và bảo mật Non-root User.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Multi-stage Build]:* Stage 1 (Build jar với `maven:3.9-eclipse-temurin-21`) và Stage 2 (Chạy jar với `eclipse-temurin:21-jre-alpine`) giúp giảm dung lượng Image từ 800MB xuống 150MB ra sao?
  2. 🔬 *[Mạng Docker Compose]:* Spring Boot kết nối với DB qua URL `jdbc:postgresql://postgres-db:5432/postgres` (dùng tên Service của Docker) nhờ cơ chế Docker DNS nội bộ thế nào?
  3. ⚠️ *[Bảo mật Container]:* Tạo non-root user (`RUN adduser -D springuser && USER springuser`) để không chạy app dưới quyền `root`.
  4. ⚖️ *[Quản lý Phụ thuộc]:* Dùng `depends_on` kèm `condition: service_healthy` để PostgreSQL và Redis khởi động xong thì Spring Boot mới được bật.
  5. 🏢 *[Thực hành Tối thượng]:* Chạy `docker compose up -d --build` và kiểm tra toàn bộ hệ thống hoạt động trơn tru.
* **Từ khóa:** `Multi-stage Dockerfile Best Practices`, `docker-compose.yml Networking`, `Non-root User Security in Docker`.

---

### 📌 Task 66: (MỚI) Kiểm Thử Hiệu Năng & Tải Trọng Hệ Thống Bằng k6 (Load Testing)
* **Hành động code:** Cài đặt công cụ k6, viết file kịch bản `load_test.js` mô phỏng 100 người dùng ảo (Virtual Users - VUs) đồng thời gọi API tìm kiếm sản phẩm và đặt hàng trong 60 giây; phân tích các chỉ số RPS, Latency P95, P99 và tỷ lệ lỗi HTTP.
* **Mục tiêu duy nhất:** Định lượng chính xác năng lực chịu tải tối đa của hệ thống Backend dưới điều kiện tải cao thực tế và tìm ra nút thắt cổ chai (Bottleneck).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất k6]:* k6 sử dụng ngôn ngữ JavaScript viết kịch bản nhưng thực thi bằng Go đa luồng hiệu năng cao (Goroutines) tạo hàng ngàn kết nối đồng thời với mức tiêu tốn CPU/RAM cực thấp ra sao?
  2. 🔬 *[Ý nghĩa Chỉ số P95 & P99]:* Tại sao chỉ nhìn vào thời gian phản hồi trung bình (Average Latency) là tự lừa dối mình? P95 = 200ms và P99 = 2000ms nói lên điều gì về trải nghiệm của 1% khách hàng kém may mắn nhất?
  3. ⚠️ *[Nhận diện Điểm nghẽn (Bottleneck)]:* Khi tăng từ 50 VUs lên 200 VUs, nếu CPU DB chạm 100% hoặc HikariCP hết connection $\rightarrow$ TPS không tăng mà Latency tăng vọt (Hiện tượng nghẽn cổ chai).
  4. ⚖️ *[So sánh]:* **k6** (Hiện đại, viết bằng code JS, tích hợp dễ dàng vào CI/CD) vs **Apache JMeter** (Nặng nề, cấu hình giao diện XML phức tạp).
  5. 🏢 *[Thực tế]:* Chạy lệnh `k6 run load_test.js`, quan sát bảng kết quả Terminal: `http_req_duration: p(95)=45ms`, `http_req_failed: 0.00%`, `iterations: 12000` (đạt 200 RPS xanh mướt)!
* **Từ khóa:** `k6 Load Testing`, `Virtual Users (VUs)`, `Percentile Latency (P95, P99)`, `Throughput (RPS)`, `System Bottleneck Identification`.

---

## 🚀 GIAI ĐOẠN 10: CI/CD Pipeline, Khóa Phân Tán & Realtime (Tasks 67 - 71)

### 📌 Task 67: Tự Động Hóa CI/CD Pipeline Với GitHub Actions
* **Hành động code:** Tạo file `.github/workflows/ci.yml` tự động build và chạy toàn bộ test suite khi có code mới push lên GitHub.
* **Mục tiêu duy nhất:** Xây dựng cổng kiểm soát chất lượng tự động (Quality Gate) trước khi merge code vào production.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cơ chế CI/CD Pipeline]:* Khái niệm Continuous Integration (CI) là gì? Khi lập trình viên push code, GitHub Actions tự động bật Runner ảo chạy `./mvnw clean test` ra sao?
  2. ⚠️ *[Chặn Merge Code Lỗi]:* 1 bài test bị fail, GitHub Actions đánh dấu đỏ (Build Failed) và chặn không cho Merge vào nhánh `main`.
  3. ⚖️ *[Cache Dependencies]:* Cấu hình cache thư mục `~/.m2/repository` trong GitHub Actions giúp giảm thời gian build từ 5 phút xuống 40 giây.
  4. 🔄 *[Đánh đổi]:* Tối ưu hóa chạy test song song (Parallel Testing) khi test suite quá lớn.
  5. 🏢 *[Thực tế]:* Tạo file `.github/workflows/ci.yml` hoàn chỉnh kiểm tra chất lượng code tự động.
* **Từ khóa:** `GitHub Actions CI Pipeline (.github/workflows/ci.yml)`, `Automated Quality Gates`, `Maven Dependency Caching in CI`.

---

### 📌 Task 68: Khóa Phân Tán (Distributed Lock) Với Redisson
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

### 📌 Task 69: Rate Limiting Phân Tán Bằng Redis Lua Script Nguyên Tử
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

### 📌 Task 70: Thông Báo Biến Động Số Dư Realtime Qua WebSocket STOMP & Redis Pub/Sub
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

### 📌 Task 71: Tổng Hợp Redis Nâng Cao — Pipeline, TTL Strategy & Monitoring
* **Hành động code:** Viết service dùng `RedisTemplate.executePipelined()` để gom nhiều lệnh Redis thành 1 round-trip duy nhất, cấu hình TTL động theo loại dữ liệu, xem metrics qua `redis-cli INFO stats`.
* **Mục tiêu duy nhất:** Tối ưu hiệu năng Redis và nắm vững chiến lược TTL trước khi bước vào Kafka và Fintech phức tạp hơn.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Pipeline]:* `executePipelined()` gom $N$ lệnh Redis thành 1 round-trip duy nhất thay vì $N$ round-trip riêng lẻ — tiết kiệm latency mạng thế nào khi đọc 100 key cùng lúc?
  2. ⚠️ *[Bẫy lỗi Memory Leak]:* Key không có TTL tích lũy vĩnh viễn trong RAM — dùng `redis-cli --scan --pattern "*"` phối hợp kiểm tra TTL để phát hiện key nào đang không có TTL ra sao?
  3. ⚖️ *[So sánh TTL Strategy]:* TTL cố định (30 phút) vs TTL động (Session = 30 phút, OTP = 5 phút, Distributed Lock = 5 giây). Nguyên tắc nào quyết định TTL phù hợp?
  4. 🔄 *[Đánh đổi]:* **Redis Cluster** vs **Redis Sentinel**. Khi nào cần Cluster (Sharding dữ liệu), khi nào Sentinel (Chỉ cần High Availability)?
  5. 🏢 *[Thực tế]:* Dùng lệnh `redis-cli INFO stats` đọc `keyspace_hits` và `keyspace_misses` để tính Cache Hit Rate của hệ thống đang chạy.
* **Từ khóa:** `Redis Pipeline executePipelined`, `TTL Strategy by Data Type`, `Redis Cluster vs Sentinel`, `Cache Hit Rate Monitoring`.
