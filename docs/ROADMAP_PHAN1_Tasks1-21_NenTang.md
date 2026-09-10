# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN — PHẦN 1: NỀN TẢNG, TESTING SỚM & API DOCS (TASKS 1 - 21)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn $\le 20\%$**: Mỗi bước tiến lên một nấc thang tự nhiên, chuyển tiếp mượt mà, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---

## 🏗️ GIAI ĐOẠN 1: Chuẩn Hóa Cấu Hình, Log SQL & Tầng Dữ Liệu Cơ Bản (Tasks 1 - 8)

### 📌 Task 1: Cấu Hình `application.yml` Đa Môi Trường & Chuẩn Hóa URL `/api/v1/`
* **Hành động code:** 
  - Xóa `application.properties`, tạo `application.yml`, `application-dev.yml` và `application-prod.yml`.
  - Cập nhật toàn bộ `@RequestMapping` trong các Controller sang tiền tố chuẩn `/api/v1/...`.
* **Mục tiêu duy nhất:** Nắm vững cú pháp YAML phân cấp, cách Spring nạp Profile (`spring.profiles.active=dev`) và nạp biến môi trường `${DB_PASSWORD}` an toàn.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* YAML khác gì Properties? Cú pháp thụt đầu dòng (Indentation/Hierarchy) giúp cấu hình lồng nhau (như datasource, jpa) gọn gàng ra sao?
  2. 🔬 *[Cơ chế Spring Profiles]:* Cách Spring tự động kích hoạt file `application-dev.yml` hoặc `application-prod.yml` dựa vào cấu hình `spring.profiles.active=dev`?
  3. ⚠️ *[Bảo mật]:* Tại sao mật khẩu thật của Database tuyệt đối không được gõ cứng vào file cấu hình mà phải dùng cú pháp `${DB_PASSWORD:default_pass}`?
  4. 🔄 *[Đánh đổi]:* Cấu hình YAML đẹp và gọn hơn, nhưng tại sao lỗi thụt lề (sai khoảng trắng space) lại là lỗi phổ biến nhất khiến ứng dụng không khởi động được?
  5. 🏢 *[Thực tế]:* Trong môi trường Production, làm sao DevOps truyền biến môi trường từ Docker/Kubernetes vào ứng dụng Spring Boot?
* **Từ khóa:** `application.yml vs properties`, `Spring Profiles`, `Environment Variable Injection (${ENV_VAR})`, `API Versioning /api/v1/`.

---

### 📌 Task 2: Cấu Hình & Học Cách Đọc Log SQL Hibernate Trong `application-dev.yml` *(KỸ NĂNG SỐNG CÒN)*
* **Hành động code:** Thêm cấu hình `show-sql: true`, `format_sql: true`, và `logging.level.org.hibernate.orm.jdbc.bind: trace` vào `application-dev.yml`.
* **Mục tiêu duy nhất:** Soi tận gốc câu lệnh SQL Hibernate sinh ra và nhìn thấy giá trị thật được truyền vào dấu hỏi chấm `?`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Mặc định Hibernate chỉ hiện `WHERE id = ? AND is_deleted = ?`. Cấu hình `org.hibernate.orm.jdbc.bind: trace` (Hibernate 6) can thiệp vào tầng JDBC Driver để in ra giá trị thật `binding parameter [1] <- [1]` ra sao?
  2. ⚠️ *[Bảo mật]:* Tại sao cấu hình in tham số SQL này **CHỈ ĐƯỢC PHÉP BẬT Ở DEV**, tuyệt đối cấm ở Prod (Nguy cơ rò rỉ mật khẩu, số thẻ tín dụng và dữ liệu cá nhân ra file log)?
  3. ⚖️ *[So sánh]:* `spring.jpa.show-sql=true` (In thẳng ra `System.out`) vs `logging.level.org.hibernate.SQL=debug` (In qua hệ thống SLF4J Logger). Cách nào chuẩn hơn?
  4. 🔄 *[Đánh đổi]:* Bật log SQL chi tiết làm giảm nhẹ hiệu năng I/O của ứng dụng lúc tải cao, nhưng mang lại lợi ích gì trong quá trình phát triển và debug?
  5. 🏢 *[Thực tế]:* Khởi động app, gọi thử 1 API GET và quan sát Terminal để đọc cấu trúc 1 câu query hoàn chỉnh kèm tham số.
* **Từ khóa:** `Hibernate SQL Logging`, `org.hibernate.orm.jdbc.bind=trace`, `Dev vs Prod Logging Security`, `JDBC Parameter Binding`.

---

