# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN (CHUẨN SINGLE RESPONSIBILITY & KHUNG 5D)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn 20%**: Mỗi bước tiến lên một nấc thang tự nhiên, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---

## 🏗️ GIAI ĐOẠN 1: Chuẩn Hóa Cấu Hình, Log SQL & Tầng Dữ Liệu Cơ Bản (Tasks 1 - 7)

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

## 🧪 GIAI ĐOẠN 2: Testing Sớm, Phân Trang & Sắp Xếp Dữ Liệu (Tasks 8 - 13)

### 📌 Task 8: Viết Unit Test JUnit 5 Cho `CategoryMapper` *(BẮT ĐẦU TEST SỚM)*
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

### 📌 Task 9: Viết Unit Test Mockito Cho `CategoryServiceImpl` *(HỌC MOCKITO SỚM)*
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

### 📌 Task 10: Thiết Kế Generic DTO `PageResponse<T>`
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

### 📌 Task 11: Tích Hợp `Pageable` Vào Tầng Repository & Service
* **Hành động code:** Cập nhật hàm `getAllProducts(int page, int size, String sortBy, String sortDir)` trong `ProductService`.
* **Mục tiêu duy nhất:** Sử dụng `PageRequest.of(page, size, sort)` và ánh xạ `Page<Product>` sang `PageResponse<ProductResponse>`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Spring Data JPA tự động dịch `PageRequest.of(page, size, sort)` thành mệnh đề `LIMIT ? OFFSET ? ORDER BY ?` trong PostgreSQL ra sao?
  2. ⚠️ *[Bẫy lệch chỉ số]:* Tại sao trong Java chỉ số trang bắt đầu từ `0` (Zero-indexed), nhưng Client truyền lên trang `1` (One-indexed)? Xử lý việc lệch 1 đơn vị này ở đâu là sạch nhất?
  3. ⚖️ *[So sánh]:* Sắp xếp theo nhiều cột cùng lúc (ví dụ: Ưu tiên `price DESC`, nếu bằng giá thì xếp `createdAt DESC`) trong `Sort.by(...)` thế nào?
  4. 🔄 *[Đánh đổi]:* Hiện tượng "Offset Skew / Data Drift": Người dùng xem trang 1, có 5 sản phẩm mới thêm vào, bấm sang trang 2 bị nhìn trùng lại 5 sản phẩm cũ.
  5. 🏢 *[Thực tế]:* Viết hàm Mapper tiện ích chuyển đổi `Page<Entity>` thành `PageResponse<Dto>` ngắn gọn bằng Java Stream.
* **Từ khóa:** `PageRequest.of`, `Sort Multiple Columns`, `Offset Pagination Data Drift`, `Page Mapper Utility`.

---

### 📌 Task 12: Thêm Endpoint Phân Trang Ở Controller & Giới Hạn Kích Thước Trang
* **Hành động code:** Endpoint `GET /api/v1/products?page=1&size=10&sortBy=price&sortDir=desc`.
* **Mục tiêu duy nhất:** Nhận tham số phân trang ở Controller và phòng chống tấn công DoS qua tham số `size`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@RequestParam(defaultValue = "...")` hoạt động ra sao khi Client không truyền tham số lên URL?
  2. ⚠️ *[Bẫy lỗi DoS]:* Nếu User cố tình truyền `size=1000000` (1 triệu), Server sẽ bị gì nếu không giới hạn `size` tối đa (ví dụ tối đa 100)?
  3. ⚖️ *[So sánh]:* Gom tham số vào 1 DTO `PageFilterRequest` vs Truyền từng `@RequestParam` rời rạc.
  4. 🏢 *[Thực tế]:* Dùng `@ParameterObject` của SpringDoc để hiển thị thanh phân trang đẹp mắt trên Swagger UI.
  5. 🎯 *[Thử Thách Tự Luyện - Solo Challenge]:* Tự mở rộng phân trang cho `Customer` và `Category` theo đúng chuẩn vừa học!
* **Từ khóa:** `@RequestParam defaultValue`, `Page Size DoS Attack Prevention`, `@ParameterObject Springdoc`.

---

### 📌 Task 13: Xử Lý Ngoại Lệ Tham Số Sắp Xếp Không Hợp Lệ
* **Hành động code:** Bắt `PropertyReferenceException` trong `GlobalExceptionHandler` khi Client truyền `sortBy` sai tên cột.
* **Mục tiêu duy nhất:** Bảo vệ an toàn cấu trúc Database và trả về lỗi `400 Bad Request` thân thiện.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi Client truyền `sortBy=unknown_column`, Hibernate ném ra ngoại lệ gì (`PropertyReferenceException`)?
  2. ⚠️ *[Bẫy lỗi bảo mật]:* Không bắt ngoại lệ này sẽ trả về lỗi 500 kèm cả đoạn StackTrace làm lộ cấu trúc DB ra sao?
  3. ⚖️ *[So sánh]:* Dùng Whitelist Sort Columns (chỉ cho sort theo `name`, `price`, `createdAt`) vs Để tự do.
  4. 🔄 *[Đánh đổi]:* Whitelist đòi hỏi viết thêm vài dòng code kiểm tra nhưng đổi lại tính an toàn tuyệt đối.
  5. 🏢 *[Thực tế]:* Bắt lỗi và trả về `400 Bad Request` với message: "Trường sắp xếp không hợp lệ".
* **Từ khóa:** `PropertyReferenceException`, `Sort Whitelisting Security`, `GlobalExceptionHandler Property Error`.

---

## 🛡️ GIAI ĐOẠN 3: Validation, Integration Test & Logging Truy Vết (Tasks 14 - 19)

### 📌 Task 14: Bổ Sung Validation Định Dạng Cho `CustomerRequest`
* **Hành động code:** Validate Email chuẩn và Số điện thoại Việt Nam bằng Regex (`@Pattern`).
* **Mục tiêu duy nhất:** Nắm vững Bean Validation với Regex và phân biệt tầng kiểm tra dữ liệu.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Pattern(regexp = "...")` của Bean Validation sử dụng `java.util.regex.Pattern` đối soát chuỗi ra sao?
  2. ⚠️ *[Bẫy lỗi]:* `@Email` mặc định đôi khi chấp nhận email dị dạng (`user@localhost`), cách viết Regex Email nghiêm ngặt?
  3. ⚖️ *[So sánh]:* Validate ở Frontend vs Backend vs Database CHECK constraint. Tại sao bắt buộc luôn validate ở Backend?
  4. 🔄 *[Đánh đổi]:* Regex phức tạp có nguy cơ bị tấn công ReDoS (Regular Expression Denial of Service) không?
  5. 🏢 *[Thực tế]:* Viết Regex số điện thoại hỗ trợ tất cả các đầu số hiện nay của Việt Nam (`03, 05, 07, 08, 09`).
