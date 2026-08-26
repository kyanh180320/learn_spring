# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN (CHUẨN SINGLE RESPONSIBILITY & KHUNG 5D)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn 20%**: Mỗi bước tiến lên một nấc thang tự nhiên, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---


## ⚡ GIAI ĐOẠN 7: Caching & Tối Ưu Tốc Độ Với Redis (Tasks 45 - 47)

> 💡 **Khởi động Redis nhanh bằng Docker:** Chạy lệnh `docker run -d --name redis-learn -p 6379:6379 redis:alpine` là có ngay Redis sẵn sàng!

### 📌 Task 45: Tích Hợp Redis & Bật `@EnableCaching`
* **Hành động code:** Thêm `spring-boot-starter-data-redis` vào `pom.xml`, cấu hình `RedisCacheManager` với TTL mặc định 30 phút.
* **Mục tiêu duy nhất:** Kết nối Redis và cấu hình JSON Serializer cho Cache Manager.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất In-memory]:* Tại sao đọc dữ liệu từ RAM của Redis (vài micro-giây) nhanh hơn đọc từ đĩa cứng PostgreSQL gấp 100 lần?
  2. 🔬 *[Cơ chế Serialization]:* Mặc định Spring dùng `JdkSerializationRedisSerializer` (nhị phân). Cấu hình `GenericJackson2JsonRedisSerializer` để lưu dữ liệu dưới dạng JSON dễ đọc ra sao?
  3. ⚠️ *[Bẫy lỗi TTL]:* Tại sao **BẮT BUỘC PHẢI CẤU HÌNH TTL (Time-To-Live)** cho mọi Key? Nếu không đặt TTL, RAM của Redis Server sẽ bị gì sau 1 năm tích tụ dữ liệu?
  4. ⚖️ *[So sánh]:* **Local Cache** (Caffeine trong RAM của JVM) vs **Distributed Cache** (Redis độc lập). Tại sao chạy 5 cụm server bắt buộc phải dùng Redis (Tránh Cache Inconsistency)?
  5. 🏢 *[Thực tế]:* Viết class `RedisConfig` cấu hình CacheManager hoàn chỉnh.
* **Từ khóa:** `Redis In-Memory Performance`, `GenericJackson2JsonRedisSerializer`, `Cache TTL`, `Local vs Distributed Cache`.

---

### 📌 Task 46: Áp Dụng `@Cacheable` Và `@CacheEvict` Cho Danh Mục
* **Hành động code:** Gắn `@Cacheable(value = "categories")` vào `getAllCategories()`, gắn `@CacheEvict(value = "categories", allEntries = true)` vào các hàm Thêm/Sửa/Xóa.
* **Mục tiêu duy nhất:** Áp dụng mô hình Cache-Aside và xóa Cache tự động khi dữ liệu thay đổi.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Cache-Aside]:* Mô hình Cache-Aside (Lazy Loading): (1) Đọc Cache $\rightarrow$ (2) Cache Miss đọc DB $\rightarrow$ (3) Ghi Cache.
  2. 🔬 *[Cơ chế Xóa Cache]:* `@CacheEvict(allEntries = true)` xóa sạch các key trong nhóm `categories` khi dữ liệu thay đổi thế nào?
  3. ⚠️ *[Vấn đề Dữ liệu Cũ (Stale Data)]:* Vào thẳng DBeaver sửa tên danh mục, Redis có biết để tự cập nhật không? Khách hàng nhìn thấy dữ liệu gì cho đến khi TTL hết hạn?
  4. ⚠️ *[Rủi ro Caching]:* Phân biệt **Cache Penetration** (Query ID ma liên tục đánh sập DB), **Cache Breakdown** (Key hot hết hạn 10.000 request cùng lao vào DB), **Cache Avalanche** (Hàng loạt Key cùng hết hạn 1 lúc).
  5. 🏢 *[Thực hành]:* Gọi API lần 1 có log SQL, gọi lần 2 không còn log SQL và thời gian phản hồi giảm từ 50ms xuống 2ms!
* **Từ khóa:** `Cache-Aside Pattern`, `@Cacheable & @CacheEvict`, `Stale Data Problem`, `Cache Penetration vs Breakdown vs Avalanche`.

---