### 📌 Task 3: Chuẩn Hóa Dependency Injection Bằng Constructor
* **Hành động code:** Kiểm tra toàn bộ Controller và Service, đảm bảo 100% sử dụng `private final ...` kết hợp `@RequiredArgsConstructor` của Lombok (loại bỏ hoàn toàn `@Autowired` trên field nếu có).
* **Mục tiêu duy nhất:** Hiểu sâu về IoC Container, 3 cách tiêm phụ thuộc (DI) và tại sao Constructor Injection là chuẩn mực duy nhất.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất IoC & DI]:* **Inversion of Control (Đảo ngược điều khiển)** là gì? Tại sao ta không tự viết `new ProductServiceImpl()` mà để Spring IoC Container tiêm vào?
  2. ⚠️ *[Tác hại của Field Injection `@Autowired`]:* Tại sao các công ty công nghệ **NGHIÊM CẤM** dùng `@Autowired` trực tiếp trên thuộc tính (Không thể viết Unit Test thuần, phá vỡ tính Immutability `final`)?
  3. ⚖️ *[So sánh 3 cách]:* **Constructor Injection** (Chuẩn mực) vs **Setter Injection** (Cho optional dependencies) vs **Field Injection** (Nguy hiểm).
  4. 🔬 *[Lombok `@RequiredArgsConstructor`]:* Annotation này của Lombok tự động sinh ra Constructor cho những thuộc tính nào (`final` và `@NonNull`)?
  5. 🏢 *[Phỏng vấn]:* Trả lời câu hỏi: *"Tại sao Constructor Injection được khuyến khích nhất trong Spring Boot?"* trong 3 ý chính.
* **Từ khóa:** `Inversion of Control (IoC)`, `Constructor Injection vs Field Injection @Autowired`, `@RequiredArgsConstructor Lombok`, `Immutability in DI`.

---

### 📌 Task 4: Tạo Class Trừu Tượng `BaseEntity`
* **Hành động code:** Tạo `com.example.learn_spring.entity.BaseEntity` chứa 2 trường `createdAt` và `updatedAt`.
* **Mục tiêu duy nhất:** Hiểu cơ chế kế thừa `@MappedSuperclass` trong JPA và chuẩn hóa kiểu dữ liệu thời gian.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất JPA]:* `@MappedSuperclass` khác gì với `@Entity`? Tại sao Hibernate không tạo ra bảng riêng `base_entity` mà nhúng các cột này vào các bảng con?
  2. ⚠️ *[Rủi ro DRY]:* Nếu không có `BaseEntity`, nguy cơ đặt tên cột ngày giờ không đồng nhất (`created_at`, `created_date`, `creation_time`) gây khó khăn gì khi viết query dùng chung?
  3. ⚖️ *[So sánh]:* So sánh `LocalDateTime` (không kèm múi giờ) vs `Instant` (chuẩn UTC Timestamp). Tại sao các hệ thống quốc tế luôn dùng `Instant`?
  4. 🔄 *[Đánh đổi]:* Kế thừa Entity (Inheritance) vs Dùng `@Embeddable` trong JPA.
  5. 🏢 *[Thực tế]:* Trong các hệ thống lớn, ngoài thời gian, `BaseEntity` thường chứa thêm những trường nào (ví dụ: `createdBy`, `updatedBy`, `version`, `isDeleted`)?
* **Từ khóa:** `@MappedSuperclass`, `BaseEntity JPA`, `Instant vs LocalDateTime UTC`.

---

### 📌 Task 5: Cấu Hình JPA Auditing Tự Động Điền Thời Gian
* **Hành động code:** Gắn `@EnableJpaAuditing` ở `LearnSpringApplication.java`, thêm `@EntityListeners(AuditingEntityListener.class)` vào `BaseEntity`, gắn `@CreatedDate` và `@LastModifiedDate`.
* **Mục tiêu duy nhất:** Hiểu cơ chế JPA Lifecycle Callbacks (`@PrePersist`, `@PreUpdate`) và tự động hóa ghi nhận thời gian.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Lifecycle]:* `AuditingEntityListener` can thiệp vào giai đoạn nào của vòng đời Entity để tự động gán giá trị thời gian hiện tại?
  2. ⚠️ *[Bẫy lỗi JPQL]:* Nếu bạn dùng câu lệnh `@Modifying @Query("UPDATE Product p SET p.price = :price WHERE p.id = :id")`, trường `updatedAt` có tự cập nhật không? Tại sao (Bypass JPA Lifecycle)?
  3. ⚖️ *[So sánh]:* Tự sinh ngày giờ ở tầng Application (Java) vs Dùng Default Value ở Database (`DEFAULT CURRENT_TIMESTAMP`). Điểm mạnh/yếu của mỗi cách?
  4. 🔄 *[Đánh đổi]:* Giao việc sinh thời gian cho Java Server có rủi ro gì nếu nhiều server bị lệch đồng hồ phần cứng (Clock drift)?
  5. 🏢 *[Thực tế]:* Cấu hình `AuditorAware<String>` giúp Spring tự động lấy `username` của người đăng nhập hiện tại nạp vào `@CreatedBy` ra sao?