* **Từ khóa:** `Bean Validation @Pattern`, `ReDoS Vulnerability`, `Vietnam Phone Number Regex`, `Backend vs Frontend Validation`.

---

### 📌 Task 15: Tạo Custom Annotation `@PhoneNumber`
* **Hành động code:** Tự viết `@PhoneNumber` và `PhoneNumberValidator implements ConstraintValidator`.
* **Mục tiêu duy nhất:** Tự xây dựng Custom Annotation tái sử dụng trong hệ thống.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Constraint(validatedBy = ...)` liên kết Annotation và class xử lý logic như thế nào?
  2. ⚠️ *[Bẫy lỗi Null]:* Trong `isValid()`, nếu giá trị truyền vào là `null`, validator nên trả về `true` hay `false` (Trách nhiệm check `null` thuộc về `@NotNull` hay `@PhoneNumber`)?
  3. ⚖️ *[So sánh]:* Tạo Custom Annotation vs Copy paste Regex dán vào 10 DTO.
  4. 🔄 *[Đánh đổi]:* Khi nào nên viết Custom Validator, khi nào dùng annotation có sẵn?
  5. 🏢 *[Thực tế]:* Truyền tham số động vào Custom Annotation (`@PhoneNumber(allowLandline = true)`).
* **Từ khóa:** `Custom ConstraintValidator`, `@Constraint`, `Bean Validation Context Null Handling`.

---

### 📌 Task 16: Chuẩn Hóa Thông Báo Lỗi Validation Trong `GlobalExceptionHandler`
* **Hành động code:** Bắt `MethodArgumentNotValidException` và trả về Map `{ "email": "...", "phoneNumber": "..." }`.
* **Mục tiêu duy nhất:** Format danh sách lỗi validation thành cấu trúc JSON rõ ràng cho Frontend.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Exception Handling]:* `@RestControllerAdvice` và `@ExceptionHandler` bắt lỗi tập trung qua `ExceptionHandlerExceptionResolver` của `DispatcherServlet` ra sao?
  2. ⚠️ *[Bẫy lỗi]:* 1 trường dính 2 lỗi (vừa `@NotBlank` vừa `@Size(min=5)`), xử lý để không bị ghi đè thông báo?
  3. ⚖️ *[So sánh]:* Định dạng lỗi dạng Map `{ field: error }` vs dạng Mảng `[ { "field": "...", "message": "..." } ]`.
  4. 🔄 *[Đánh đổi]:* Gom tất cả lỗi trả về 1 lần (Collect All Errors) thân thiện cho Frontend nhưng tốn thêm chi phí duyệt lỗi.
  5. 🏢 *[Thực tế]:* Đọc thông điệp lỗi đa ngôn ngữ (i18n) từ file `messages.properties` thông qua `MessageSource`.
* **Từ khóa:** `MethodArgumentNotValidException`, `BindingResult`, `FieldErrors Formatting`, `Spring Boot i18n Validation`.

---

### 📌 Task 17: Viết Integration Test Cho Controller Với `MockMvc` *(HỌC INTEGRATION TEST TẠI ĐÂY)*
* **Hành động code:** Tạo `CategoryControllerTest.java` sử dụng `@WebMvcTest(CategoryController.class)` và `MockMvc`.
* **Mục tiêu duy nhất:** Kiểm thử tích hợp tầng Controller, HTTP Status Code và kiểm tra JSON response.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất MockMvc]:* `MockMvc` giả lập môi trường Servlet Container (Tomcat) như thế nào mà không cần mở cổng mạng 8080 thật?
  2. 🔬 *[Phạm vi Test]:* So sánh `@WebMvcTest` (Slice Test - Chỉ khởi động Controller + Filter) vs `@SpringBootTest + @AutoConfigureMockMvc` (Full Test).
  3. ⚠️ *[Kiểm tra JSON & Validation]:* Viết test case: Gửi body rỗng `{ "name": "" }` $\rightarrow$ Kiểm tra `status().isBadRequest()` và `jsonPath("$.code").value(1002)` để chứng minh validation hoạt động hoàn hảo.
  4. 🔬 *[Khái niệm Contract Testing]:* Khái niệm **Consumer-Driven Contract Testing (Pact)** bảo vệ hợp đồng API giữa Frontend và Backend ra sao?
  5. 🏢 *[Thực tế]:* Viết Integration Test kiểm tra gọi `GET /api/v1/categories/1` $\rightarrow$ Trả về `200 OK` kèm đúng tên danh mục.
* **Từ khóa:** `MockMvc Framework`, `@WebMvcTest vs @SpringBootTest`, `JsonPath Assertions`, `Consumer-Driven Contract Testing (Pact Concept)`.

---

### 📌 Task 18: Bắt Lỗi Trùng Lặp Tầng Database (`DataIntegrityViolationException`)
* **Hành động code:** Bắt `DataIntegrityViolationException` trong `GlobalExceptionHandler` và format thông báo thân thiện.
* **Mục tiêu duy nhất:** Bắt lỗi vi phạm ràng buộc Unique Constraint từ PostgreSQL.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Dù Service đã có `if (repo.existsByEmail(email))`, tại sao 2 request gửi lên cùng mili-giây vẫn vượt qua `if` và gây lỗi ở DB (Race Condition ở tầng App)?
  2. ⚠️ *[Bẫy lỗi]:* Trả trực tiếp chuỗi lỗi kỹ thuật của PostgreSQL cho Client có nguy cơ lộ bảo mật gì?
  3. ⚖️ *[So sánh]:* Check bằng Code Java (`existsBy`) vs Unique Constraint ở Database (`@Column(unique = true)`).
  4. 🔄 *[Đánh đổi]:* Bắt lỗi tầng DB phụ thuộc vào mã lỗi SQL State (Postgres `23505`), làm sao để code không bị dính chặt vào 1 loại DB?
  5. 🏢 *[Thực tế]:* Chuyển đổi thành thông điệp thân thiện: "Dữ liệu đã tồn tại trong hệ thống".
* **Từ khóa:** `DataIntegrityViolationException`, `PostgreSQL Error 23505 Unique Violation`, `Database Unique Constraint as Single Source of Truth`.

---

### 📌 Task 19: Ghi Log Chuẩn (SLF4J) & Tự Động Gắn `Trace-Id` Với MDC Filter
* **Hành động code:** Tạo `CorrelationIdFilter` tự động sinh `traceId` và nạp vào `MDC.put("traceId", traceId)`.
* **Mục tiêu duy nhất:** Định danh và truy vết luồng log của từng request độc lập trong môi trường nhiều người dùng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất SLF4J]:* SLF4J là Logging Facade còn Logback là Implementation. Tại sao trong code ta chỉ gọi `log.info(...)` của SLF4J?
  2. ⚠️ *[Vấn đề Thực tế]:* Khi 5.000 người cùng đặt hàng trong 1 giây, nhờ có `traceId` ở đầu dòng log, làm sao ta lọc ra được toàn bộ hành trình của đúng 1 request bị lỗi?
  3. 🔬 *[Cơ chế MDC]:* MDC lưu trữ dữ liệu theo từng luồng (`ThreadLocal`) như thế nào để mọi lệnh `log.info()` đều tự in kèm mã `[TraceID: 3a7b-8c9d]`?
  4. ⚠️ *[Bẫy lỗi Memory Leak]:* Tại sao **BẮT BUỘC PHẢI GỌI `MDC.clear()`** trong khối `finally` của Filter (Tomcat tái sử dụng Thread từ Thread Pool)?
  5. 🏢 *[Thực tế]:* Trả kèm header `X-Correlation-ID: 3a7b-8c9d` trong Response để Frontend gửi mã này cho Backend tra cứu log.
* **Từ khóa:** `SLF4J Logger`, `MDC (Mapped Diagnostic Context)`, `Correlation ID Distributed Tracing Pattern`, `ThreadLocal Memory Leak Prevention with MDC.clear()`.

---

## 🔍 GIAI ĐOẠN 4: Truy Vấn Nâng Cao, Bắt N+1 & Tối Ưu Database (Tasks 20 - 27)

### 📌 Task 20: Viết Câu Truy Vấn JPQL Tùy Biến Đầu Tiên Với `@Query`
* **Hành động code:** Viết hàm tìm sản phẩm có giá trong khoảng `minPrice` đến `maxPrice` trong `ProductRepository`.
* **Mục tiêu duy nhất:** Nắm vững cú pháp JPQL (Java Persistence Query Language) và truyền tham số an toàn chống SQL Injection.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* JPQL thao tác trên **Entity/Java Class** hay trên **Bảng/Cột vật lý trong SQL**?
  2. ⚠️ *[Bẫy lỗi]:* Viết `WHERE price >= :minPrice` mà quên `@Param("minPrice")` ở tham số hàm Java, lỗi gì có thể xảy ra khi build không lưu tên tham số?
  3. ⚖️ *[So sánh]:* Dùng JPQL `@Query` vs Dùng tên hàm tự sinh `findByPriceBetween(...)`.
  4. 🔄 *[Đánh đổi]:* Tên hàm JPA tự sinh có 5 điều kiện sẽ dài 100 ký tự và khó đọc, JPQL giúp câu lệnh rõ ràng hơn ra sao?
  5. 🏢 *[Thực tế]:* Tại sao tuyệt đối không dùng phép cộng chuỗi tạo query (`"WHERE name = '" + name + "'"` - SQL Injection)?
* **Từ khóa:** `Spring Data JPA @Query`, `JPQL Named Parameters`, `SQL Injection Prevention`.

---

### 📌 Task 21: Viết Câu JPQL Tìm Kiếm Sản Phẩm Theo Tên Danh Mục
* **Hành động code:** Lấy danh sách sản phẩm thuộc về một danh mục theo tên (`category.name`).
* **Mục tiêu duy nhất:** Hiểu cú pháp Implicit Join và `JOIN FETCH` trong JPQL.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Viết `SELECT p FROM Product p WHERE p.category.name = :name`, Hibernate tự động sinh câu lệnh SQL `INNER JOIN` hay `CROSS JOIN`?
  2. ⚠️ *[Bẫy lỗi]:* Sản phẩm chưa gán danh mục (`category_id` là NULL), câu `INNER JOIN` ngầm có lấy ra được không? Muốn lấy phải dùng `LEFT JOIN` ra sao?
  3. ⚖️ *[So sánh]:* Sự khác nhau giữa `JOIN` thông thường và `JOIN FETCH` trong JPQL (`JOIN FETCH` giải quyết dứt điểm N+1 Query).
  4. 🔄 *[Đánh đổi]:* Tại sao không nên `JOIN FETCH` cùng lúc nhiều danh sách `@OneToMany` (`MultipleBagFetchException`)?
  5. 🏢 *[Thực tế]:* Viết câu truy vấn tối ưu kết hợp `JOIN FETCH` để lấy Sản phẩm kèm Danh mục trong đúng 1 câu SQL duy nhất.
* **Từ khóa:** `JPQL Join vs Join Fetch`, `Implicit Joins in JPA`, `MultipleBagFetchException`.

---

### 📌 Task 22: Bắt & Đo Lường Lỗi N+1 Query Thực Tế Với Hibernate Statistics
* **Hành động code:** Bật `spring.jpa.properties.hibernate.generate_statistics=true` trong `application-dev.yml` và kiểm tra log số lượng query.
* **Mục tiêu duy nhất:** Đo lường chính xác số lượng câu query phát sinh trong từng request và triệt tiêu N+1 Query.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Cấu hình `generate_statistics=true` cung cấp những chỉ số thống kê nào (Số lượng query, Thời gian thực thi, Session open time, Flushes count)?
  2. ⚠️ *[Thực hành bắt N+1]:* Tạo 10 Order, mỗi Order có 1 Customer. Viết hàm load 10 Order và lặp qua gọi `order.getCustomer().getName()`. Quan sát log: Có đúng $1 + 10 = 11$ câu query bị bắn xuống DB không?
  3. 🔬 *[Công cụ Chuyên nghiệp]:* Giới thiệu về **Hypersistence Optimizer** và thư viện **QuickPerf** giúp tự động phát hiện N+1 và fail Unit Test nếu số lượng query vượt quá 1 câu ra sao?
  4. ⚖️ *[So sánh]:* Chi phí 1 query lấy 100 dòng vs 100 query lấy 1 dòng (Network Round-Trip Time - RTT và Database Connection Handshake).
  5. 🏢 *[Thực tế]:* Sửa lại hàm trên bằng `JOIN FETCH` và quan sát log thống kê: Số lượng query giảm từ 11 câu xuống đúng 1 câu duy nhất!
* **Từ khóa:** `hibernate.generate_statistics=true`, `N+1 Query Detection`, `QuickPerf Assertion`, `Network Round-Trip Time (RTT)`.

---

### 📌 Task 23: Tối Ưu Bộ Nhớ Với DTO Projection
* **Hành động code:** Tạo `ProductSummaryResponse` và viết câu truy vấn SELECT thẳng vào DTO này.
* **Mục tiêu duy nhất:** Giảm tải bộ nhớ RAM và tăng tốc độ đọc dữ liệu báo cáo bằng JPA Constructor Expression.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Dùng `SELECT new com.example...Dto(p.id, p.name, p.price)`, Hibernate có đưa Entity vào `Persistence Context` không (Giải phóng bộ nhớ RAM cực lớn)?
  2. ⚠️ *[Bẫy lỗi]:* Cú pháp Constructor Expression yêu cầu DTO phải có đúng Constructor khớp từng kiểu dữ liệu và thứ tự tham số.
  3. ⚖️ *[So sánh]:* **Constructor Expression (Class-based)** vs **Interface-based Projection** (Proxy Interface).
  4. 🔄 *[Đánh đổi]:* DTO Projection truy vấn siêu nhanh và tốn ít RAM nhưng dữ liệu trả về là Read-Only (không hỗ trợ Dirty Checking tự động cập nhật).
  5. 🏢 *[Thực tế]:* Trong báo cáo Dashboard thống kê hàng triệu dòng doanh thu, tại sao 100% đều dùng DTO Projection?
* **Từ khóa:** `JPA Constructor Expression`, `Interface-based vs Class-based Projection`, `Read-only Query Performance`.

---

### 📌 Task 24: Làm Quen Với JPA Specification (Criteria API)
* **Hành động code:** Tạo class `ProductSpecification` chứa điều kiện lọc `hasCategory(Long categoryId)`.
* **Mục tiêu duy nhất:** Nắm vững cấu trúc 3 thành phần của Criteria API: `Root`, `CriteriaQuery`, `CriteriaBuilder`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `Root<Product>`, `CriteriaQuery<?>`, `CriteriaBuilder` đóng vai trò gì tương ứng trong SQL (`FROM`, `SELECT`, `WHERE/AND/OR`)?
  2. ⚠️ *[Bẫy lỗi]:* `categoryId` truyền vào là `null`, Specification phải trả về gì (`criteriaBuilder.conjunction()` hoặc `null`) để không bị lỗi SQL?
  3. ⚖️ *[So sánh]:* **JPA Specification** (Có sẵn trong Spring) vs **QueryDSL** (Cần generate code Q-classes).
  4. 🔄 *[Đánh đổi]:* Cú pháp Criteria API rườm rà nhưng đổi lại là Type-safe và ghép nối điều kiện lọc động vô hạn lúc runtime.
  5. 🏢 *[Thực tế]:* `ProductRepository` bắt buộc kế thừa thêm `JpaSpecificationExecutor<Product>`.
* **Từ khóa:** `JpaSpecificationExecutor`, `Specification Functional Interface`, `CriteriaBuilder Conjunction`.

---

### 📌 Task 25: Thêm Điều Kiện Lọc Khoảng Giá Và Tên Vào `ProductSpecification`
* **Hành động code:** Viết các method tĩnh `priceGreaterThanOrEqualTo`, `priceLessThanOrEqualTo`, `nameLike`.
* **Mục tiêu duy nhất:** Cài đặt các toán tử so sánh số và tìm kiếm chuỗi không phân biệt hoa thường.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tìm kiếm không phân biệt chữ hoa chữ thường với `criteriaBuilder.lower()` trong CriteriaBuilder.
  2. ⚠️ *[Bẫy lỗi]:* Ký tự `%` hoặc `_` trong từ khóa tìm kiếm SQL Like cần escape ra sao?
  3. ⚖️ *[So sánh]:* `criteriaBuilder.between(...)` vs Tách thành 2 hàm `greaterThanOrEqualTo` và `lessThanOrEqualTo`.
  4. 🔄 *[Đánh đổi]:* Tìm kiếm `LIKE '%keyword%'` khiến DB không dùng được Index B-Tree thông thường (Full Table Scan). Giải pháp trong thực tế (Full-Text Search / Elasticsearch)?
  5. 🏢 *[Thực tế]:* Viết các hàm Specification trả về lambda expression ngắn gọn, chuẩn Clean Code.
* **Từ khóa:** `CriteriaBuilder.like with lower`, `CriteriaBuilder.greaterThanOrEqualTo`, `B-Tree Index limitation on leading wildcard`.

---

### 📌 Task 26: Ghép Nối Specification Thành API Tìm Kiếm Linh Hoạt
* **Hành động code:** Endpoint `GET /api/v1/products/search?categoryId=1&minPrice=100&maxPrice=500&name=phone`.
* **Mục tiêu duy nhất:** Kết hợp nhiều Specification động bằng `Specification.where().and()` kết hợp phân trang `Pageable`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `Specification.where(spec1).and(spec2).and(spec3)` tự động gộp điều kiện bằng toán tử `AND` và bỏ qua điều kiện `null` ra sao?
  2. ⚠️ *[Bẫy lỗi]:* Truyền cả Specification và `Pageable` vào `productRepository.findAll(spec, pageable)`, Spring Data JPA tính câu `COUNT(*)` đi kèm ra sao?
  3. ⚖️ *[So sánh]:* Gom tham số lọc vào 1 DTO `ProductFilterRequest` vs Truyền 10 `@RequestParam` rời rạc.
  4. 🔄 *[Đánh đổi]:* Lọc động bằng Specification tạo ra nhiều câu SQL với cấu trúc khác nhau, ảnh hưởng thế nào đến Query Plan Cache của DB?
  5. 🏢 *[Thực tế]:* Viết API Controller nhận `ProductFilterRequest`, kết hợp `Pageable` và trả về `ApiResponse<PageResponse<ProductResponse>>`.
* **Từ khóa:** `Specification Chaining with AND/OR`, `Dynamic Filter DTO`, `Query Plan Cache Impact`.

---

### 📌 Task 27: Đánh Index Database Và Phân Tích Bằng `EXPLAIN ANALYZE`
* **Hành động code:** Thêm `@Table(indexes = { @Index(name = "idx_product_category_price", columnList = "category_id, price") })` trong `Product`.
* **Mục tiêu duy nhất:** Hiểu cấu trúc B-Tree Index, quy tắc Leftmost Prefix và đo lường chi phí truy vấn bằng `EXPLAIN ANALYZE`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* B-Tree Index tổ chức dữ liệu thế nào để giảm độ phức tạp tìm kiếm từ $O(N)$ xuống $O(\log N)$?
  2. ⚠️ *[Bẫy lỗi]:* Bảng chỉ có 50 dòng dữ liệu, tại sao chạy `EXPLAIN ANALYZE`, PostgreSQL vẫn chọn `Seq Scan` mà bỏ qua Index (Cost-based Optimizer)?
  3. ⚖️ *[So sánh]:* **Single Column Index** vs **Composite Index** (`category_id, price`). Thứ tự các cột quan trọng ra sao (Leftmost Prefix Rule)?
  4. 🔄 *[Đánh đổi]:* Mỗi Index tạo ra làm tăng tốc `SELECT` nhưng làm chậm tốc độ `INSERT`, `UPDATE`, `DELETE` (Chi phí cân bằng lại cây B-Tree).
  5. 🏢 *[Thực tế]:* Chạy `EXPLAIN ANALYZE SELECT * FROM products WHERE category_id = 1 AND price > 100` kiểm chứng.
* **Từ khóa:** `B-Tree Index Mechanics`, `Composite Index Leftmost Prefix`, `EXPLAIN ANALYZE Cost Estimation`, `Write Amplification of Indexes`.

---

## 🛒 GIAI ĐOẠN 5: Quản Lý Giao Dịch & Đa Luồng (Concurrency & Locking) (Tasks 28 - 34)

### 📌 Task 28: Thiết Kế Luồng Vòng Đời Trạng Thái Đơn Hàng
* **Hành động code:** Cài đặt phương thức `boolean canTransitionTo(OrderStatus nextStatus)` trong Enum `OrderStatus`.
* **Mục tiêu duy nhất:** Áp dụng Finite State Machine (FSM) bảo vệ quy tắc chuyển đổi trạng thái đơn hàng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Mô hình **Finite State Machine (FSM)** giúp bảo vệ dữ liệu nghiệp vụ không bị nhảy cóc trạng thái ra sao?
  2. ⚠️ *[Rủi ro nghiệp vụ]:* Đơn hàng đã ở trạng thái `DELIVERED` (Đã giao và thu tiền) mà bị đổi ngược về `PENDING`, hậu quả kế toán/kho vận là gì?
  3. ⚖️ *[So sánh]:* `if...else` rải rác trong Service vs Định nghĩa chuyển trạng thái bên trong Enum `OrderStatus` (dùng `EnumSet`).
  4. 🔄 *[Đánh đổi]:* Trả về mã lỗi `409 Conflict` vs `400 Bad Request` khi chuyển trạng thái không hợp lệ.
  5. 🏢 *[Thực tế]:* Logic hoàn kho chỉ khi đơn hàng bị hủy từ trạng thái `PENDING` hoặc `CONFIRMED`.
* **Từ khóa:** `Finite State Machine (FSM)`, `Order Status Lifecycle`, `EnumSet Java`, `HTTP 409 Conflict`.

---

### 📌 Task 29: Cơ Chế Rollback Của `@Transactional` & Kịch Bản Lỗi Giả Lập
* **Hành động code:** Thêm `@Transactional(rollbackFor = Exception.class)` cho các hàm sửa đổi dữ liệu và giả lập lỗi ở bước cuối để kiểm tra DB.
* **Mục tiêu duy nhất:** Hiểu sâu về Spring AOP Proxy, quy tắc Rollback (Checked vs Unchecked Exception) và tính Nguyên tử (Atomicity).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất AOP Proxy]:* Khối code `try { target.method(); tx.commit(); } catch (Throwable t) { tx.rollback(); }` hoạt động bên dưới ra sao?
  2. ⚠️ *[Bẫy lỗi kinh điển]:* Mặc định Spring **CHỈ ROLLBACK** với `RuntimeException` và `Error`. Nếu ném ra Checked Exception (`IOException`, `SQLException`), Transaction sẽ COMMIT bình thường! Cách sửa bằng `rollbackFor = Exception.class`?
  3. ⚠️ *[Bẫy lỗi Self-Invocation]:* Method `A()` (không có `@Transactional`) gọi method `B()` (có `@Transactional`) trong cùng 1 class `this.B()`, transaction ở `B` có chạy không (Bypass Spring Proxy)?
  4. 🔬 *[Thực hành]:* Trong `createOrder()`, sau khi trừ kho, cố tình `throw new RuntimeException("Lỗi")`. Vào DB kiểm tra: Số lượng sản phẩm có bị trừ không (Atomicity)?
  5. 🏢 *[Thực tế]:* Tuyệt đối không gọi tác vụ I/O chậm (Gửi Mail, gọi API ngoài) bên trong khối `@Transactional` để tránh cạn kiệt Connection Pool (HikariCP).
* **Từ khóa:** `Spring AOP Transaction Proxy`, `rollbackFor = Exception.class`, `Self-Invocation Pitfall`, `ACID Atomicity`.

---

### 📌 Task 30: Viết Test 2 Luồng Đơn Giản Tái Hiện Bug Trừ Kho Âm (Lost Update) *(VỪA SỨC - KHÔNG DÙNG API PHỨC TẠP)*
* **Hành động code:** Viết bài test Java tạo 2 luồng `Thread t1` và `Thread t2` cùng gọi hàm mua 1 sản phẩm cuối cùng (`quantity = 1`).
* **Mục tiêu duy nhất:** Tận mắt nhìn thấy lỗi Race Condition / Lost Update bằng code Java đơn giản nhất (`t1.start(); t2.start(); t1.join(); t2.join();`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Lost Update]:* Luồng 1 đọc `quantity = 1`, Luồng 2 đọc `quantity = 1`. Cả 2 cùng trừ 1 thành 0 và ghi đè lên nhau. Số lượng bán ra là 2 nhưng tồn kho chỉ giảm 1.
  2. ⚠️ *[Hậu quả]:* Bán vượt số lượng tồn kho (Overselling) trong Flash Sale làm thiếu hàng giao, bị phạt tiền và khiếu nại.
  3. 🔬 *[Cơ chế Thread.join()]:* `t1.join()` và `t2.join()` giúp luồng chính chờ 2 luồng con chạy xong để kiểm tra kết quả cuối cùng ra sao?
  4. ⚖️ *[So sánh]:* Tại sao khi bạn test thủ công 1 mình bằng Swagger/Postman, bạn **KHÔNG BAO GIỜ** phát hiện được bug này?
  5. 🏢 *[Thực tế]:* Chạy bài test và quan sát: Tồn kho bị âm hoặc 2 đơn hàng cùng mua thành công 1 sản phẩm duy nhất!
* **Từ khóa:** `Race Condition Simulation`, `Lost Update Problem`, `Thread t1 t2 join()`, `Overselling Bug`.

---

### 📌 Task 31: Khắc Phục Bằng Khóa Lạc Quan (Optimistic Lock Với `@Version`)
* **Hành động code:** Thêm trường `@Version private Long version;` vào Entity `Product`.
* **Mục tiêu duy nhất:** Áp dụng Compare-And-Swap (CAS) bằng trường phiên bản `@Version` không cần khóa Database.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Hibernate tự động thêm `WHERE id = ? AND version = ?` vào câu `UPDATE` ra sao? Khi update thành công thì trường `version` tăng lên mấy đơn vị?
  2. ⚠️ *[Cơ chế báo lỗi]:* Khi Luồng 2 update với `version` cũ, Rows Affected = 0, Hibernate ném ra ngoại lệ gì (`OptimisticLockingFailureException`)?
  3. ⚖️ *[So sánh]:* Tại sao Khóa Lạc Quan không hề dùng Database-level Lock mà chạy hoàn toàn dựa trên so sánh số phiên bản?
  4. 🔄 *[Đánh đổi]:* Khóa lạc quan đọc cực nhanh nhưng khi xung đột quá cao (1.000 người tranh 1 món đồ), 999 người sẽ bị ném Exception và thất bại.
  5. 🏢 *[Thực tế]:* Chạy lại bài test đa luồng ở Task 30: Luồng 1 mua thành công, Luồng 2 bị chặn lại bởi `OptimisticLockingFailureException` $\rightarrow$ Tồn kho được bảo vệ an toàn tuyệt đối!
* **Từ khóa:** `@Version Annotation`, `OptimisticLockingFailureException`, `Compare-And-Swap (CAS) Concept`.

---

### 📌 Task 32: Xử Lý Lỗi Xung Đột Phiên Bản & Chiến Lược Thử Lại (Spring Retry)
* **Hành động code:** Bắt `OptimisticLockingFailureException` ở `GlobalExceptionHandler` (trả về 409 Conflict) và tìm hiểu `@Retryable`.
* **Mục tiêu duy nhất:** Xử lý lỗi xung đột dữ liệu thân thiện và áp dụng cơ chế tự động thử lại.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Ném lỗi ngay cho Client bắt họ bấm lại vs Server tự động thử lại 3 lần (Retry Pattern).
  2. ⚠️ *[Bẫy lỗi]:* Khi nào KHÔNG ĐƯỢC RETRY (Sản phẩm chỉ còn 1 cái, lần 1 đã bán hết thì lần 2 retry không có ý nghĩa)?
  3. ⚖️ *[So sánh]:* Trả về `409 Conflict` vs `400 Bad Request` khi xảy ra xung đột dữ liệu.
  4. 🔄 *[Đánh đổi]:* Giới hạn `maxAttempts = 3` và thời gian giãn cách `backoff` để không làm nghẽn luồng của Server.
  5. 🏢 *[Thực tế]:* Format message: "Dữ liệu vừa được cập nhật bởi một phiên làm việc khác, vui lòng tải lại trang".
* **Từ khóa:** `Spring Retry @Retryable`, `HTTP 409 Conflict Handling`, `Backoff Strategy`.

---

### 📌 Task 33: Khắc Phục Bằng Khóa Bi Quan (Pessimistic Lock Với `SELECT FOR UPDATE`)
* **Hành động code:** Viết hàm `findByIdWithPessimisticLock` trong `ProductRepository` với `@Lock(LockModeType.PESSIMISTIC_WRITE)`.
* **Mục tiêu duy nhất:** Sử dụng Database Row-Level Lock ép các luồng phải xếp hàng tuần tự.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Database Lock]:* Hibernate sinh câu lệnh `SELECT ... FOR UPDATE` xuống PostgreSQL như thế nào? Hàng dữ liệu đó bị khóa (Row-level lock) ra sao cho đến khi Transaction kết thúc?
  2. ⚠️ *[Cơ chế Xếp Hàng]:* Khi Luồng 1 đang giữ khóa, Luồng 2 gọi hàm này sẽ bị chặn lại (Block/Wait) chờ Luồng 1 commit xong mới được đọc tiếp ra sao?
  3. ⚖️ *[So sánh]:* **Optimistic Lock** (Phù hợp đọc nhiều, ít tranh chấp) vs **Pessimistic Lock** (Phù hợp hệ thống Flash Sale, ngân hàng, tranh chấp cực cao).
  4. 🔄 *[Đánh đổi]:* Khóa bi quan ép các luồng phải xếp hàng tuần tự, làm giảm thông lượng (Throughput) của hệ thống.
  5. 🏢 *[Thực tế]:* Chạy lại bài test đa luồng: Cả 2 luồng đều thực thi an toàn mà không hề có Exception nào bị văng ra!
* **Từ khóa:** `@Lock(LockModeType.PESSIMISTIC_WRITE)`, `SELECT FOR UPDATE`, `Row-Level Locking`, `Optimistic vs Pessimistic Trade-off`.

---

### 📌 Task 34: Nhận Diện Deadlock & Cấu Hình Khóa Timeout
* **Hành động code:** Cấu hình Query Hint `@QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") })`.
* **Mục tiêu duy nhất:** Hiểu nguyên nhân Deadlock (Khóa chết) và cấu hình Timeout để giải phóng kết nối bị treo.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Deadlock]:* Tình huống Deadlock (Khóa chết) kinh điển: Luồng 1 giữ khóa SP A và chờ SP B; Luồng 2 giữ khóa SP B và chờ SP A. Hai luồng nhìn nhau vĩnh viễn!
  2. ⚠️ *[Giải pháp Database]:* Database tự phát hiện Deadlock qua thuật toán Chu trình đồ thị (Wait-For Graph) và chủ động "giết" (Kill/Rollback) 1 trong 2 transaction ra sao?
  3. ⚖️ *[Ngăn ngừa Deadlock]:* Quy tắc vàng: Luôn khóa các tài nguyên theo một **thứ tự ID tăng dần cố định** (`sortById` trước khi lock).
  4. 🔄 *[Cấu hình Timeout]:* Nếu không lấy được khóa sau 3 giây (3000ms), hàm ném ra `PessimisticLockException` thay vì để request bị treo vô tận.
  5. 🏢 *[Thực tế]:* Bắt `PessimisticLockException` và trả về `503 Service Unavailable` hoặc `409 Conflict`.