### 📌 Task 47: Cache Dữ Liệu Chi Tiết Sản Phẩm Kèm Key Động (`@Cacheable(key = "#id")`)
* **Hành động code:** Cache thông tin chi tiết sản phẩm theo ID `getProductById(Long id)` và xóa cache đúng sản phẩm đó khi cập nhật (`@CacheEvict(key = "#id")`).
* **Mục tiêu duy nhất:** Quản lý Key Cache chi tiết từng bản ghi bằng biểu thức SpEL.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất SpEL Key]:* Biểu thức SpEL `key = "#id"` tạo ra Redis Key có dạng `products::1`, `products::2` như thế nào?
  2. ⚠️ *[Bẫy lỗi `@CachePut` vs `@CacheEvict`]:* Khi update sản phẩm, tại sao người ta thường chọn `@CacheEvict` (xóa key để lần sau đọc lại DB) thay vì `@CachePut` (ghi đè kết quả trả về vào cache)?
  3. ⚖️ *[So sánh]:* Cache toàn bộ danh sách sản phẩm vs Cache từng sản phẩm theo ID. Kiểu nào tiết kiệm bộ nhớ RAM hơn khi có hàng triệu sản phẩm?
  4. 🔄 *[Đánh đổi]:* Việc lưu trữ dữ liệu dưới dạng JSON trong Redis làm tăng nhẹ dung lượng so với nhị phân (Binary), nhưng đổi lại lợi ích lớn về khả năng debug và xem trực tiếp trên RedisInsight.
  5. 🏢 *[Thực tế]:* Kiểm tra RedisInsight hoặc lệnh `redis-cli KEYS "*"` để thấy các key sản phẩm được lưu trữ trực quan.
* **Từ khóa:** `SpEL Cache Keys`, `@CachePut vs @CacheEvict`, `Granular Caching Strategy`.

---

## ⚡ GIAI ĐOẠN 8: Bất Đồng Bộ, Upload File Lên Mây & Event-Driven (Tasks 48 - 52)

### 📌 Task 48: Kiểm Tra Bảo Mật File Upload (Magic Bytes & Chống Path Traversal) *(BẢO MẬT FILE ĐỘC LẬP)*
* **Hành động code:** Viết class `FileSecurityValidator` kiểm tra: dung lượng $\le 5MB$, sanitize tên file với `Path.normalize()`, và đọc Magic Bytes (File Signature) byte đầu tiên.
* **Mục tiêu duy nhất:** Ngăn chặn tuyệt đối việc hacker upload mã độc giả mạo đuôi ảnh (.php, .sh, .exe đổi tên thành .jpg) và tấn công Path Traversal.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cơ chế Magic Bytes]:* Khái niệm **Magic Bytes (File Signature)** là gì? Làm sao đọc byte đầu tiên (`FF D8 FF` cho JPEG, `89 50 4E 47` cho PNG) để xác định định dạng thực sự của file?
  2. ⚠️ *[Lỗ hổng Đổi đuôi file]:* Nếu hacker đổi file virus `shell.php` thành `avatar.jpg`, tại sao kiểm tra bằng `filename.endsWith(".jpg")` bị qua mặt hoàn toàn?
  3. ⚠️ *[Tấn công Path Traversal]:* Nếu hacker đặt tên file là `../../../../etc/passwd` hoặc `../../System32/file.dll`, hàm `Path.normalize()` ngăn chặn việc ghi đè vào thư mục hệ điều hành ra sao?
  4. ⚖️ *[Giới hạn dung lượng]:* Cấu hình `spring.servlet.multipart.max-file-size=5MB` trong `application.yml` bảo vệ server khỏi tấn công cạn kiệt ổ cứng (Disk DoS).
  5. 🏢 *[Thực tế]:* Viết Unit Test: Thử đổi đuôi file text thành `.jpg` và truyền vào validator $\rightarrow$ Validator bắt được ngay lỗi `INVALID_FILE_TYPE`!
* **Từ khóa:** `File Upload Security Magic Bytes`, `Path Traversal Prevention`, `Apache Tika / Java NIO MIME Detection`, `Multipart Max File Size`.

---