* **Từ khóa:** `@EnableJpaAuditing`, `@EntityListeners`, `AuditingEntityListener`, `JPA Lifecycle Callbacks`.

---

### 📌 Task 6: Kế Thừa `BaseEntity` Cho Các Entity Nghiệp Vụ
* **Hành động code:** Cho `Category`, `Product`, `Customer`, `Order` kế thừa `BaseEntity`.
* **Mục tiêu duy nhất:** Áp dụng kế thừa thực tế và phân tích xem bảng nào NÊN hoặc KHÔNG NÊN kế thừa `BaseEntity`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi một Entity kế thừa `@MappedSuperclass`, Hibernate ánh xạ các cột của class cha vào bảng con như thế nào khi sinh câu `CREATE TABLE`?
  2. ⚠️ *[Rủi ro Thiết kế]:* `OrderItem` có nên kế thừa `BaseEntity` không? Một dòng chi tiết đơn hàng (đã chốt khi mua) có bao giờ được "cập nhật" thời gian (`updatedAt`) không?
  3. ⚖️ *[So sánh]:* Muốn đổi tên cột trong bảng con (ví dụ bảng `orders` muốn cột ngày tạo tên là `order_date` thay vì `created_at`), ta dùng annotation gì (`@AttributeOverride`)?
  4. 🔄 *[Đánh đổi]:* Việc tự động ghi nhận `updatedAt` mỗi lần gọi `save()` có làm tăng nhẹ chi phí I/O ghi đĩa của Database không?
  5. 🏢 *[Thực tế]:* Trong các hệ thống kế toán/tài chính, tại sao bảng lịch sử giao dịch tuyệt đối không có hàm update (Append-Only Log)?
* **Từ khóa:** `@AttributeOverride`, `Entity Inheritance Design`, `Append-Only Data Architecture`.

---

### 📌 Task 7: Thêm Cờ Xóa Mềm (`isDeleted`) & Tự Động Hóa Với `@SQLDelete`
* **Hành động code:** Thêm `private boolean isDeleted = false;` vào `Category` và `Product`, cấu hình `@SQLDelete` và `@SQLRestriction("is_deleted = false")`.
* **Mục tiêu duy nhất:** Hiểu bản chất Xóa Mềm (Soft Delete) và cách Hibernate tự động chèn điều kiện lọc.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Xóa vật lý (Hard Delete - `DELETE FROM`) tác động lên B-Tree Index thế nào so với Xóa logic (Soft Delete - `UPDATE is_deleted = true`)?
  2. ⚠️ *[Bẫy lỗi Unique]:* Xử lý lỗi trùng Unique Constraint khi danh mục cũ đã bị xóa mềm nhưng người dùng tạo danh mục mới trùng tên?
  3. 🔬 *[Cơ chế `@SQLRestriction`]:* Hibernate 6 tự động chèn thêm điều kiện `AND is_deleted = false` vào các câu `SELECT` ở tầng nào?
  4. 🔄 *[Đánh đổi]:* `@SQLRestriction` chặn đọc dữ liệu đã xóa mềm, làm sao để viết API cho Admin xem danh sách "Thùng rác" để Khôi phục (Restore)?
  5. 🏢 *[Thực tế]:* Viết API `PATCH /api/v1/products/{id}/restore` để khôi phục sản phẩm đã bị xóa mềm.
* **Từ khóa:** `@SQLDelete`, `@SQLRestriction`, `Soft Delete vs Hard Delete`, `Unique Constraint with Soft Delete`.

---

### 📌 Task 8: (MỚI) Tự Động Seed Dữ Liệu Mẫu Cho Dev Với `CommandLineRunner` & Java Faker
* **Hành động code:** Tạo class `DataSeeder` implement `CommandLineRunner`, gắn `@Profile("dev")`, kiểm tra nếu DB chưa có danh mục thì tự động sinh 10 danh mục và 50 sản phẩm mẫu.
* **Mục tiêu duy nhất:** Hiểu vòng đời khởi động ứng dụng Spring Boot và tự động hóa việc chuẩn bị dữ liệu kiểm thử cục bộ.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Vòng đời]:* `CommandLineRunner` và `ApplicationRunner` được Spring IoC Container kích hoạt chính xác ở thời điểm nào (Trước hay sau khi Tomcat hoàn tất lắng nghe cổng 8080)?
  2. ⚠️ *[Nguy cơ Production]:* Tại sao class Seeder này **BẮT BUỘC** phải có `@Profile("dev")` hoặc `@Profile("!prod")` (Nguy cơ ghi đè dữ liệu rác vào Database khách hàng thật)?
  3. ⚖️ *[So sánh]:* Dùng Java `CommandLineRunner` vs Viết script `data.sql` / file migration `R__seed_data.sql`. Khi nào nên dùng cách nào?
  4. 🔄 *[Tính Idempotent]:* Làm sao để Seeder chạy an toàn mỗi khi khởi động lại app mà không bị sinh dữ liệu trùng lặp (Kiểm tra `repository.count() == 0`)?
  5. 🏢 *[Thực tế]:* Khởi động app với profile `dev`, kiểm tra trong console dòng log "Đã nạp 50 sản phẩm mẫu thành công" và mở DB kiểm chứng.