* **Từ khóa:** `Deadlock Detection (Wait-For Graph)`, `jakarta.persistence.lock.timeout`, `Ordered Resource Locking Strategy`.

---

## 🔐 GIAI ĐOẠN 6: Bảo Mật, JWT & Rate Limiting Chống Tấn Công (Tasks 35 - 44)

### 📌 Task 35: Thiết Kế Entity `User` Và `Role`
* **Hành động code:** Tạo bảng `users` (`id`, `username`, `password`, `email`, `role`, `isActive`).
* **Mục tiêu duy nhất:** Cài đặt interface `UserDetails` và `GrantedAuthority` chuẩn Spring Security.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Interface `UserDetails` và `GrantedAuthority` trong Spring Security yêu cầu những phương thức bắt buộc nào?
  2. ⚠️ *[Bẫy lỗi]:* Quy ước tiền tố `ROLE_` trong Spring Security: `hasRole("ADMIN")` tự động kiểm tra `ROLE_ADMIN` ra sao?
  3. ⚖️ *[So sánh]:* Single Role per User (Enum) vs Multi-Roles (`@ManyToMany`).
  4. 🔄 *[Đánh đổi]:* Multi-Roles linh hoạt nhưng làm tăng độ phức tạp của câu query `JOIN` mỗi lần check quyền.
  5. 🏢 *[Thực tế]:* Phân biệt **Role** (`ADMIN`, `STAFF`) vs **Permission/Privilege** (`product:read`, `product:create`).