### 📌 Task 49: Tích Hợp Cloud Storage (Cloudinary / MinIO SDK) Cho Ảnh Sản Phẩm *(LÊN MÂY CHUYÊN NGHIỆP)*
* **Hành động code:** Tích hợp SDK Cloudinary (hoặc AWS S3/MinIO), upload file qua luồng InputStream và lưu link ảnh CDN vào DB.
* **Mục tiêu duy nhất:** Giải quyết triệt để bài toán lưu trữ phân tán cho hệ thống nhiều server (Multi-instance Cluster).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Object Storage]:* Tại sao Cloudinary / AWS S3 / MinIO giải quyết dứt điểm bài toán nhiều server (Mọi server đều trỏ về 1 kho lưu trữ đám mây dùng chung qua CDN)?
  2. ⚠️ *[Hạn chế Local Storage]:* Khi chạy 3 server sau Load Balancer, tại sao lưu ảnh ở ổ cứng Server 1 thì Server 2 và Server 3 sẽ báo lỗi `404 Not Found` khi khách xem ảnh?
  3. 🔄 *[Đánh đổi]:* Lưu ảnh vào DB dạng BLOB (`byte[]`) là tối kỵ vì làm DB phình to hàng chục GB và làm sập cache RAM của DB.
  4. ⚖️ *[Tối ưu CDN]:* Cloudinary tự động nén ảnh WebP và resize ảnh động theo kích thước màn hình điện thoại/máy tính giúp tiết kiệm băng thông ra sao?
  5. 🏢 *[Thực tế]:* Chạy API upload ảnh lên Cloudinary và nhận về link ảnh CDN `https://res.cloudinary.com/...` hiển thị trực tiếp lên trình duyệt!
* **Từ khóa:** `Cloudinary / AWS S3 SDK`, `Object Storage Architecture`, `CDN Image Delivery Optimization`, `Multi-Instance File Storage Solution`.

---