* **Từ khóa:** `CommandLineRunner`, `ApplicationRunner`, `Data Seeder`, `Spring Profiles Separation`, `Idempotent Seeding`.

---

## 🧪 GIAI ĐOẠN 2: Testing Sớm, Phân Trang & Sắp Xếp Dữ Liệu (Tasks 9 - 15)

### 📌 Task 9: Viết Unit Test JUnit 5 Cho `CategoryMapper` *(BẮT ĐẦU TEST SỚM)*
* **Hành động code:** Tạo `src/test/java/.../CategoryMapperTest.java` kiểm tra các hàm `toEntity()`, `toResponse()`.
* **Mục tiêu duy nhất:** Nắm vững cấu trúc bài test chuẩn AAA (Arrange - Act - Assert) và kiểm tra tính an toàn Null Pointer.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kim tự tháp Test]:* Tại sao Unit Test cho Mapper không cần khởi động Spring Context mà chỉ cần chạy bằng JUnit thuần (tốc độ dưới 5ms)?
  2. 🔬 *[Cấu trúc AAA]:* Áp dụng **Arrange (Chuẩn bị) $\rightarrow$ Act (Hành động) $\rightarrow$ Assert (Khẳng định)** trong bài test.
  3. ⚠️ *[Bẫy lỗi Null Safety]:* Viết test case: Khi truyền `null` vào `toResponse(null)`, mapper trả về `null` an toàn thay vì ném `NullPointerException`.
  4. ⚖️ *[So sánh]:* `Assertions.assertEquals` của JUnit 5 vs Thư viện Fluent Assertions `AssertJ` (`assertThat(...).isEqualTo(...)`).
  5. 🏢 *[Thực tế]:* Chạy lệnh `./mvnw test` để thấy bài test đầu tiên chạy xanh mướt (`BUILD SUCCESS`)!
* **Từ khóa:** `JUnit 5 @Test`, `Assertions.assertEquals vs AssertJ`, `AAA Pattern`, `Null Safety Testing`.

---

### 📌 Task 10: Viết Unit Test Mockito Cho `CategoryServiceImpl` *(HỌC MOCKITO SỚM)*
* **Hành động code:** Tạo `CategoryServiceImplTest.java` sử dụng `@Mock CategoryRepository` và `@InjectMocks CategoryServiceImpl`.
* **Mục tiêu duy nhất:** Hiểu bản chất Mocking (Giả lập tầng phụ thuộc) và test cả 2 luồng: Thành công & Ném ngoại lệ.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Mocking]:* Tại sao khi test Service bắt buộc phải "giả lập" (Mock) Repository mà không được kết nối DB thật (Tính độc lập & Tốc độ)?
  2. 🔬 *[Kỹ thuật Stubbing]:* `when(categoryRepository.findById(1L)).thenReturn(Optional.of(category))` chỉ định cho Mockito làm gì?
  3. ⚠️ *[Test Luồng Lỗi]:* Dùng `assertThrows(AppException.class, () -> categoryService.getCategoryById(99L))` để kiểm tra ném đúng `CATEGORY_NOT_FOUND`.
  4. 🔬 *[Xác minh Hành vi]:* Phương thức `verify(categoryRepository, times(1)).save(any())` dùng để làm gì?
  5. 🏢 *[Thực tế]:* Viết đầy đủ test tạo thành công và test thất bại do trùng tên (`CATEGORY_NAME_EXISTED`).
* **Từ khóa:** `Mockito @Mock & @InjectMocks`, `Stubbing when().thenReturn()`, `assertThrows Testing`, `verify() Behavior`.

---