* **Từ khóa:** `UserDetails & GrantedAuthority`, `ROLE_ Prefix Convention`, `RBAC vs ABAC`.

---

### 📌 Task 36: Cấu Hình `PasswordEncoder` Với `BCryptPasswordEncoder` *(TÁCH BIỆT KHỎI API)*
* **Hành động code:** Tạo class `SecurityConfig` và khai báo Bean `@Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }`.
* **Mục tiêu duy nhất:** Hiểu sâu về thuật toán băm mật khẩu một chiều có Salt và Cost factor của BCrypt.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Hàm băm một chiều (One-way Hash) là gì? Tại sao không có hàm giải mã BCrypt về mật khẩu ban đầu?
  2. ⚠️ *[Bẫy lỗi]:* Lưu mật khẩu plain text hoặc mã hóa 2 chiều lưu key trong code là thảm họa bảo mật tồi tệ nhất.
  3. 🔬 *[Cơ chế Salt]:* Cấu trúc chuỗi băm BCrypt (`$2a$10$...`) chứa: Thuật toán, Cost factor, Salt 16 bytes, Hash value.
  4. ⚖️ *[So sánh]:* Tại sao BCrypt cố tình chạy chậm (50-100ms cho 1 lần băm) để chống dàn máy đào GPU tấn công Brute-force?
  5. 🏢 *[Thực tế]:* Viết Unit Test kiểm tra `passwordEncoder.matches("123456", encodedPassword)` trả về `true`.