### 📌 Task 50: Bật Tính Năng Chạy Ngầm Bất Đồng Bộ (`@EnableAsync`)
* **Hành động code:** Bật `@EnableAsync`, cấu hình `ThreadPoolTaskExecutor` và viết service gửi email thông báo chạy ngầm.
* **Mục tiêu duy nhất:** Cấu hình Thread Pool chuẩn cho tác vụ chạy ngầm bất đồng bộ.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Async` dùng Spring AOP đẩy việc thực thi method sang một Thread Worker riêng biệt từ `TaskExecutor` ra sao?
  2. ⚠️ *[Bẫy lỗi Thread Pool]:* Mặc định Spring dùng `SimpleAsyncTaskExecutor` (Tạo mới 1 Thread cho mỗi request ngầm và không tái sử dụng) $\rightarrow$ 10.000 đơn hàng sẽ làm sập server do cạn RAM tạo Thread của OS.
  3. ⚖️ *[Cấu hình chuẩn]:* Ý nghĩa của `corePoolSize`, `maxPoolSize`, `queueCapacity` trong `ThreadPoolTaskExecutor`.
  4. 🔄 *[Xử lý lỗi]:* Hàm `@Async` trả về `void` (Fire-and-Forget), làm sao bắt lỗi mất mạng/sai SMTP bằng `AsyncUncaughtExceptionHandler`?
  5. 🏢 *[Thực tế]:* Viết class `AsyncConfig` chuẩn cho môi trường Production.
* **Từ khóa:** `@EnableAsync & @Async`, `ThreadPoolTaskExecutor vs SimpleAsyncTaskExecutor`, `AsyncUncaughtExceptionHandler`.

---

### 📌 Task 51: Tách Rời Logic Bằng Spring Event (`ApplicationEventPublisher`)
* **Hành động code:** Khi tạo đơn thành công, bắn `OrderCreatedEvent` để module thông báo tự động lắng nghe và gửi mail ngầm.
* **Mục tiêu duy nhất:** Áp dụng mô hình Event-Driven Architecture nội bộ và bảo đảm tính toàn vẹn Transaction (`AFTER_COMMIT`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kiến trúc]:* Event-Driven Architecture nội bộ giúp giảm phụ thuộc (Decoupling) giữa `OrderService` và `EmailService` thế nào (`OrderService` không cần tiêm `EmailService`)?
  2. ⚠️ *[Bẫy lỗi Transaction]:* Đơn hàng lưu DB bị lỗi và rollback, làm sao tránh việc email chúc mừng vẫn bị gửi đi bằng `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`?
  3. ⚖️ *[So sánh]:* Đồng bộ (`@EventListener`) vs Bất đồng bộ (`@Async @EventListener`).
  4. 🔄 *[Đánh đổi]:* Tuân thủ nguyên lý Open/Closed (thêm tính năng tích điểm, gửi SMS mà không sửa code OrderService) nhưng khó lần theo vết luồng code hơn.
  5. 🏢 *[Thực tế]:* Tạo `OrderCreatedEvent`, dùng `eventPublisher.publishEvent(...)` trong `OrderServiceImpl`, và tạo `OrderNotificationListener` gửi mail sau khi commit thành công.
* **Từ khóa:** `ApplicationEventPublisher`, `@TransactionalEventListener AFTER_COMMIT`, `Event-Driven Loose Coupling`, `Open/Closed Principle`.

---

## 🚀 GIAI ĐOẠN 9: Test Nâng Cao, Giám Sát, Docker & CI/CD (Tasks 52 - 55)

### 📌 Task 52: Nâng Cấp Integration Test Với Testcontainers (Test PostgreSQL Thật)
* **Hành động code:** Tích hợp `testcontainers-postgresql` để chạy integration test với database PostgreSQL thật trong Docker container thay vì dùng H2 in-memory.
* **Mục tiêu duy nhất:** Kiểm thử ứng dụng trên môi trường Database thật tự động khởi tạo bằng Docker.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Testcontainers]:* Testcontainers tự động kéo Docker Image PostgreSQL và khởi chạy container ảo trong lúc chạy test, sau đó tự hủy container ra sao?
  2. ⚠️ *[Rủi ro H2 Database]:* Tại sao không nên dùng H2 Database in-memory để test cho dự án production chạy PostgreSQL (Khác biệt về cú pháp SQL Dialect, hàm JSONB, Regex)?
  3. ⚖️ *[So sánh]:* Test Mock (Mockito) vs Test với Testcontainers.
  4. 🔄 *[Đánh đổi]:* Testcontainers cực kỳ chân thực nhưng tốn thêm 5-10 giây để kéo và bật container Docker.
  5. 🏢 *[Thực tế]:* Viết Integration Test hoàn chỉnh tạo Order và lưu vào PostgreSQL container ảo.
* **Từ khóa:** `Testcontainers PostgreSQL Spring Boot`, `H2 Database vs Real Database Pitfalls`, `@Testcontainers & @Container`.

---

### 📌 Task 53: Tích Hợp Health Check & Metrics Với Spring Boot Actuator
* **Hành động code:** Thêm `spring-boot-starter-actuator` và cấu hình endpoint giám sát sức khỏe hệ thống trong `application.yml`.
* **Mục tiêu duy nhất:** Giám sát sức khỏe ứng dụng và bảo mật các endpoint nhạy cảm của Actuator.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Endpoint `/actuator/health` kiểm tra Database Connection Pool, Redis, Ổ đĩa Disk Space ra sao?
  2. ⚠️ *[Bảo mật Actuator]:* Endpoint `/actuator/env` (Lộ mật khẩu DB) hoặc `/actuator/heapdump` (Tải toàn bộ RAM) cực kỳ nguy hiểm. Tại sao ở Production chỉ nên mở `health, info, metrics`?
  3. 🔬 *[Hệ sinh thái Giám sát]:* **Actuator/Micrometer** (Thu thập Metrics) $\rightarrow$ **Prometheus** (Lưu trữ Time-Series) $\rightarrow$ **Grafana** (Vẽ biểu đồ CPU, RAM, Latency, TPS).
  4. ⚖️ *[Liveness vs Readiness]:* Phân biệt **Liveness Probe** (App còn sống không) vs **Readiness Probe** (App đã sẵn sàng nhận traffic chưa) cho Kubernetes.
  5. 🏢 *[Thực tế]:* Cấu hình mở `show-details=always` ở môi trường Dev để xem chi tiết tình trạng kết nối DB và Redis.
* **Từ khóa:** `Spring Boot Actuator`, `Actuator Security Best Practices`, `Prometheus & Grafana Ecosystem`, `Liveness and Readiness Probes`.

---

### 📌 Task 54: Đóng Gói Ứng Dụng Với Docker & Docker Compose
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

### 📌 Task 55: Tự Động Hóa CI/CD Pipeline Với GitHub Actions
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

## 🏆 LỘ TRÌNH ĐÃ HOÀN HẢO 100% VỀ MẶT KỸ THUẬT, SƯ PHẠM VÀ THỰC HÀNH!

👉 **Bắt đầu ngay với [Task 1: Cấu Hình application.yml Đa Môi Trường & Chuẩn Hóa URL /api/v1/](file:///Users/kyanh/Documents/Learn/learn_spring/LEARNING_ROADMAP.md#task-1-c%E1%BA%A5u-h%C3%ACnh-applicationyml-%C4%91a-m%C3%B4i-tr%C6%B0%E1%BB%9Dng--chu%E1%BA%A9n-h%C3%B3a-url-apiv1)!**