### 📌 Task 11: Thiết Kế Generic DTO `PageResponse<T>`
* **Hành động code:** Tạo Generic Class `PageResponse<T>` chứa metadata phân trang: `content`, `pageNo`, `pageSize`, `totalElements`, `totalPages`, `isLast`.
* **Mục tiêu duy nhất:** Chuẩn hóa cấu trúc JSON trả về cho mọi API phân trang trong hệ thống.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao không nên trả trực tiếp `org.springframework.data.domain.Page<T>` của Spring ra ngoài Controller (Tránh rò rỉ cấu trúc nội bộ framework)?
  2. ⚠️ *[Bẫy lỗi]:* Danh sách rỗng (0 bản ghi) thì `totalPages` bằng 0 hay 1? `isFirst`, `isLast` bằng bao nhiêu?
  3. ⚖️ *[So sánh]:* **Offset Pagination** (`page`, `size`) vs **Cursor Pagination** (`limit`, `nextCursor`). Khi nào dùng Cursor (ví dụ: Newsfeed TikTok/Facebook)?
  4. 🔄 *[Đánh đổi]:* Câu query đếm `SELECT COUNT(*)` ảnh hưởng hiệu năng ra sao khi bảng có 10 triệu dòng?
  5. 🏢 *[Thực tế]:* Kỹ thuật dùng `Slice<T>` thay vì `Page<T>` để tránh câu `SELECT COUNT(*)` cho tính năng "Xem thêm / Infinite Scroll".
* **Từ khóa:** `PageResponse Generic DTO`, `Page vs Slice Spring Data JPA`, `Offset vs Cursor Pagination`, `Count Query Performance`.

---

### 📌 Task 12: Tích Hợp `Pageable` Vào Tầng Repository & Service
* **Hành động code:** Cập nhật hàm `getAllProducts(int page, int size, String sortBy, String sortDir)` trong `ProductService`.
* **Mục tiêu duy nhất:** Sử dụng `PageRequest.of(page, size, sort)` và ánh xạ `Page<Product>` sang `PageResponse<ProductResponse>`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Spring Data]:* Khi truyền `Pageable` vào hàm `productRepository.findAll(pageable)`, Spring tự sinh ra mấy câu lệnh SQL (1 câu `SELECT ... LIMIT ... OFFSET` và 1 câu `SELECT COUNT(*)`)?
  2. ⚠️ *[Bẫy lỗi Số trang]:* Spring Data JPA đánh số trang bắt đầu từ `0` (`0-indexed`), còn người dùng truyền lên từ `1` (`1-indexed`). Xử lý `page - 1` ở đâu để không bị lỗi `IllegalArgumentException`?
  3. ⚖️ *[So sánh]:* Tạo đối tượng `Sort.by(direction, sortBy)` an toàn vs Truyền chuỗi Sort tự do.
  4. 🔄 *[Tối ưu]:* Nếu `page = 0` và số bản ghi trả về nhỏ hơn `size`, Spring Data có bỏ qua câu `SELECT COUNT(*)` không?
  5. 🏢 *[Thực tế]:* Viết hàm chuyển đổi ánh xạ từ `Page<Product>` sang `PageResponse<ProductResponse>`.
* **Từ khóa:** `Pageable`, `PageRequest.of()`, `Sort.Direction`, `Page Mapping`.

---

### 📌 Task 13: Thêm Endpoint Phân Trang Ở Controller & Giới Hạn Kích Thước Trang
* **Hành động code:** Thêm endpoint `GET /api/v1/products` với các `@RequestParam(defaultValue = "1") int page`, `@RequestParam(defaultValue = "10") int size`.
* **Mục tiêu duy nhất:** Kiểm soát tham số đầu vào và bảo vệ server khỏi tấn công Out-Of-Memory do client đòi lấy quá nhiều dữ liệu.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Tham số]:* Ý nghĩa của `defaultValue` trong `@RequestParam` giúp API hoạt động như thế nào khi client không truyền tham số phân trang?
  2. ⚠️ *[Lỗ hổng DoS / OOM]:* Nếu client ác ý truyền `?size=10000000`, chuyện gì xảy ra với heap memory (RAM) của JVM?
  3. 🛡️ *[Giải pháp Giới hạn]:* Cách đặt trần cứng: `if (size > 100) size = 100;` hoặc validate `@Max(100)` để bảo vệ server.
  4. ⚖️ *[So sánh]:* Dùng đối tượng `Pageable` trực tiếp làm tham số của Controller vs Bóc tách từng `@RequestParam` riêng biệt. Ưu nhược điểm?
  5. 🏢 *[Thực tế]:* Gọi Postman với `?page=1&size=5` và kiểm tra cấu trúc JSON trả về.
* **Từ khóa:** `@RequestParam DefaultValue`, `Pagination DoS Prevention`, `Page Size Upper Bound`, `Memory Protection`.

---