* **Từ khóa:** `BCryptPasswordEncoder Salt Mechanism`, `One-way Hash vs Two-way Encryption`, `Cost Factor Brute-force Resistance`.

---

### 📌 Task 37: Xây Dựng API Đăng Ký Tài Khoản (`POST /api/v1/auth/register`)
* **Hành động code:** Viết DTO `RegisterRequest`, Service kiểm tra trùng `username`/`email`, mã hóa mật khẩu và lưu `User` vào Database.
* **Mục tiêu duy nhất:** Hoàn thiện luồng nghiệp vụ đăng ký người dùng mới an toàn.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Luồng xử lý từ DTO $\rightarrow$ Check trùng Database $\rightarrow$ Encode mật khẩu $\rightarrow$ Lưu Entity.
  2. ⚠️ *[Bẫy lỗi]:* Nếu 2 người cùng bấm đăng ký trùng email trong 1 mili-giây, `DataIntegrityViolationException` bảo vệ Database ra sao?
  3. ⚖️ *[So sánh]:* Mặc định tài khoản mới tạo nên có Role gì (`CUSTOMER` / `USER`)? Tại sao tuyệt đối không cho phép Client truyền `role` lên trong RegisterRequest?
  4. 🔄 *[Đánh đổi]:* Trả về thông tin User sau đăng ký có nên bao gồm mật khẩu băm không? (Quy tắc lọc dữ liệu nhạy cảm).
  5. 🏢 *[Thực tế]:* Gọi thử API đăng ký trên Swagger và kiểm tra dòng dữ liệu trong PostgreSQL: Mật khẩu đã được băm an toàn dạng `$2a$10$...`.
