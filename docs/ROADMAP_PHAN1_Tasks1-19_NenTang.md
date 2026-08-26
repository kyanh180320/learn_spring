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