### 📌 Task 14: Xử Lý Ngoại Lệ Tham Số Sắp Xếp Không Hợp Lệ
* **Hành động code:** Kiểm tra tham số `sortBy` xem có thuộc danh sách cột cho phép sắp xếp (`id`, `name`, `price`, `createdAt`) hay không; nếu không hợp lệ, ném ngoại lệ rõ ràng.
* **Mục tiêu duy nhất:** Ngăn chặn lỗi ném ra từ Hibernate khi client truyền tên cột không tồn tại (`PropertyReferenceException`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất lỗi]:* Nếu client truyền `?sortBy=hacker_column`, tại sao Hibernate ném `PropertyReferenceException: No property 'hacker_column' found for type 'Product'` và trả về 500?
  2. ⚠️ *[Bảo mật]:* Việc để lộ tên thuộc tính Entity trong stacktrace lỗi 500 vi phạm nguyên tắc bảo mật Information Disclosure ra sao?
  3. 🛡️ *[Kỹ thuật Whitelist]:* Sử dụng `Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "price", "createdAt");` để kiểm tra trước khi tạo `Sort`.
  4. ⚖️ *[So sánh]:* Báo lỗi 400 Bad Request ngay vs Tự động fallback về sắp xếp mặc định (`id, DESC`). Trường hợp nào tốt hơn cho trải nghiệm người dùng?
  5. 🏢 *[Thực tế]:* Truyền `?sortBy=invalid_column` và nhận về mã lỗi chuẩn `400 Bad Request` kèm thông điệp rõ ràng.
* **Từ khóa:** `PropertyReferenceException`, `Sort Whitelisting`, `Information Disclosure Prevention`, `Input Validation`.

---