* **Từ khóa:** `User Registration Flow`, `Password Hashing in Service`, `Sensitive Data Exposure Prevention`.

---

### 📌 Task 38: Tích Hợp Thư Viện JWT & Viết `JwtTokenProvider`
* **Hành động code:** Viết class sinh và xác thực JSON Web Token (JWT).
* **Mục tiêu duy nhất:** Hiểu cấu trúc 3 phần của JWT và thuật toán ký số HMAC-SHA256.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* JWT gồm 3 phần: `Header.Payload.Signature`. Phần nào chỉ là Base64Url ai cũng đọc được trên jwt.io?
  2. ⚠️ *[Bẫy lỗi]:* Tuyệt đối không lưu mật khẩu, số thẻ tín dụng vào Payload JWT.
  3. 🔬 *[Chữ ký điện tử]:* Chữ ký `Signature = HMAC-SHA256(...)` bảo vệ tính toàn vẹn thế nào khi Hacker sửa `role: CUSTOMER` thành `ADMIN`?
  4. ⚖️ *[So sánh]:* **Stateless JWT** vs **Stateful Session (JSESSIONID)**. Tại sao Microservices ưu tiên JWT?
  5. 🏢 *[Thực tế]:* Access Token expiration (15-60 phút), độ dài `SECRET_KEY` tối thiểu 256 bits (32 ký tự).
* **Từ khóa:** `JWT Anatomy (Header, Payload, Signature)`, `HMAC-SHA256 Integrity Verification`, `Stateless Authentication`.

---

### 📌 Task 39: Viết API Đăng Nhập (`POST /api/v1/auth/login`)
* **Hành động code:** Nhận `username` + `password`, đối soát mật khẩu và trả về Token.
* **Mục tiêu duy nhất:** Xác thực danh tính và trả về Token an toàn.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `passwordEncoder.matches(raw, encodedFromDB)` kiểm tra tính đúng đắn thế nào mà không cần giải mã?
  2. ⚠️ *[Bảo mật]:* Đăng nhập sai luôn trả về: "Tên đăng nhập hoặc mật khẩu không chính xác" (Chống User Enumeration Attack).
  3. ⚖️ *[So sánh]:* Trả Token trong Body JSON Response vs Lưu Token trong `HttpOnly Secure Cookie`.
  4. 🔄 *[Đánh đổi]:* Lưu trong Cookie chống XSS nhưng mở ra CSRF; Lưu trong Header miễn nhiễm CSRF nhưng cần chống XSS.
  5. 🏢 *[Thực tế]:* Trả về DTO `LoginResponse` chứa `accessToken`, `tokenType = "Bearer"`, `expiresIn`, `username`, `role`.
* **Từ khóa:** `passwordEncoder.matches Mechanism`, `User Enumeration Attack Prevention`, `XSS vs CSRF Token Storage`.

---