### 📌 Task 15: Bổ Sung Validation Định Dạng Cho `CustomerRequest`
* **Hành động code:** Thêm các annotation `@NotBlank`, `@Email`, `@Size(min = 3, max = 50)` vào `CustomerRequest`.
* **Mục tiêu duy nhất:** Nắm vững chuẩn Jakarta Bean Validation và bảo vệ tính toàn vẹn của dữ liệu ngay tại cổng vào Controller.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@NotNull` vs `@NotEmpty` vs `@NotBlank` khác nhau thế nào? Tại sao trường chuỗi (String) như `name`, `password` luôn phải dùng `@NotBlank`?
  2. ⚠️ *[Bẫy quên `@Valid`]:* Nếu viết `@RequestBody CustomerRequest request` mà quên gắn `@Valid` thì các annotation trong DTO có tác dụng không?
  3. ⚖️ *[So sánh]:* Validate ở tầng Controller (DTO) vs Validate ở tầng Entity (`@Column(nullable = false)`). Tại sao phải làm ở DTO trước?
  4. 🔄 *[Đánh đổi]:* Thêm nhiều rule validation có làm chậm request không? (Chi phí tính toán Regex trong microsecond so với chi phí gọi DB).
  5. 🏢 *[Thực tế]:* Gửi body rỗng lên API tạo khách hàng và quan sát Spring ném lỗi `MethodArgumentNotValidException`.
* **Từ khóa:** `Jakarta Bean Validation`, `@NotBlank vs @NotEmpty vs @NotNull`, `@Valid Annotation`, `DTO Validation Layer`.

---

## 🛡️ GIAI ĐOẠN 3: Custom Validation, Exception Chuẩn & API Docs (Tasks 16 - 21)

### 📌 Task 16: Tạo Custom Annotation `@PhoneNumber`
* **Hành động code:** Tạo annotation `@PhoneNumber` và class `PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String>`.
* **Mục tiêu duy nhất:** Tự viết một quy tắc Validation tùy biến theo chuẩn số điện thoại Việt Nam (10 chữ số, bắt đầu bằng đầu số hợp lệ).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cơ chế Hoạt động]:* `ConstraintValidator` kết nối với Jakarta Bean Validation Engine qua cơ chế nào để tự động thực thi hàm `isValid()`?
  2. ⚠️ *[Bẫy lỗi Null]:* Trong hàm `isValid(String value, ...)`, nếu `value == null` thì nên trả về `true` hay `false`? Tại sao quy ước của Bean Validation là để `@NotNull` kiểm tra null riêng?
  3. 🔬 *[Tối ưu Regex]:* Tại sao `Pattern.compile(...)` nên được khai báo là `private static final` thay vì gọi `value.matches(regex)` trong mỗi request?
  4. 🔄 *[Đánh đổi]:* Viết Custom Annotation tốn công hơn viết `if/else` trong Service, nhưng đem lại khả năng tái sử dụng trên nhiều DTO ra sao?
  5. 🏢 *[Thực tế]:* Áp dụng `@PhoneNumber` vào `CustomerRequest`, test thử với `0912345678` (hợp lệ) và `12345` (báo lỗi).
* **Từ khóa:** `Custom ConstraintValidator`, `@Constraint Annotation`, `Compiled Regex Optimization`, `Validation Reusability`.

---

### 📌 Task 17: Chuẩn Hóa Thông Báo Lỗi Validation Trong `GlobalExceptionHandler`
* **Hành động code:** Bắt `MethodArgumentNotValidException`, trích xuất toàn bộ lỗi của từng field và trả về JSON chuẩn chứa `Map<String, String> errors`.
* **Mục tiêu duy nhất:** Giúp Frontend/Mobile App hiển thị chính xác thông báo lỗi dưới từng ô nhập liệu của người dùng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Cấu trúc của `MethodArgumentNotValidException` chứa thông tin gì (BindingResult, FieldErrors, RejectedValues)?
  2. ⚠️ *[Trải nghiệm Người dùng]:* Tại sao trả về lỗi chung chung `"Dữ liệu không hợp lệ"` là tồi tệ đối với Frontend/Mobile?
  3. ⚖️ *[So sánh]:* Chỉ trả về lỗi đầu tiên gặp phải vs Trả về danh sách tất cả các trường bị lỗi cùng một lúc.
  4. 🔄 *[Đa ngôn ngữ]:* Làm sao để thông điệp lỗi tự động dịch sang Tiếng Anh/Tiếng Việt dựa vào Header `Accept-Language`?
  5. 🏢 *[Thực tế]:* Gửi payload thiếu cả `email`, `phone` và `name` $\rightarrow$ Nhận về JSON chứa danh sách lỗi chi tiết cho cả 3 trường.
* **Từ khóa:** `@RestControllerAdvice`, `MethodArgumentNotValidException`, `BindingResult & FieldError`, `Structured Error Response`.

---

### 📌 Task 18: (MỚI) Tự Động Hóa Tài Liệu API Với Swagger / OpenAPI (`springdoc-openapi`)
* **Hành động code:** Thêm dependency `springdoc-openapi-starter-webmvc-ui`, cấu hình metadata `OpenAPI` Bean (Title, Version, Description, Contact), gắn `@Tag`, `@Operation`, `@ApiResponse` vào Controller.
* **Mục tiêu duy nhất:** Tự động sinh tài liệu API chuẩn quốc tế OpenAPI 3.0 và kiểm thử trực quan qua giao diện Swagger UI tại `/swagger-ui.html`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `springdoc-openapi` tự động phân tích Controller, DTO, Type hints và Java Reflection để sinh ra file đặc tả `json/yaml` tại `/v3/api-docs` như thế nào?
  2. ⚠️ *[Bảo mật]:* Tại sao ở môi trường Production, đường dẫn `/swagger-ui.html` và `/v3/api-docs` bắt buộc phải được bảo vệ bằng Spring Security hoặc tắt hoàn toàn (`springdoc.api-docs.enabled=false`)?
  3. ⚖️ *[So sánh]:* Viết tài liệu API thủ công bằng Postman/Notion vs Tự động sinh tài liệu đồng bộ từ code bằng Swagger UI.
  4. 🔄 *[Mô tả Schema]:* Dùng `@Schema(description = "Số điện thoại Việt Nam", example = "0987654321")` trên DTO giúp Frontend hiểu nhanh nghiệp vụ ra sao?
  5. 🏢 *[Thực tế]:* Mở trình duyệt tại `http://localhost:8080/swagger-ui.html`, bấm "Try it out" gọi thử API GET và POST ngay trên giao diện web.
* **Từ khóa:** `springdoc-openapi`, `Swagger UI (/swagger-ui.html)`, `OpenAPI 3.0 Specification`, `@Operation & @ApiResponse`, `API Documentation Security`.

---

### 📌 Task 19: Viết Integration Test Cho Controller Với `MockMvc` *(HỌC INTEGRATION TEST TẠI ĐÂY)*
* **Hành động code:** Tạo `CustomerControllerTest.java` sử dụng `@WebMvcTest(CustomerController.class)`, tiêm `MockMvc` và `@MockBean CustomerService`.
* **Mục tiêu duy nhất:** Kiểm thử toàn diện tầng Web (Routing, Serialization JSON, Validation, HTTP Status Code) mà không cần bật cả ứng dụng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `@WebMvcTest`]:* Slice Test `@WebMvcTest` khác gì `@SpringBootTest` (Chỉ nạp Controller và Filter, không nạp Service, Repository, Database $\rightarrow$ Tốc độ cực nhanh)?
  2. 🔬 *[Kỹ thuật `MockMvc`]:* Dùng `mockMvc.perform(post(...).contentType(...).content(...))` để mô phỏng một HTTP Request thật sự ra sao?
  3. ⚠️ *[Khẳng định Kết quả]:* Dùng `andExpect(status().isBadRequest())` và `andExpect(jsonPath("$.errors.email").exists())` để kiểm tra JSON trả về.
  4. ⚖️ *[So sánh]:* Unit Test (Task 9, 10) vs Integration Test lát cắt WebMvc (Task 19) vs Full System Test (`@SpringBootTest`).
  5. 🏢 *[Thực tế]:* Viết 1 test case gửi request hợp lệ (trả về 201 Created) và 1 test case gửi sai format (trả về 400 Bad Request).
* **Từ khóa:** `@WebMvcTest`, `MockMvc Testing`, `@MockBean / @MockitoBean`, `JsonPath Assertion`, `Controller Slice Testing`.

---

### 📌 Task 20: Bắt Lỗi Trùng Lặp Tầng Database (`DataIntegrityViolationException`)
* **Hành động code:** Đánh `UNIQUE` cho cột `email` và `phone` trong database; thêm hàm bắt `DataIntegrityViolationException` trong `GlobalExceptionHandler`.
* **Mục tiêu duy nhất:** Xử lý triệt để tình huống Race Condition khi 2 request cùng đăng ký 1 email tại cùng một thời điểm.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Race Condition]:* Nếu 2 request cùng kiểm tra `existsByEmail("a@gmail.com") == false` cùng lúc, cả 2 cùng vượt qua tầng Service $\rightarrow$ Cái gì là chốt chặn cuối cùng ngăn dữ liệu rác?
  2. ⚠️ *[Lỗi 500]:* Nếu không bắt `DataIntegrityViolationException`, Spring sẽ ném lỗi 500 kèm thông điệp SQL thô thiển ra ngoài màn hình người dùng.
  3. 🔬 *[Bóc tách Lỗi]:* Làm sao để phân tích thông báo lỗi từ Database Driver (PostgreSQL/MySQL) để biết chính xác là trùng cột `email` hay trùng cột `phone`?
  4. 🔄 *[Đánh đổi]:* Kiểm tra trước bằng `existsByEmail()` (thân thiện hơn nhưng tốn 1 câu SELECT) vs Dựa hoàn toàn vào Unique Constraint của DB (nhanh hơn nhưng phải parse lỗi).
  5. 🏢 *[Thực tế]:* Gửi 2 request trùng email cùng lúc và nhận về mã lỗi chuẩn `409 Conflict` kèm thông điệp: "Email này đã được sử dụng".
* **Từ khóa:** `DataIntegrityViolationException`, `Unique Constraint in DB`, `Database-level Race Condition`, `HTTP 409 Conflict`.

---

### 📌 Task 21: Ghi Log Chuẩn (SLF4J) & Tự Động Gắn `Trace-Id` Với MDC Filter
* **Hành động code:** Tạo `TraceIdFilter` kế thừa `OncePerRequestFilter`, sinh `UUID.randomUUID()` và đưa vào `MDC.put("traceId", traceId)`, cấu hình log pattern hiển thị `%X{traceId}`.
* **Mục tiêu duy nhất:** Đảm bảo mọi dòng log sinh ra từ cùng 1 HTTP Request đều có chung một mã định danh duy nhất để dò vết lỗi (Log Tracing).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất MDC]:* **Mapped Diagnostic Context (MDC)** của SLF4J lưu trữ dữ liệu ở đâu (ThreadLocal) và tự động gắn vào log ra sao?
  2. ⚠️ *[Bẫy lỗi Memory Leak]:* Tại sao trong khối `finally` của Filter **BẮT BUỘC** phải gọi `MDC.clear()` (Nguy cơ rò rỉ dữ liệu sang request khác khi Thread Pool tái sử dụng thread)?
  3. ⚖️ *[So sánh]:* Dùng `System.out.println` (Cấm kỵ tuyệt đối - đồng bộ, nghẽn I/O) vs Dùng `log.info(...)` của SLF4J/Logback (Bất đồng bộ, hỗ trợ định dạng, phân cấp Log Level).
  4. 🔄 *[Header phản hồi]:* Thêm `response.setHeader("X-Trace-Id", traceId)` giúp người dùng cung cấp mã lỗi cho bộ phận hỗ trợ kỹ thuật dễ dàng ra sao?
  5. 🏢 *[Thực tế]:* Gọi 1 request bất kỳ và nhìn vào console: Mọi dòng log từ Controller, Service đến SQL đều có tiền tố `[traceId=a1b2c3d4]`.
* **Từ khóa:** `SLF4J & Logback`, `MDC (Mapped Diagnostic Context)`, `ThreadLocal Memory Leak Prevention`, `Trace-Id Request Correlation`.