### 📌 Task 40: Viết `JwtAuthenticationFilter` (`OncePerRequestFilter`)
* **Hành động code:** Đón bắt mọi request, trích xuất Header `Authorization`, xác thực Token và nạp thông tin vào Spring Context.
* **Mục tiêu duy nhất:** Xác thực Token ở tầng Filter và lưu vào `SecurityContextHolder`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao filter này phải kế thừa `OncePerRequestFilter` (Đảm bảo chỉ thực thi đúng 1 lần cho mỗi HTTP request)?
  2. 🔬 *[Cơ chế Context]:* `SecurityContextHolder.getContext().setAuthentication(auth)` lưu trữ đối tượng ở đâu trong JVM (`ThreadLocal`)?
  3. ⚠️ *[Bẫy lỗi]:* Request không có Header `Authorization` (hoặc token hết hạn), tại sao phải gọi `filterChain.doFilter(...)` cho request đi tiếp thay vì ném lỗi ngay?
  4. ⚖️ *[So sánh]:* Giải mã JWT lấy User trực tiếp từ Payload vs Gọi DB `findByUsername()` trong mỗi request.
  5. 🏢 *[Thực tế]:* Trích xuất token bằng cách cắt bỏ tiền tố `"Bearer "` (`header.substring(7)`).
* **Từ khóa:** `OncePerRequestFilter`, `ThreadLocal in SecurityContextHolder`, `UsernamePasswordAuthenticationToken`.

---

### 📌 Task 41: Cấu Hình `SecurityFilterChain` Trong Spring Security 6
* **Hành động code:** Cấu hình mở public các API cần thiết và kích hoạt Filter kiểm tra Token.
* **Mục tiêu duy nhất:** Phân luồng bảo mật tập trung bằng Lambda DSL trong Spring Boot 3.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Spring Security 6 dùng cú pháp Lambda DSL, không còn kế thừa `WebSecurityConfigurerAdapter`.
  2. ⚠️ *[Bản chất CSRF]:* Tại sao REST API stateless dùng JWT lại tắt CSRF (`csrf.disable()`)?
  3. 🔬 *[Quản lý Session]:* Cấu hình `SessionCreationPolicy.STATELESS` ngăn Spring Boot tạo `HttpSession` trong RAM.
  4. ⚖️ *[So sánh]:* Thứ tự Filter: Bắt buộc đặt `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`.
  5. 🏢 *[Thực tế]:* Mở `permitAll` cho Swagger UI, API Auth, và các API GET xem sản phẩm công khai.
* **Từ khóa:** `SecurityFilterChain Lambda DSL Spring Boot 3`, `CSRF Disable Justification for JWT`, `SessionCreationPolicy.STATELESS`.

---

### 📌 Task 42: Phân Quyền API Với `@PreAuthorize`
* **Hành động code:** Bật `@EnableMethodSecurity` và thêm `@PreAuthorize("hasRole('ADMIN')")` cho các hàm Thêm/Sửa/Xóa sản phẩm.
* **Mục tiêu duy nhất:** Phân quyền theo vai trò (Role-Based Access Control) trên từng method.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@EnableMethodSecurity` sử dụng Spring AOP chặn trước khi gọi method bằng biểu thức SpEL ra sao?
  2. ⚠️ *[Kiểm tra]:* Đăng nhập bằng `CUSTOMER`, gọi API `POST /api/v1/products` $\rightarrow$ Nhận mã `403 Forbidden`.
  3. ⚖️ *[So sánh]:* Phân quyền tập trung trong cấu hình vs Phân quyền phân tán ngay trên method (`@PreAuthorize`).
  4. 🏢 *[Kiểm tra quyền sở hữu]:* Biểu thức SpEL: `@PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")`.
  5. 🏢 *[Thực tế]:* Phân biệt rõ **401 Unauthorized** (Chưa đăng nhập) vs **403 Forbidden** (Không đủ quyền).
* **Từ khóa:** `@EnableMethodSecurity`, `@PreAuthorize SpEL Expression`, `401 vs 403 HTTP Semantics`.

---

### 📌 Task 43: Tùy Biến Lỗi 401 Và 403 Theo Chuẩn `ApiResponse`
* **Hành động code:** Viết `CustomAuthenticationEntryPoint` và `CustomAccessDeniedHandler`.
* **Mục tiêu duy nhất:** Xử lý lỗi bảo mật ở tầng Filter Chain trả về cấu trúc JSON thống nhất.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao `GlobalExceptionHandler` không bắt được lỗi 401/403 (Lỗi xảy ra ở Filter Chain TRƯỚC KHI vào `DispatcherServlet`)?
  2. 🔬 *[Cơ chế EntryPoint]:* Tự ghi mã JSON vào `HttpServletResponse` bằng `ObjectMapper`.
  3. ⚠️ *[Bẫy lỗi]:* Quên đặt `response.setContentType("application/json;charset=UTF-8")` làm văng lỗi font hoặc text/plain.
  4. 🏢 *[Thực tế]:* Đăng ký 2 handler này vào `SecurityFilterChain` qua `exceptionHandling(...)` đảm bảo 100% phản hồi chuẩn JSON.
  5. 🏢 *[Kiểm tra]:* Gửi request không token $\rightarrow$ Nhận JSON 401; Gửi token user vào API admin $\rightarrow$ Nhận JSON 403.
* **Từ khóa:** `AuthenticationEntryPoint (401 Handler)`, `AccessDeniedHandler (403 Handler)`, `ObjectMapper Direct Writing`.

---

### 📌 Task 44: Chống Tấn Công DoS & Brute-Force Bằng Rate Limiting
* **Hành động code:** Tích hợp thư viện `Bucket4j` (hoặc tạo Filter Rate Limiter) giới hạn API Login tối đa 5 lần/phút/IP.
* **Mục tiêu duy nhất:** Áp dụng thuật toán Token Bucket chặn đứng tấn công Brute-Force và DoS.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Token Bucket]:* Thuật toán **Token Bucket (Thùng thẻ)** hoạt động ra sao: Thùng chứa tối đa N token, mỗi request tiêu thụ 1 token, token được hồi lại định kỳ theo giây?
  2. ⚠️ *[Rủi ro Brute-force]:* Nếu không có Rate Limiting, hacker có thể chạy tool thử 1.000 mật khẩu/giây vào endpoint `POST /api/v1/auth/login` làm sập server và dò ra mật khẩu người dùng.
  3. ⚖️ *[Mã lỗi HTTP Chuẩn]:* Khi bị vượt quá giới hạn request, server bắt buộc phải trả về mã HTTP nào (`429 Too Many Requests`) kèm header `Retry-After: 60`?
  4. 🔄 *[Đánh đổi]:* Lưu trữ số lượt request theo IP trên RAM của App (Local In-Memory) vs Lưu trên Redis (Distributed Rate Limiting). Khi chạy 3 server thì Local Rate Limiting bị lọt request thế nào?
  5. 🏢 *[Thực tế]:* Thử dùng Postman gửi 6 request login liên tiếp trong 10 giây: Request thứ 6 nhận về ngay `429 Too Many Requests`!
* **Từ khóa:** `Rate Limiting Bucket4j`, `Token Bucket Algorithm`, `HTTP 429 Too Many Requests`, `Brute-Force & DoS Protection`.

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
