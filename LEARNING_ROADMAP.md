# 🧭 LỘ TRÌNH NÂNG CẤP SPRING BOOT THỰC CHIẾN (KHUNG TƯ DUY KỸ SƯ 5 CHIỀU)

> **🎯 Khung Tư Duy 5 Chiều (5D Engineering Framework) ở mỗi bài toán:**
> 1. 🔬 **[Bản chất & Cơ chế ngầm]**: Bên dưới framework / database thực sự đang làm cái gì?
> 2. ⚠️ **[Rủi ro & Bẫy lỗi (Edge Cases)]**: Nếu không làm hoặc làm ẩu thì hệ thống sẽ gãy/sập ở đâu khi tải cao?
> 3. ⚖️ **[So sánh & Giải pháp khác]**: Có cách tiếp cận nào khác không (DB vs App, thư viện A vs B)?
> 4. 🔄 **[Sự đánh đổi (Trade-offs)]**: Được cái gì và phải trả giá bằng cái gì (RAM, CPU, I/O, độ phức tạp bảo trì)?
> 5. 🏢 **[Thực tế Doanh nghiệp (Production)]**: Các công ty lớn và dự án triệu người dùng xử lý vấn đề này theo quy chuẩn nào?

---

## 🧱 GIAI ĐOẠN 1: Chuẩn Hóa Entity & Quản Lý Dữ Liệu Tự Động (Tasks 1 - 6)

### 📌 Task 1: Tạo class trừu tượng `BaseEntity`
* **Mục tiêu:** Tạo class cha chứa 2 trường `createdAt` và `updatedAt`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@MappedSuperclass` khác gì với `@Entity` thông thường trong JPA? Tại sao nó không tạo ra một bảng riêng tên là `base_entity` trong PostgreSQL?
  2. ⚠️ *[Rủi ro]:* Nếu không có `BaseEntity` mà để từng lập trình viên tự gõ trường ngày giờ ở 10 Entity khác nhau, điều gì xảy ra khi một người đặt tên là `created_at`, người khác lại đặt là `created_date` hay `creation_time`?
  3. ⚖️ *[So sánh]:* So sánh việc dùng `LocalDateTime` vs `Instant` vs `ZonedDateTime` trong `BaseEntity`. Tại sao các hệ thống quốc tế luôn ưu tiên `Instant` hoặc UTC timestamp?
  4. 🔄 *[Đánh đổi]:* Việc sử dụng kế thừa (Inheritance) trong Entity có vi phạm nguyên lý "Composition over Inheritance" không? Có cách nào khác (như JPA `@Embeddable`) không?
  5. 🏢 *[Thực tế]:* Trong các hệ thống lớn, ngoài thời gian (`createdAt`, `updatedAt`), `BaseEntity` thường chứa thêm những trường nào (ví dụ: `createdBy`, `updatedBy`, `version`, `isDeleted`)?
* **Từ khóa:** `@MappedSuperclass`, `BaseEntity JPA`, `Instant vs LocalDateTime UTC`.

---

### 📌 Task 2: Cấu hình JPA Auditing tự động điền thời gian
* **Mục tiêu:** Cấu hình `@EnableJpaAuditing` và `@EntityListeners(AuditingEntityListener.class)`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `AuditingEntityListener` can thiệp vào giai đoạn nào trong vòng đời của một Hibernate Entity (JPA Lifecycle callbacks: `@PrePersist`, `@PreUpdate`)?
  2. ⚠️ *[Bẫy lỗi]:* Nếu bạn dùng câu lệnh UPDATE trực tiếp bằng JPQL (`@Modifying @Query("UPDATE Product p SET p.price = :price WHERE p.id = :id")`), trường `updatedAt` có được tự động cập nhật không? Tại sao (Gợi ý: JPQL bypass JPA Lifecycle)?
  3. ⚖️ *[So sánh]:* So sánh việc để Spring Boot tự sinh ngày giờ (Application-level) với việc dùng Trigger/Default Value trong PostgreSQL (`DEFAULT CURRENT_TIMESTAMP`). Điểm mạnh/yếu của mỗi cách?
  4. 🔄 *[Đánh đổi]:* Giao việc sinh thời gian cho App Server (Java) có rủi ro gì nếu bạn chạy nhiều cụm Server (Multi-instances) mà đồng hồ phần cứng của các server bị lệch nhau vài giây (NTP clock drift)?
  5. 🏢 *[Thực tế]:* Làm sao để cấu hình `AuditorAware<String>` giúp Spring tự động lấy `username` của người đang đăng nhập hiện tại nạp vào trường `@CreatedBy`?
* **Từ khóa:** `@EnableJpaAuditing`, `@EntityListeners`, `AuditingEntityListener`, `AuditorAware`, `JPA Lifecycle Callbacks`.

---

### 📌 Task 3: Kế thừa `BaseEntity` cho các Entity nghiệp vụ
* **Mục tiêu:** Cho `Category`, `Product`, `Customer`, `Order` kế thừa `BaseEntity`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi một Entity kế thừa `@MappedSuperclass`, Hibernate ánh xạ các cột của class cha vào bảng con như thế nào khi sinh câu `CREATE TABLE`?
  2. ⚠️ *[Rủi ro]:* `OrderItem` có nên kế thừa `BaseEntity` không? Một dòng chi tiết đơn hàng (đã chốt khi mua) có bao giờ được "cập nhật" thời gian (`updatedAt`) không?
  3. ⚖️ *[So sánh]:* Nếu muốn đổi tên cột trong bảng con (ví dụ bảng `orders` muốn cột ngày tạo tên là `order_created_at` thay vì `created_at`), ta dùng annotation gì (`@AttributeOverride`)?
  4. 🔄 *[Đánh đổi]:* Việc tự động ghi nhận `updatedAt` mỗi lần gọi `save()` có làm tăng nhẹ chi phí I/O ghi đĩa của Database không?
  5. 🏢 *[Thực tế]:* Trong các hệ thống kế toán / tài chính, tại sao người ta tuyệt đối không cho phép có hàm `update` đối với các bảng mang tính lịch sử giao dịch (Append-Only Log / Event Sourcing)?
* **Từ khóa:** `@AttributeOverride`, `Entity Inheritance Design`, `Append-Only Data Architecture`.

---

### 📌 Task 4: Thêm cờ Xóa Mềm (`isDeleted`) cho Category & Product
* **Mục tiêu:** Thêm trường `private boolean isDeleted = false;` vào Entity.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Xóa vật lý (Hard Delete - `DELETE FROM`) tác động lên Disk/B-Tree Index như thế nào so với Xóa logic (Soft Delete - `UPDATE set is_deleted = true`)?
  2. ⚠️ *[Bẫy lỗi]:* Giả sử cột `category.name` có ràng buộc duy nhất (`UNIQUE`). Bạn có danh mục "Quần Áo" đã xóa mềm (`is_deleted=true`). Khi người dùng tạo một danh mục mới cũng tên "Quần Áo", Database có báo lỗi trùng Unique không? Bạn giải quyết thế nào?
  3. ⚖️ *[So sánh]:* So sánh dùng cờ Boolean (`is_deleted = true/false`) vs dùng Timestamp (`deleted_at = NULL / 2026-08-26 15:00:00`). Kiểu nào lưu trữ được lịch sử xóa tốt hơn?
  4. 🔄 *[Đánh đổi]:* Dữ liệu xóa mềm vẫn nằm trong DB, làm tăng dung lượng bảng và làm chậm tốc độ quét Index. Cần có chiến lược lưu trữ dữ liệu cũ (Archive) ra sao trong thực tế?
  5. 🏢 *[Thực tế]:* Luật bảo vệ dữ liệu người dùng (như GDPR tại Châu Âu) yêu cầu "Quyền được lãng quên" (Right to be forgotten - phải xóa sạch thông tin cá nhân). Khi đó Soft Delete có bị xem là vi phạm luật không nếu không ẩn danh hóa dữ liệu?
* **Từ khóa:** `Soft Delete vs Hard Delete`, `Unique Constraint with Soft Delete`, `Partial Index PostgreSQL`.

---

### 📌 Task 5: Tự động hóa Xóa Mềm với `@SQLDelete` và `@SQLRestriction`
* **Mục tiêu:** Cấu hình `@SQLDelete` và `@SQLRestriction("is_deleted = false")` cho `Product`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@SQLRestriction` (Hibernate 6) hoạt động như thế nào? Nó chèn điều kiện `AND is_deleted = false` vào câu lệnh SQL được sinh ra ở tầng nào?
  2. ⚠️ *[Bẫy lỗi]:* Nếu bạn viết Native SQL Query (`@Query(value = "SELECT * FROM products", nativeQuery = true)`), `@SQLRestriction` có tự động thêm điều kiện lọc không? Tại sao?
  3. ⚖️ *[So sánh]:* `@SQLRestriction` (ở mức Entity) khác gì với Hibernate Filter (`@FilterDef`, `@Filter`)? Khi nào nên dùng Hibernate Filter (ví dụ: Multi-tenancy phân tách dữ liệu công ty)?
  4. 🔄 *[Đánh đổi]:* Khi Admin muốn vào trang quản trị xem danh sách "Các sản phẩm đã bị xóa" để Khôi phục (Restore), `@SQLRestriction` sẽ chặn không cho đọc. Làm sao để giải quyết trường hợp này?
  5. 🏢 *[Thực tế]:* Trong dự án lớn, tính năng khôi phục dữ liệu (Undo/Restore) được triển khai như thế nào thông qua API `PATCH /api/products/{id}/restore`?
* **Từ khóa:** `@SQLDelete`, `@SQLRestriction`, `Hibernate Filters`, `Native Query bypass Soft Delete`.

---

### 📌 Task 6: Hoàn thiện Service Xóa Mềm & Kiểm tra phản hồi
* **Mục tiêu:** Triển khai `deleteProduct(Long id)` và kiểm tra phản hồi API.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi gọi `productRepository.delete(product)`, Hibernate thực thi câu SQL gì xuống DB (nhờ có `@SQLDelete`)?
  2. ⚠️ *[Bẫy lỗi]:* Nếu người dùng cố tình gọi xóa 2 lần liên tiếp với cùng 1 `id`, API của bạn sẽ phản hồi mã lỗi gì ở lần thứ 2 (`404 Not Found` hay `200 OK`)?
  3. ⚖️ *[So sánh]:* API Xóa thành công nên trả về `204 No Content` (không có body) hay `200 OK` kèm JSON ApiResponse? Ưu/nhược điểm theo chuẩn RESTful?
  4. 🔄 *[Đánh đổi]:* Khi một Category bị xóa mềm, các Product thuộc Category đó có nên tự động bị xóa mềm theo (Cascade Soft Delete) không? Hay chỉ ẩn Category?
  5. 🏢 *[Thực tế]:* Kiểm tra log console để xác nhận: Hibernate thực sự chạy lệnh `UPDATE` thay vì `DELETE` và câu `SELECT` có tự động gắn `is_deleted = false` không.
* **Từ khóa:** `Cascading Soft Delete`, `RESTful Delete Status Codes (204 vs 200)`.

---

## 📄 GIAI ĐOẠN 2: Phân Trang, API Versioning & Sắp Xếp Dữ Liệu (Tasks 7 - 12)

### 📌 Task 7: Thiết kế DTO chuẩn `PageResponse<T>`
* **Mục tiêu:** Tạo Generic Class `PageResponse<T>` chứa metadata phân trang chuẩn hóa.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao không nên trả trực tiếp `org.springframework.data.domain.Page<T>` của Spring ra ngoài Controller? (Vấn đề phụ thuộc chặt chẽ framework và rò rỉ cấu trúc nội bộ).
  2. ⚠️ *[Bẫy lỗi]:* Nếu Frontend dựa vào trường `totalPages` để render thanh phân trang, trường hợp danh sách rỗng (0 bản ghi) thì `totalPages` phải bằng 0 hay 1? `isFirst` và `isLast` bằng bao nhiêu?
  3. ⚖️ *[So sánh]:* So sánh cấu trúc phân trang theo **Offset Pagination** (`page`, `size`, `totalElements`) với **Cursor Pagination** (`limit`, `nextCursor`, `hasMore`). Khi nào dùng Cursor (ví dụ: Newsfeed Facebook, TikTok)?
  4. 🔄 *[Đánh đổi]:* Để tính được `totalElements` và `totalPages`, Spring Data JPA bắt buộc phải chạy thêm **1 câu query đếm (`SELECT COUNT(*)`)**. Câu query đếm này ảnh hưởng hiệu năng ra sao khi bảng có 10 triệu dòng?
  5. 🏢 *[Thực tế]:* Các hệ thống cực lớn (như Google Search kết quả hàng triệu trang) thường dùng kỹ thuật "Ước lượng tổng số" hoặc dùng `Slice<T>` thay vì `Page<T>` để tránh câu `SELECT COUNT(*)` như thế nào?
* **Từ khóa:** `PageResponse Generic DTO`, `Page vs Slice Spring Data JPA`, `Offset vs Cursor-based Pagination`, `Count Query Performance`.

---

### 📌 Task 8: Áp dụng `Pageable` vào Repository & Service
* **Mục tiêu:** Tích hợp `Pageable` vào `ProductService.getAllProducts(...)`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Spring Data JPA tự động dịch `PageRequest.of(page, size, sort)` thành mệnh đề `LIMIT ? OFFSET ? ORDER BY ?` trong PostgreSQL như thế nào?
  2. ⚠️ *[Bẫy lỗi]:* Tại sao trong lập trình (Java/Spring), chỉ số trang bắt đầu từ `0` (Zero-indexed), nhưng với người dùng và Frontend thì luôn là trang `1` (One-indexed)? Xử lý việc lệch 1 đơn vị này ở đâu là sạch nhất?
  3. ⚖️ *[So sánh]:* Sắp xếp theo nhiều cột cùng lúc (ví dụ: Ưu tiên `price DESC`, nếu bằng giá thì xếp `createdAt DESC`) được khai báo trong `Sort.by(...)` như thế nào?
  4. 🔄 *[Đánh đổi]:* Vấn đề "Offset Skew / Data Drift": Nếu người dùng đang xem trang 1, cùng lúc đó có 5 sản phẩm mới được thêm vào, khi người dùng bấm sang trang 2 họ sẽ bị nhìn trùng lại 5 sản phẩm cũ của trang 1. Cách khắc phục?
  5. 🏢 *[Thực tế]:* Làm sao viết 1 hàm Mapper tiện ích (Generic Utility Method) chuyển đổi bất kỳ đối tượng `Page<Entity>` nào thành `PageResponse<Dto>` chỉ với 1 dòng code bằng Java Stream/Lambda?
* **Từ khóa:** `PageRequest.of`, `Sort Multiple Columns`, `Offset Pagination Data Drift`, `Generic Page Mapper`.

---

### 📌 Task 9: Thêm tham số phân trang vào Controller
* **Mục tiêu:** Endpoint `GET /api/v1/products?page=1&size=10&sortBy=price&sortDir=desc`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Annotation `@RequestParam(defaultValue = "...")` hoạt động như thế nào khi Client không truyền tham số lên URL?
  2. ⚠️ *[Bẫy lỗi]:* Nếu một User cố tình truyền `size=1000000` (1 triệu), Server của bạn sẽ bị gì nếu không giới hạn `size` tối đa?
  3. ⚖️ *[So sánh]:* So sánh việc nhận từng tham số rời rạc (`@RequestParam int page, @RequestParam int size...`) với việc tạo 1 object `PageFilterRequest` gom chung tất cả tham số lại? Cách nào dễ mở rộng hơn khi có thêm bộ lọc?
  4. 🔄 *[Đánh đổi]:* Giá trị mặc định của `pageSize` nên là bao nhiêu (10, 20 hay 50)? Đánh đổi giữa số lần gọi request của Frontend và dung lượng payload mỗi lần trả về?
  5. 🏢 *[Thực tế]:* Cách Swagger UI / SpringDoc hiển thị tài liệu hóa các tham số phân trang một cách trực quan qua annotation `@ParameterObject`.
* **Từ khóa:** `@RequestParam defaultValue`, `Page Size DoS Attack Prevention`, `@ParameterObject Springdoc`.

---

### 📌 Task 10: Xử lý ngoại lệ tham số phân trang không hợp lệ
* **Mục tiêu:** Bắt các lỗi truyền tham số sai (page âm, sortBy không tồn tại).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi Client truyền `sortBy=unknown_column`, Hibernate ném ra ngoại lệ gì (`PropertyReferenceException`)?
  2. ⚠️ *[Bẫy lỗi]:* Nếu không bắt lỗi `PropertyReferenceException` ở `GlobalExceptionHandler`, Client sẽ nhận về mã lỗi 500 (Internal Server Error) kèm cả đoạn StackTrace. Điều đó gây nguy cơ lộ bảo mật cấu trúc DB thế nào?
  3. ⚖️ *[So sánh]:* Nên dùng Whitelist (Chỉ cho phép sort theo một danh sách các cột hợp lệ như `name`, `price`, `createdAt`) hay để tự do cho sort theo bất kỳ thuộc tính nào của Entity?
  4. 🔄 *[Đánh đổi]:* Việc kiểm tra Whitelist Sort Column chặt chẽ ở tầng Application có làm tăng thêm vài dòng code kiểm tra không? Đổi lại được lợi ích bảo mật gì?
  5. 🏢 *[Thực tế]:* Viết method trong `GlobalExceptionHandler` bắt `PropertyReferenceException` và trả về mã `400 Bad Request` với message: "Trường sắp xếp không hợp lệ".
* **Từ khóa:** `PropertyReferenceException`, `Sort Whitelisting Security`, `GlobalExceptionHandler Property Error`.

---

### 📌 Task 11: Mở rộng phân trang cho Category và Customer
* **Mục tiêu:** Áp dụng kiến thức phân trang cho các Entity còn lại.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi nào một bảng **KHÔNG NÊN** phân trang? (Ví dụ: Bảng danh sách Tỉnh/Thành phố chỉ có 63 bản ghi cố định, có cần phân trang không?).
  2. ⚠️ *[Rủi ro]:* Với bảng `Category` có cấu trúc cha - con (Cây danh mục nhiều cấp), phân trang phẳng theo kiểu `LIMIT 10` sẽ làm vỡ giao diện cây phân cấp như thế nào?
  3. ⚖️ *[So sánh]:* Khi hiển thị Dropdown chọn danh mục trên Web, ta nên gọi API lấy tất cả (`getAll`) hay API phân trang?
  4. 🔄 *[Đánh đổi]:* Việc cung cấp cả 2 API: một API lấy tất cả (cho Dropdown) và một API phân trang (cho Bảng quản trị) có làm tăng chi phí bảo trì Controller không?
  5. 🏢 *[Thực tế]:* Kiểm tra toàn bộ các API phân trang trên Swagger UI (`/swagger-ui.html`) để đảm bảo tính nhất quán về định dạng trả về.
* **Từ khóa:** `Pagination Decision Matrix`, `Hierarchical Data Pagination`, `API Consistency`.

---

### 📌 Task 12: Chiến Lược API Versioning (Quản Lý Phiên Bản API)
* **Mục tiêu:** Chuyển đổi toàn bộ endpoint sang tiền tố `/api/v1/...` và tìm hiểu các chiến lược Versioning.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi ứng dụng di động (Mobile App) của khách hàng chưa kịp cập nhật bản mới, nếu Backend thay đổi cấu trúc JSON Response mà không có Versioning, ứng dụng Mobile của hàng triệu người dùng sẽ bị sập (Crash) ra sao?
  2. ⚠️ *[Bẫy lỗi]:* Phân biệt giữa **Breaking Change** (Đổi tên trường, xóa trường, đổi kiểu dữ liệu) và **Non-breaking Change** (Thêm trường mới có giá trị mặc định).
  3. ⚖️ *[So sánh 3 Chiến lược]:*
     - **URI Path Versioning:** `/api/v1/products` vs `/api/v2/products` (Phổ biến, dễ debug nhất).
     - **Header Versioning:** `X-API-Version: 2` hoặc `Accept: application/vnd.company.v2+json`.
     - **Query Parameter:** `/api/products?version=2`.
  4. 🔄 *[Đánh đổi]:* URI Versioning rất trực quan nhưng làm tăng số lượng Controller khi có nhiều phiên bản. Header Versioning giữ URL sạch nhưng khó test trực tiếp trên thanh địa chỉ trình duyệt.
  5. 🏢 *[Thực tế]:* Cấu hình `@RequestMapping("/api/v1/products")` cho toàn bộ các Controller hiện tại.
* **Từ khóa:** `API Versioning Strategies (URI vs Header vs Query)`, `Breaking vs Non-Breaking API Changes`, `Backward Compatibility`.

---

## 🛡️ GIAI ĐOẠN 3: Validation, Exception Handling & Logging Truy Vết (Tasks 13 - 18)

### 📌 Task 13: Bổ sung Validation định dạng cho `CustomerRequest`
* **Mục tiêu:** Validate Email chuẩn và Số điện thoại Việt Nam bằng Regex.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Pattern(regexp = "...")` của Bean Validation sử dụng thư viện nào phía dưới để đối soát chuỗi (Java `java.util.regex.Pattern`)?
  2. ⚠️ *[Bẫy lỗi]:* Tại sao `@Email` mặc định của Hibernate Validator đôi khi vẫn chấp nhận các email dị dạng (như `user@localhost`)? Làm sao để viết Regex Email nghiêm ngặt chuẩn quốc tế?
  3. ⚖️ *[So sánh]:* Validate ở tầng Frontend (Javascript/React) vs Validate ở tầng Backend (Spring DTO) vs Ràng buộc ở Database (CHECK constraint). Tại sao bắt buộc phải luôn validate ở Backend dù Frontend đã validate rất kỹ?
  4. 🔄 *[Đánh đổi]:* Việc sử dụng Regex phức tạp để validate có thể bị tấn công làm treo CPU (ReDoS - Regular Expression Denial of Service) không? Cách phòng tránh?
  5. 🏢 *[Thực tế]:* Viết Regex số điện thoại hỗ trợ tất cả các đầu số hiện nay của Viettel, VinaPhone, MobiFone, Vietnamobile (`03`, `05`, `07`, `08`, `09` + 8 chữ số).
* **Từ khóa:** `Bean Validation @Pattern`, `ReDoS Vulnerability`, `Vietnam Phone Number Regex`, `Backend vs Frontend Validation`.

---

### 📌 Task 14: Tạo Custom Annotation `@PhoneNumber`
* **Mục tiêu:** Tự viết Custom Annotation `@PhoneNumber` và class `PhoneNumberValidator`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Constraint(validatedBy = PhoneNumberValidator.class)` liên kết Annotation và class xử lý logic như thế nào trong Bean Validation Engine?
  2. ⚠️ *[Bẫy lỗi]:* Trong hàm `isValid(String value, ConstraintValidatorContext context)`, nếu người dùng truyền giá trị `null`, validator nên trả về `true` hay `false`? (Gợi ý: Trách nhiệm kiểm tra `null` thuộc về `@NotNull` hay `@PhoneNumber`?).
  3. ⚖️ *[So sánh]:* Việc tạo Custom Annotation `@PhoneNumber` khác gì so với việc copy dòng `@Pattern(regexp = "...")` dán vào 10 file DTO khác nhau? (Nguyên lý Tái sử dụng & Dễ bảo trì khi nhà mạng đổi đầu số).
  4. 🔄 *[Đánh đổi]:* Viết Custom Validator đòi hỏi tạo thêm 2 file mới (Annotation interface + Validator class). Khi nào thì nên viết Custom Validator, khi nào chỉ cần dùng Annotation có sẵn?
  5. 🏢 *[Thực tế]:* Làm sao truyền tham số động vào Custom Annotation (ví dụ: `@PhoneNumber(allowLandline = true)` để cho phép cả số bàn)?
* **Từ khóa:** `Custom ConstraintValidator`, `@Constraint`, `Bean Validation Context Null Handling`.

---

### 📌 Task 15: Chuẩn hóa thông báo lỗi Validation trong `GlobalExceptionHandler`
* **Mục tiêu:** Trả về danh sách chi tiết lỗi cho từng trường dạng Map `{ "email": "...", "phoneNumber": "..." }`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi một request bị vi phạm validation `@Valid`, Spring ném ra `MethodArgumentNotValidException`. Ngoại lệ này chứa dữ liệu gì từ `BindingResult`?
  2. ⚠️ *[Bẫy lỗi]:* Nếu một trường bị vi phạm cùng lúc 2 lỗi (ví dụ vừa `@NotBlank` vừa `@Size(min=5)`), phương thức `getFieldErrors()` sẽ lấy thông báo lỗi nào? Làm sao để không bị ghi đè thông báo?
  3. ⚖️ *[So sánh]:* Trả về định dạng lỗi dạng Map `{ field: error }` vs dạng Mảng `[ { "field": "email", "message": "..." } ]`. Định dạng nào thân thiện hơn cho ứng dụng Frontend (React Hook Form / Formik)?
  4. 🔄 *[Đánh đổi]:* Việc gom tất cả lỗi trả về 1 lần (Collect All Errors) giúp người dùng sửa form nhanh hơn, nhưng Server phải duyệt qua toàn bộ validation. Có tốn thêm chi phí không?
  5. 🏢 *[Thực tế]:* Đọc thông điệp lỗi đa ngôn ngữ (i18n - Internationalization: Tiếng Việt, Tiếng Anh) từ file `messages.properties` thông qua `MessageSource`.
* **Từ khóa:** `MethodArgumentNotValidException`, `BindingResult`, `FieldErrors Formatting`, `Spring Boot i18n Validation`.

---

### 📌 Task 16: Bắt lỗi trùng lặp dữ liệu tầng Database (`DataIntegrityViolationException`)
* **Mục tiêu:** Bắt lỗi trùng lặp Unique Constraint ở tầng Database và format thông báo thân thiện.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Dù trong Service đã có dòng `if (customerRepository.existsByEmail(email))`, tại sao khi có 2 request gửi lên cùng một mili-giây, cả 2 đều vượt qua câu lệnh `if` và gây lỗi văng ra từ Database? (Hiện tượng Race Condition ở tầng Application).
  2. ⚠️ *[Bẫy lỗi]:* Lỗi `DataIntegrityViolationException` ném ra từ Spring chứa chuỗi thông báo lỗi kỹ thuật của PostgreSQL (ví dụ: `ERROR: duplicate key value violates unique constraint "customers_email_key"`). Nếu trả trực tiếp chuỗi này cho Client, nguy cơ bảo mật là gì?
  3. ⚖️ *[So sánh]:* Ràng buộc duy nhất bằng Code Java (`existsBy`) vs Ràng buộc bằng Database Unique Constraint (`@Column(unique = true)`). Tại sao bắt buộc phải có Database Constraint làm chốt chặn cuối cùng?
  4. 🔄 *[Đánh đổi]:* Việc bắt lỗi tầng DB phụ thuộc vào mã lỗi SQL State (ví dụ Postgres error code `23505` cho unique violation). Làm sao để code xử lý exception không bị dính chặt vào 1 loại database cụ thể?
  5. 🏢 *[Thực tế]:* Viết hàm trong `GlobalExceptionHandler` nhận diện `DataIntegrityViolationException` và chuyển đổi thành thông điệp thân thiện: "Dữ liệu đã tồn tại trong hệ thống".
* **Từ khóa:** `DataIntegrityViolationException`, `PostgreSQL Error 23505 Unique Violation`, `Database Unique Constraint as Single Source of Truth`.

---

### 📌 Task 17: Ghi Log Chuẩn (SLF4J) & Tự Động Gắn `Trace-Id` Với MDC Filter *(ĐƯỢC ĐẨY LÊN SỚM)*
* **Mục tiêu:** Tạo `CorrelationIdFilter` tự động gắn `traceId` vào mọi dòng log của cùng một request để phục vụ debug các giai đoạn sau.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất SLF4J]:* SLF4J là Logging Facade (Giao diện) còn Logback là Implementation. Tại sao trong code ta chỉ gọi `log.info(...)` của SLF4J mà không gọi trực tiếp Logback?
  2. ⚠️ *[Vấn đề Thực tế]:* Khi có 5.000 người dùng cùng bấm đặt hàng trong 1 giây, log in ra terminal xen lẫn nhau. Nhờ có `traceId` xuất hiện ở đầu mỗi dòng log, làm sao ta lọc ra được toàn bộ hành trình của đúng 1 request bị lỗi?
  3. 🔬 *[Cơ chế MDC]:* Cơ chế **MDC (Mapped Diagnostic Context)** của SLF4J lưu trữ dữ liệu theo từng luồng (`ThreadLocal`) như thế nào để mọi lệnh `log.info()` trong Controller/Service đều tự động in kèm mã `[TraceID: 3a7b-8c9d]` ở đầu dòng?
  4. ⚠️ *[Bẫy lỗi Memory Leak]:* Tại sao **BẮT BUỘC PHẢI GỌI `MDC.clear()`** trong khối `finally` của Filter? (Gợi ý: Tomcat tái sử dụng Thread từ Thread Pool, nếu không clear thì request sau sẽ bị dính `traceId` của request trước).
  5. 🏢 *[Thực tế]:* Trả kèm header `X-Correlation-ID: 3a7b-8c9d` trong Response để khi Frontend gặp lỗi có thể đưa mã này cho Backend tra cứu log ngay lập tức.
* **Từ khóa:** `SLF4J Logger`, `MDC (Mapped Diagnostic Context)`, `Correlation ID Distributed Tracing Pattern`, `ThreadLocal Memory Leak Prevention with MDC.clear()`.

---

### 📌 Task 18: Validate giá trị logic nghiệp vụ cho `ProductRequest`
* **Mục tiêu:** Đảm bảo `price > 0`, `quantity >= 0`, tên không được toàn dấu cách trắng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao đối với tiền tệ (`price`), ta bắt buộc phải dùng kiểu dữ liệu `BigDecimal` mà tuyệt đối không được dùng `double` hay `float` trong Java? (Lỗi sai số dấu phẩy động: `0.1 + 0.2 = 0.30000000000000004`).
  2. ⚠️ *[Bẫy lỗi]:* Phân biệt sự khác nhau giữa `@NotNull`, `@NotEmpty` và `@NotBlank`. Nếu dùng `@NotNull` cho `String name`, người dùng gửi lên `name = "   "` (3 dấu cách) thì có vượt qua được validation không?
  3. ⚖️ *[So sánh]:* `@DecimalMin(value = "0.0", inclusive = false)` khác gì với `@Min(1)` khi validate giá tiền có số lẻ thập phân?
  4. 🔄 *[Đánh đổi]:* `BigDecimal` tính toán chính xác tuyệt đối nhưng tốc độ xử lý chậm hơn kiểu nguyên thủy `double`. Tại sao trong hệ thống thương mại/ngân hàng, sự chính xác luôn được ưu tiên hơn micro-giây CPU?
  5. 🏢 *[Thực tế]:* Trong thực tế, các sàn TMĐT (Shopee, Tiki) giới hạn giá sản phẩm tối thiểu là 1.000 VNĐ và tối đa là 1 tỷ VNĐ để tránh lỗi nhập nhầm của người bán như thế nào?
* **Từ khóa:** `BigDecimal Precision vs Double`, `@NotBlank vs @NotEmpty vs @NotNull`, `@DecimalMin inclusive`.

---

## 🔍 GIAI ĐOẠN 4: Truy Vấn Nâng Cao, Phát Hiện N+1 & Tối Ưu Database (Tasks 19 - 26)

### 📌 Task 19: Viết câu truy vấn JPQL tùy biến đầu tiên với `@Query`
* **Mục tiêu:** Viết hàm tìm sản phẩm có giá trong khoảng `minPrice` đến `maxPrice`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* JPQL (`Java Persistence Query Language`) thao tác trên các **Entity và thuộc tính của Java Class** hay thao tác trên các **Bảng và Cột vật lý trong SQL**?
  2. ⚠️ *[Bẫy lỗi]:* Nếu bạn viết `WHERE price >= :minPrice` mà không truyền `@Param("minPrice")` ở tham số hàm Java, lỗi gì có thể xảy ra khi build dự án ở chế độ không lưu tên tham số (`-parameters` flag)?
  3. ⚖️ *[So sánh]:* So sánh việc dùng JPQL `@Query` với việc dùng tên hàm tự sinh của Spring Data JPA `findByPriceBetween(BigDecimal min, BigDecimal max)`. Khi nào nên dùng cách nào?
  4. 🔄 *[Đánh đổi]:* Tên hàm JPA tự sinh dễ viết nhưng nếu có 5 điều kiện kết hợp thì tên hàm sẽ dài 100 ký tự và cực kỳ khó đọc. JPQL giúp câu lệnh rõ ràng hơn ra sao?
  5. 🏢 *[Thực tế]:* Tại sao tuyệt đối không được dùng phép cộng chuỗi để tạo câu query (`"SELECT p FROM Product p WHERE p.name = '" + name + "'"` - Lỗ hổng kinh điển SQL Injection)?
* **Từ khóa:** `Spring Data JPA @Query`, `JPQL Named Parameters`, `SQL Injection Prevention`.

---

### 📌 Task 20: Viết câu JPQL tìm kiếm sản phẩm theo tên danh mục
* **Mục tiêu:** Lấy danh sách sản phẩm thuộc về một danh mục theo tên (`category.name`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi bạn viết `SELECT p FROM Product p WHERE p.category.name = :name`, Hibernate tự động sinh câu lệnh SQL `INNER JOIN` hay `CROSS JOIN` bên dưới?
  2. ⚠️ *[Bẫy lỗi]:* Nếu một sản phẩm chưa được gán danh mục (`category_id` là NULL), câu lệnh `INNER JOIN` ngầm trên có lấy ra được sản phẩm đó không? Nếu muốn lấy thì phải dùng câu lệnh gì (`LEFT JOIN`)?
  3. ⚖️ *[So sánh]:* Sự khác nhau giữa `JOIN` thông thường và `JOIN FETCH` trong JPQL là gì? (Gợi ý: `JOIN FETCH` giải quyết dứt điểm bài toán N+1 Query như thế nào?).
  4. 🔄 *[Đánh đổi]:* Dùng `JOIN FETCH` rất mạnh để load dữ liệu liên quan trong 1 query, nhưng tại sao không nên `JOIN FETCH` cùng lúc nhiều danh sách `@OneToMany` (Lỗi `MultipleBagFetchException`)?
  5. 🏢 *[Thực tế]:* Viết câu truy vấn tối ưu kết hợp `JOIN FETCH` để lấy Sản phẩm kèm Danh mục trong đúng 1 câu SQL duy nhất.
* **Từ khóa:** `JPQL Join vs Join Fetch`, `Implicit Joins in JPA`, `MultipleBagFetchException`.

---

### 📌 Task 21: Phát Hiện & Đo Lường Lỗi N+1 Query Thực Tế *(MỚI BỔ SUNG)*
* **Mục tiêu:** Kích hoạt Hibernate Statistics để đếm chính xác số lượng câu query phát sinh trong từng request.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Cấu hình `spring.jpa.properties.hibernate.generate_statistics=true` cung cấp những chỉ số thống kê nào (Số lượng query, Thời gian thực thi, Session open time, Flushes count)?
  2. ⚠️ *[Thực hành bắt N+1]:* Tạo 10 Order, mỗi Order có 1 Customer. Viết hàm load 10 Order và lặp qua gọi `order.getCustomer().getName()`. Quan sát log: Có đúng $1 + 10 = 11$ câu query bị bắn xuống DB không?
  3. 🔬 *[Công cụ Chuyên nghiệp]:* Giới thiệu về **Hypersistence Optimizer** (của Vlad Mihalcea) và thư viện **QuickPerf** giúp tự động phát hiện N+1 và fail Unit Test nếu số lượng query vượt quá 1 câu ra sao?
  4. ⚖️ *[So sánh]:* Chi phí của 1 câu query lấy 100 dòng vs 100 câu query lấy 1 dòng. Tại sao 100 câu query lại chậm hơn gấp 20 lần? (Network Round-Trip Time - RTT và Database Connection Handshake).
  5. 🏢 *[Thực tế]:* Sửa lại hàm trên bằng `JOIN FETCH` và quan sát log thống kê: Số lượng query giảm từ 11 câu xuống đúng 1 câu duy nhất!
* **Từ khóa:** `hibernate.generate_statistics=true`, `N+1 Query Detection`, `QuickPerf Assertion`, `Network Round-Trip Time (RTT)`.

---

### 📌 Task 22: Tối ưu bộ nhớ với DTO Projection
* **Mục tiêu:** Viết câu truy vấn SELECT thẳng vào DTO tóm tắt `ProductSummaryResponse`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi dùng `SELECT new com.example...Dto(p.id, p.name, p.price)`, Hibernate có đưa các Entity vào `Persistence Context` (First-Level Cache / Dirty Checking) không? (Tại sao việc này giúp giải phóng bộ nhớ RAM cực lớn?).
  2. ⚠️ *[Bẫy lỗi]:* Cú pháp Constructor Expression yêu cầu DTO phải có đúng Constructor khớp từng kiểu dữ liệu và thứ tự tham số. Nếu trong Entity `id` là `Long` mà trong DTO Constructor khai báo `Integer`, lỗi gì sẽ nảy sinh lúc runtime?
  3. ⚖️ *[So sánh]:* So sánh **Constructor Expression (Class-based Projection)** với **Interface-based Projection** (Spring tự tạo Proxy Interface). Cách nào có hiệu năng cao hơn và dễ debug hơn?
  4. 🔄 *[Đánh đổi]:* DTO Projection giúp truy vấn siêu nhanh và tốn cực ít RAM, nhưng nhược điểm là dữ liệu lấy lên là Read-Only (không thể gọi `dto.setName(); save()` để cập nhật tự động như Entity).
  5. 🏢 *[Thực tế]:* Trong các báo cáo Dashboard thống kê hàng triệu dòng doanh thu, tại sao 100% lập trình viên kinh nghiệm đều dùng DTO Projection thay vì load Entity?
* **Từ khóa:** `JPA Constructor Expression`, `Interface-based vs Class-based Projection`, `Read-only Query Performance`.

---

### 📌 Task 23: Làm quen với JPA Specification (Criteria API)
* **Mục tiêu:** Tạo class `ProductSpecification` chứa điều kiện lọc `hasCategory(Long categoryId)`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Interface `Specification<Product>` hoạt động dựa trên Java Criteria API. Ba đối tượng `Root<Product>`, `CriteriaQuery<?>`, `CriteriaBuilder` đóng vai trò gì tương ứng trong câu lệnh SQL (`FROM`, `SELECT`, `WHERE/AND/OR`)?
  2. ⚠️ *[Bẫy lỗi]:* Nếu `categoryId` truyền vào là `null` (người dùng không chọn danh mục), hàm Specification của bạn phải trả về cái gì (`criteriaBuilder.conjunction()` hoặc `null`) để không bị lỗi câu lệnh SQL?
  3. ⚖️ *[So sánh]:* So sánh **JPA Specification** (Criteria API có sẵn trong Spring) với **QueryDSL** (cần plugin generate code Q-classes). Ưu và nhược điểm của mỗi bên trong các dự án thực tế?
  4. 🔄 *[Đánh đổi]:* Cú pháp của Criteria API khá rườm rà và khó đọc đối với người mới bắt đầu. Lợi ích lớn nhất đánh đổi lại là gì? (Type-safe và khả năng ghép nối điều kiện lọc động vô hạn lúc runtime).
  5. 🏢 *[Thực tế]:* Để Repository dùng được Specification, interface `ProductRepository` bắt buộc phải kế thừa thêm interface nào (`JpaSpecificationExecutor<Product>`)?
* **Từ khóa:** `JpaSpecificationExecutor`, `Specification Functional Interface`, `CriteriaBuilder Conjunction`.

---

### 📌 Task 24: Thêm điều kiện lọc khoảng giá và tên vào `ProductSpecification`
* **Mục tiêu:** Viết các method tĩnh `priceGreaterThanOrEqualTo`, `priceLessThanOrEqualTo`, `nameLike`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Trong CriteriaBuilder, phương thức `like()` khác `equal()` ở điểm nào? Làm sao để tìm kiếm không phân biệt chữ hoa chữ thường (`criteriaBuilder.lower()`)?
  2. ⚠️ *[Bẫy lỗi]:* Ký tự đặc biệt trong SQL Like: Nếu người dùng nhập từ khóa tìm kiếm là `%` hoặc `_`, câu lệnh `like` sẽ hiểu nhầm là ký tự đại diện. Cần escape các ký tự này như thế nào?
  3. ⚖️ *[So sánh]:* Dùng `criteriaBuilder.between(root.get("price"), min, max)` vs tách thành 2 hàm riêng biệt `greaterThanOrEqualTo` và `lessThanOrEqualTo`. Cách nào linh hoạt hơn khi người dùng chỉ nhập `minPrice` mà không nhập `maxPrice`?
  4. 🔄 *[Đánh đổi]:* Việc tìm kiếm `LIKE '%keyword%'` (chứa ký tự `%` ở đầu) sẽ khiến Database không thể sử dụng Index B-Tree thông thường và buộc phải Full Table Scan. Giải pháp thay thế cho tìm kiếm văn bản chuyên sâu trong thực tế là gì (Full-Text Search / Elasticsearch)?
  5. 🏢 *[Thực tế]:* Viết các hàm Specification trả về lambda expression ngắn gọn, sạch sẽ chuẩn Clean Code.
* **Từ khóa:** `CriteriaBuilder.like with lower`, `CriteriaBuilder.greaterThanOrEqualTo`, `B-Tree Index limitation on leading wildcard`.

---

### 📌 Task 25: Ghép nối Specification thành API Tìm Kiếm Linh Hoạt
* **Mục tiêu:** Endpoint `GET /api/v1/products/search?categoryId=1&minPrice=100&maxPrice=500&name=phone`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Phương thức `Specification.where(spec1).and(spec2).and(spec3)` tự động gộp các điều kiện bằng toán tử `AND` và loại bỏ các điều kiện `null` như thế nào?
  2. ⚠️ *[Bẫy lỗi]:* Khi truyền cả Specification và `Pageable` vào `productRepository.findAll(spec, pageable)`, Spring Data JPA tự động tính toán câu `COUNT(*)` đi kèm với các điều kiện lọc tương ứng ra sao?
  3. ⚖️ *[So sánh]:* So sánh việc gom các tham số lọc vào 1 DTO `ProductFilterRequest` vs truyền 10 `@RequestParam` rời rạc ở Controller?
  4. 🔄 *[Đánh đổi]:* Việc lọc động bằng Specification có thể tạo ra hàng trăm câu lệnh SQL với cấu trúc khác nhau lúc runtime. Điều này ảnh hưởng thế nào đến bộ nhớ đệm câu lệnh (Query Plan Cache) của Database?
  5. 🏢 *[Thực tế]:* Viết API Controller nhận `ProductFilterRequest`, kết hợp `Pageable` và trả về `ApiResponse<PageResponse<ProductResponse>>` hoàn chỉnh.
* **Từ khóa:** `Specification Chaining with AND/OR`, `Dynamic Filter DTO`, `Query Plan Cache Impact`.

---

### 📌 Task 26: Đánh Index Database và phân tích bằng `EXPLAIN ANALYZE`
* **Mục tiêu:** Đánh Index trên cột `name`, `category_id`, `created_at` và đo hiệu năng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Cấu trúc dữ liệu **B-Tree Index** trong PostgreSQL tổ chức dữ liệu như thế nào để giảm độ phức tạp tìm kiếm từ $O(N)$ xuống $O(\log N)$?
  2. ⚠️ *[Bẫy lỗi]:* Nếu bảng chỉ có 50 dòng dữ liệu, tại sao khi chạy `EXPLAIN ANALYZE`, PostgreSQL vẫn chọn `Seq Scan` (quét toàn bộ) mà bỏ qua không dùng Index dù bạn đã đánh index? (Gợi ý: Trình tối ưu hóa chi phí Cost-based Optimizer).
  3. ⚖️ *[So sánh]:* **Single Column Index** (Index đơn cột) khác gì với **Composite Index** (Index đa cột: `category_id, price`)? Thứ tự các cột trong Composite Index quan trọng như thế nào (Leftmost Prefix Rule)?
  4. 🔄 *[Đánh đổi]:* Mỗi Index tạo ra sẽ làm tăng tốc độ `SELECT` nhưng làm chậm tốc độ `INSERT`, `UPDATE`, `DELETE` bao nhiêu phần trăm? Tại sao? (Chi phí cân bằng lại cây B-Tree).
  5. 🏢 *[Thực tế]:* Khai báo `@Table(indexes = { @Index(name = "idx_product_category_price", columnList = "category_id, price") })` trong Entity và chạy `EXPLAIN ANALYZE` kiểm chứng.
* **Từ khóa:** `B-Tree Index Mechanics`, `Composite Index Leftmost Prefix`, `EXPLAIN ANALYZE Cost Estimation`, `Write Amplification of Indexes`.

---

## 🛒 GIAI ĐOẠN 5: Nghiệp Vụ Chặt Chẽ & Xử Lý Giao Dịch Đa Luồng (Tasks 27 - 33)

### 📌 Task 27: Thiết kế Luồng Vòng Đời Trạng Thái Đơn Hàng
* **Mục tiêu:** Xây dựng State Diagram cho enum `OrderStatus`: `PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `SHIPPED` $\rightarrow$ `DELIVERED` / `CANCELLED`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khái niệm **State Machine (Máy trạng thái hữu hạn)** giúp bảo vệ dữ liệu nghiệp vụ không bị nhảy cóc trạng thái như thế nào?
  2. ⚠️ *[Rủi ro nghiệp vụ]:* Nếu một đơn hàng đã ở trạng thái `DELIVERED` (Đã giao hàng và thu tiền) mà ai đó gọi API đổi ngược lại thành `PENDING` (Chờ xử lý), hậu quả về mặt kế toán/kho vận là gì?
  3. ⚖️ *[So sánh]:* Viết logic kiểm tra trạng thái bằng chuỗi `if...else` rải rác trong Service vs Định nghĩa danh sách các trạng thái hợp lệ tiếp theo ngay bên trong Enum `OrderStatus`. Cách nào chuẩn Clean Code và dễ bảo trì hơn?
  4. 🔄 *[Đánh đổi]:* Việc siết chặt quy tắc chuyển đổi trạng thái có làm giảm tính linh hoạt khi Admin muốn can thiệp thủ công (sửa lỗi dữ liệu khẩn cấp) không? Trong thực tế họ xử lý quyền "Super Admin Override" ra sao?
  5. 🏢 *[Thực tế]:* Vẽ bảng ma trận chuyển đổi trạng thái (State Transition Matrix) xác định rõ: từ trạng thái A có thể đi sang những trạng thái nào.
* **Từ khóa:** `Finite State Machine (FSM)`, `Order Status Lifecycle`, `Enum State Transition Matrix`.

---

### 📌 Task 28: Viết hàm Validate Chuyển Trạng Thái Trong `OrderService`
* **Mục tiêu:** Cài đặt phương thức `boolean canTransitionTo(OrderStatus nextStatus)` trong Enum và kiểm tra ở Service.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Sử dụng `EnumSet<OrderStatus>` trong Java mang lại hiệu năng so sánh bitwise siêu nhanh như thế nào?
  2. ⚠️ *[Bẫy lỗi]:* Khi hủy đơn hàng (`CANCELLED`), nếu đơn hàng đó đang ở trạng thái `SHIPPED` (hàng đã giao cho shipper trên đường vận chuyển), có được phép tự động hoàn kho ngay lập tức không? Tại sao?
  3. ⚖️ *[So sánh]:* Trả về mã lỗi `400 Bad Request` vs `409 Conflict` khi người dùng cố tình chuyển trạng thái không hợp lệ. Mã HTTP nào mô tả chính xác hơn sự xung đột trạng thái tài nguyên?
  4. 🔄 *[Đánh đổi]:* Khi có nhiều quy tắc phức tạp kèm theo mỗi lần đổi trạng thái (ví dụ: sang `CONFIRMED` thì trừ kho, sang `SHIPPED` thì gọi đơn vị vận chuyển GHTK), việc nhồi hết code vào 1 hàm `updateStatus` có làm vi phạm Single Responsibility không? (Gợi ý: State Pattern / Strategy Pattern).
  5. 🏢 *[Thực tế]:* Viết logic hoàn kho chỉ khi đơn hàng bị hủy từ trạng thái `PENDING` hoặc `CONFIRMED`.
* **Từ khóa:** `EnumSet Java`, `HTTP 409 Conflict vs 400 Bad Request`, `State Pattern Refactoring`.

---

### 📌 Task 29: Tìm hiểu cơ chế Rollback của `@Transactional`
* **Mục tiêu:** Hiểu sâu về cách Spring quản lý Commit và Rollback qua Proxy AOP.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* `@Transactional` tạo ra một Dynamic Proxy bọc lấy hàm Service. Khối code `try { target.method(); tx.commit(); } catch (Throwable t) { tx.rollback(); }` hoạt động bên dưới ra sao?
  2. ⚠️ *[Bẫy lỗi kinh điển]:* Mặc định Spring **CHỈ ROLLBACK** với `RuntimeException` và `Error` (Unchecked). Nếu phương thức ném ra `IOException` hay `SQLException` (Checked Exception), Transaction sẽ **COMMIT** bình thường! Làm sao để sửa bẫy lỗi này (`rollbackFor = Exception.class`)?
  3. ⚠️ *[Bẫy lỗi 2]:* Nếu trong Service bạn viết khối `try...catch` nuốt chửng lỗi (không `throw` lại ra ngoài), Transaction có rollback không? Tại sao?
  4. ⚖️ *[So sánh]:* Self-Invocation Issue: Nếu method `A()` (không có `@Transactional`) gọi method `B()` (có `@Transactional`) trong cùng 1 class `this.B()`, transaction ở method `B` có hoạt động không? Tại sao (Gợi ý: Bypass Spring Proxy)?
  5. 🏢 *[Thực tế]:* Tại sao trong dự án doanh nghiệp, chuẩn quy ước luôn là `@Transactional(rollbackFor = Exception.class)` cho tất cả các Service sửa đổi dữ liệu?
* **Từ khóa:** `Spring AOP Transaction Proxy`, `rollbackFor = Exception.class`, `Self-Invocation Transaction Pitfall`, `Checked vs Unchecked Exception Rollback`.

---

### 📌 Task 30: Tạo kịch bản lỗi giả lập để kiểm chứng Rollback
* **Mục tiêu:** Viết kịch bản tạo đơn hàng bị lỗi ở bước cuối và kiểm tra tính toàn vẹn của DB.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tính **Atomicity (Nguyên tử)** trong chuẩn ACID bảo đảm điều gì khi 1 giao dịch gồm 10 câu lệnh SQL nhưng câu thứ 10 bị lỗi?
  2. ⚠️ *[Thực hành]:* Trong `createOrder()`, sau khi đã chạy lệnh trừ kho sản phẩm và lưu OrderItem, bạn thêm dòng `throw new RuntimeException("Lỗi mô phỏng")`. Vào DB kiểm tra: Số lượng sản phẩm có bị trừ không? Đơn hàng có bị lưu không?
  3. ⚖️ *[So sánh]:* Transaction ở tầng Database đơn lẻ (Single DB) vs Transaction phân tán (Distributed Transaction - khi bạn phải gọi API sang 1 Microservice khác). `@Transactional` có rollback được API của Microservice khác không?
  4. 🔄 *[Đánh đổi]:* Giữ một `@Transactional` quá lâu (ví dụ trong hàm transaction lại gọi API bên ngoài mất 5 giây) sẽ giữ khóa Database Connection lâu, dẫn đến cạn kiệt Connection Pool (HikariCP). Cách khắc phục?
  5. 🏢 *[Thực tế]:* Quy tắc vàng: Tuyệt đối không gọi các tác vụ I/O chậm (Gửi Email, gọi API bên thứ 3) bên trong một khối `@Transactional`.
* **Từ khóa:** `ACID Atomicity`, `HikariCP Connection Leak under Long Transaction`, `Distributed Transaction Limitation`.

---

### 📌 Task 31: Tìm hiểu lỗi Tranh Chấp Tồn Kho (Race Condition)
* **Mục tiêu:** Mô phỏng tình huống 2 khách hàng cùng mua 1 sản phẩm cuối cùng tại cùng 1 thời điểm.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Vấn đề **Lost Update (Mất dữ liệu cập nhật)**: Luồng 1 đọc `quantity = 1`, Luồng 2 đọc `quantity = 1`. Cả 2 đều trừ 1 thành 0 và ghi đè lên nhau. Tại sao ở môi trường đơn luồng (test 1 mình) bạn không bao giờ phát hiện được lỗi này?
  2. ⚠️ *[Hậu quả]:* Hậu quả khi bán vượt số lượng tồn kho (Overselling) trong ngày Sale lớn (Flash Sale 11/11): Thiếu hàng giao cho khách, bị sàn phạt tiền, khách khiếu nại làm mất uy tín thương hiệu.
  3. ⚖️ *[So sánh]:* Hai trường phái giải quyết tranh chấp: **Optimistic Locking (Khóa lạc quan - tin rằng ít khi trùng)** vs **Pessimistic Locking (Khóa bi quan - chặn cửa, bắt xếp hàng)**.
  4. 🔄 *[Đánh đổi]:* Đánh đổi giữa **Hiệu năng hệ thống (Throughput)** và **Tính nhất quán dữ liệu (Consistency)** khi áp dụng cơ chế khóa.
  5. 🏢 *[Thực tế]:* Các hệ thống Flash Sale chịu tải 100.000 request/giây xử lý trừ kho trên Redis (Atomic `DECR` command) trước khi đẩy vào Database như thế nào?
* **Từ khóa:** `Race Condition Overselling`, `Lost Update Problem`, `Optimistic vs Pessimistic Locking Strategy`, `Redis Atomic DECR`.

---

### 📌 Task 32: Áp dụng Khóa Lạc Quan (Optimistic Lock với `@Version`)
* **Mục tiêu:** Thêm trường `@Version private Long version;` vào Entity `Product`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Hibernate tự động thêm điều kiện `WHERE id = ? AND version = ?` vào câu `UPDATE` như thế nào? Khi update thành công thì trường `version` tự động tăng lên mấy đơn vị?
  2. ⚠️ *[Cơ chế báo lỗi]:* Khi Luồng 2 cố update với `version` cũ, số dòng bị ảnh hưởng (Rows Affected) trả về bằng 0. Hibernate phát hiện điều này và ném ra ngoại lệ gì (`OptimisticLockingFailureException` / `ObjectOptimisticLockingFailureException`)?
  3. ⚖️ *[So sánh]:* Tại sao Khóa Lạc Quan không hề dùng bất kỳ câu lệnh Lock nào của Database (Database-level lock) mà lại hoạt động hoàn toàn dựa trên logic so sánh số phiên bản?
  4. 🔄 *[Đánh đổi]:* Khóa lạc quan có chi phí cực thấp khi đọc, nhưng khi tỉ lệ xung đột quá cao (1000 người tranh 1 món đồ), 999 người sẽ bị ném Exception và thất bại. Trường hợp này dùng Khóa Bi Quan có tốt hơn không?
  5. 🏢 *[Thực tế]:* Kiểm tra câu lệnh SQL thực tế chạy trong console khi update Product để thấy trường `version` tự động tăng từ 0 lên 1.
* **Từ khóa:** `@Version Annotation`, `OptimisticLockingFailureException`, `Compare-And-Swap (CAS) Concept`.

---

### 📌 Task 33: Bắt lỗi `OptimisticLockingFailureException` & Chiến lược Thử Lại (Retry)
* **Mục tiêu:** Bắt ngoại lệ xung đột ở `GlobalExceptionHandler` và tìm hiểu Spring Retry.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi gặp `OptimisticLockingFailureException`, việc ném lỗi ngay cho Client bắt họ bấm lại khác gì với việc Server tự động nạp lại dữ liệu mới và thử trừ kho lại 3 lần (Retry Pattern)?
  2. ⚠️ *[Bẫy lỗi]:* Khi nào KHÔNG ĐƯỢC TỰ ĐỘNG RETRY? (Ví dụ: Sản phẩm chỉ còn 1 cái, lần thử 1 thất bại do người khác mua mất rồi, số lượng đã về 0 thì lần thử 2 có ý nghĩa gì không?).
  3. ⚖️ *[So sánh]:* Trả về HTTP Status `409 Conflict` vs `400 Bad Request` khi xảy ra xung đột phiên bản dữ liệu.
  4. 🔄 *[Đánh đổi]:* Thư viện `spring-retry` (`@Retryable`) giúp code tự động thử lại rất gọn, nhưng nếu không giới hạn số lần retry (`maxAttempts = 3`) và thời gian giãn cách (`backoff`), nó có thể làm nghẽn thread của Server không?
  5. 🏢 *[Thực tế]:* Bắt ngoại lệ ở `GlobalExceptionHandler` và trả về mã 409 kèm thông điệp: "Dữ liệu vừa được cập nhật bởi một phiên làm việc khác, vui lòng tải lại trang".
* **Từ khóa:** `Spring Retry @Retryable`, `HTTP 409 Conflict Handling`, `Backoff Strategy`.

---

## 🔐 GIAI ĐOẠN 6: Bảo Mật Với Spring Security 6 & JWT (Tasks 34 - 41)

### 📌 Task 34: Thiết kế Entity `User` và `Role`
* **Mục tiêu:** Tạo bảng `users` (`id`, `username`, `password`, `email`, `role`, `isActive`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Trong Spring Security, interface `UserDetails` và `GrantedAuthority` yêu cầu những phương thức bắt buộc nào (`getUsername`, `getPassword`, `getAuthorities`, `isAccountNonLocked`...)?
  2. ⚠️ *[Bẫy lỗi]:* Quy ước tiền tố Role trong Spring Security: Tại sao khi khai báo quyền trong code thường phải có tiền tố `ROLE_` (ví dụ `ROLE_ADMIN`), nhưng khi dùng hàm `hasRole("ADMIN")` thì Spring lại tự động bỏ tiền tố `ROLE_`?
  3. ⚖️ *[So sánh]:* Thiết kế **Single Role per User** (1 người chỉ có 1 Role dạng Enum) vs **Multi-Roles per User** (Quan hệ `@ManyToMany` giữa `User` và `Role`). Khi nào nên dùng cách nào?
  4. 🔄 *[Đánh đổi]:* Thiết kế Multi-Roles linh hoạt hơn rất nhiều, nhưng làm tăng độ phức tạp của câu query `JOIN` mỗi lần kiểm tra quyền của người dùng.
  5. 🏢 *[Thực tế]:* Phân biệt giữa **Role** (Vai trò: `ADMIN`, `STAFF`, `CUSTOMER`) và **Permission/Privilege** (Hành vi cụ thể: `product:read`, `product:create`, `product:delete`).
* **Từ khóa:** `UserDetails & GrantedAuthority`, `ROLE_ Prefix Convention`, `RBAC (Role-Based Access Control) vs ABAC`.

---

### 📌 Task 35: Mã hóa mật khẩu với `BCryptPasswordEncoder` & Viết API Đăng Ký
* **Mục tiêu:** Khai báo Bean `PasswordEncoder` và mã hóa mật khẩu trước khi lưu vào DB.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Hàm băm một chiều (One-way Hash Function) là gì? Tại sao không có hàm "giải mã" (Decrypt) một chuỗi BCrypt về mật khẩu ban đầu?
  2. ⚠️ *[Bẫy lỗi]:* Tại sao lưu mật khẩu dạng Plain text ("123456") hay dùng mã hóa 2 chiều (AES/DES lưu key trong code) là thảm họa bảo mật tồi tệ nhất của một hệ thống?
  3. 🔬 *[Cơ chế Salt]:* Chuỗi băm BCrypt luôn có dạng `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`. Cấu trúc này chứa những phần nào (Thuật toán, Chi phí băm Cost/Rounds, Salt 16 bytes, Hash value)?
  4. ⚖️ *[So sánh]:* Tại sao BCrypt được thiết kế cố tình chạy **chậm** (khoảng 50-100 mili-giây cho 1 lần băm) thay vì siêu nhanh như MD5/SHA-256? (Chống lại các dàn máy đào GPU tấn công Brute-force hàng tỷ mật khẩu/giây).
  5. 🏢 *[Thực tế]:* Viết API `POST /api/v1/auth/register`, kiểm tra trùng `username`/`email`, mã hóa mật khẩu bằng `passwordEncoder.encode(rawPassword)` và lưu vào DB.
* **Từ khóa:** `BCryptPasswordEncoder Salt Mechanism`, `One-way Hash vs Two-way Encryption`, `Brute-force & Rainbow Table Resistance`.

---

### 📌 Task 36: Tích hợp thư viện JWT & Viết `JwtTokenProvider`
* **Mục tiêu:** Viết class sinh và xác thực JSON Web Token (JWT).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* JWT gồm 3 phần: `Header.Payload.Signature`. Phần nào được mã hóa? Phần nào chỉ là Base64Url-encoded (ai cũng có thể đọc được nội dung trên trang jwt.io)?
  2. ⚠️ *[Bẫy lỗi bảo mật]:* Tại sao **TUYỆT ĐỐI KHÔNG ĐƯỢC** lưu thông tin nhạy cảm (Mật khẩu, Số thẻ tín dụng, Số CCCD) vào Payload của JWT?
  3. 🔬 *[Chữ ký điện tử]:* Chữ ký `Signature = HMAC-SHA256(Base64(Header) + "." + Base64(Payload), SECRET_KEY)` bảo vệ tính toàn vẹn của Token như thế nào? Nếu Hacker sửa `role: "CUSTOMER"` thành `role: "ADMIN"` trong Payload thì chuyện gì xảy ra?
  4. ⚖️ *[So sánh]:* **Stateless Token (JWT)** vs **Stateful Session (JSESSIONID)**. Tại sao kiến trúc Microservices và REST API hiện đại luôn ưu tiên JWT? (Server không cần tốn RAM lưu Session state, dễ Scale ngang).
  5. 🏢 *[Thực tế]:* Thời gian sống (`expiration`) của Access Token nên là bao lâu (15-60 phút)? Độ dài tối thiểu an toàn của chuỗi `SECRET_KEY` (ít nhất 256 bits / 32 ký tự ngẫu nhiên).
* **Từ khóa:** `JWT Anatomy (Header, Payload, Signature)`, `HMAC-SHA256 Integrity Verification`, `Stateless vs Stateful Authentication`.

---

### 📌 Task 37: Viết API Đăng Nhập (`POST /api/v1/auth/login`)
* **Mục tiêu:** Nhận `username` + `password`, đối soát mật khẩu và trả về Token.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Phương thức `passwordEncoder.matches(rawPassword, encodedPasswordFromDB)` kiểm tra tính đúng đắn của mật khẩu như thế nào mà không cần giải mã chuỗi băm trong DB?
  2. ⚠️ *[Bảo mật]:* Khi đăng nhập sai, tại sao luôn phải trả về thông báo chung chung: "Tên đăng nhập hoặc mật khẩu không chính xác" thay vì nói rõ "Tên đăng nhập không tồn tại"? (Chống kỹ thuật tấn công dò quét tài khoản User Enumeration Attack).
  3. ⚖️ *[So sánh]:* Trả Token trong Body JSON Response vs Lưu Token trong `HttpOnly Secure Cookie`. Cách nào chống lại tấn công XSS (Cross-Site Scripting) tốt hơn?
  4. 🔄 *[Đánh đổi]:* Lưu trong `HttpOnly Cookie` bảo mật XSS rất tốt nhưng lại mở ra nguy cơ bị tấn công CSRF (Cross-Site Request Forgery). Lưu trong Header `Authorization: Bearer <token>` miễn nhiễm với CSRF nhưng cần Frontend bảo vệ chống XSS.
  5. 🏢 *[Thực tế]:* Viết API Login hoàn chỉnh, trả về DTO `LoginResponse` chứa `accessToken`, `tokenType = "Bearer"`, `expiresIn`, `username`, `role`.
* **Từ khóa:** `passwordEncoder.matches Mechanism`, `User Enumeration Attack Prevention`, `XSS vs CSRF Token Storage Trade-off`.

---

### 📌 Task 38: Viết `JwtAuthenticationFilter` (`OncePerRequestFilter`)
* **Mục tiêu:** Đón bắt mọi request, trích xuất Header `Authorization`, xác thực Token và nạp thông tin vào Spring Context.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao filter này phải kế thừa `OncePerRequestFilter` thay vì `GenericFilterBean`? (Đảm bảo filter chỉ thực thi đúng 1 lần duy nhất cho mỗi HTTP request, kể cả khi có forward nội bộ).
  2. 🔬 *[Cơ chế Context]:* `SecurityContextHolder.getContext().setAuthentication(authentication)` lưu trữ đối tượng xác thực ở đâu trong bộ nhớ của JVM (`ThreadLocal`)?
  3. ⚠️ *[Bẫy lỗi]:* Nếu một request gửi lên không có Header `Authorization` (hoặc Token hết hạn), Filter có nên ném Exception ngay lập tức không? Tại sao phải gọi `filterChain.doFilter(request, response)` cho request đi tiếp để các Filter sau của Spring Security xử lý?
  4. ⚖️ *[So sánh]:* Việc giải mã JWT lấy thông tin User trực tiếp từ Payload vs Gọi Database `userRepository.findByUsername()` trong mỗi request. Cách nào nhanh hơn và cách nào cập nhật trạng thái User (như bị khóa tài khoản) tức thì hơn?
  5. 🏢 *[Thực tế]:* Trích xuất chuỗi token bằng cách loại bỏ tiền tố `"Bearer "` (`header.substring(7)`).
* **Từ khóa:** `OncePerRequestFilter`, `ThreadLocal in SecurityContextHolder`, `UsernamePasswordAuthenticationToken`, `Filter Chain Propagation`.

---

### 📌 Task 39: Cấu hình `SecurityFilterChain` trong Spring Security 6
* **Mục tiêu:** Cấu hình mở public các API cần thiết và kích hoạt Filter kiểm tra Token.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Trong Spring Security 6 (dùng cú pháp Lambda DSL hiện đại), tại sao class cấu hình không còn kế thừa `WebSecurityConfigurerAdapter` (đã bị xóa bỏ)?
  2. ⚠️ *[Bản chất CSRF]:* Tại sao với REST API stateless dùng JWT, ta lại cấu hình `csrf(csrf -> csrf.disable())`? Cơ chế tấn công CSRF dựa vào Cookie trình duyệt tự gửi kèm, còn Header `Authorization` trình duyệt có tự gửi kèm không?
  3. 🔬 *[Quản lý Session]:* Cấu hình `sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))` ngăn Spring Boot tự động tạo và lưu trữ `HttpSession` trong RAM như thế nào?
  4. ⚖️ *[So sánh]:* Thứ tự của Filter: Tại sao bắt buộc phải đặt `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`?
  5. 🏢 *[Thực tế]:* Cấu hình phân luồng: Cho phép truy cập tự do (`permitAll`) vào Swagger UI (`/v3/api-docs/**`, `/swagger-ui/**`), API Auth (`/api/v1/auth/**`), và các API GET xem sản phẩm công khai.
* **Từ khóa:** `SecurityFilterChain Lambda DSL Spring Boot 3`, `CSRF Disable Justification for JWT`, `SessionCreationPolicy.STATELESS`, `addFilterBefore`.

---

### 📌 Task 40: Phân quyền API với `@PreAuthorize`
* **Mục tiêu:** Bật `@EnableMethodSecurity` và phân quyền chi tiết cho từng method Controller.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Annotation `@EnableMethodSecurity` sử dụng Spring AOP để chặn trước khi gọi method (Pre-invocation authorization check) bằng ngôn ngữ biểu thức SpEL (Spring Expression Language) ra sao?
  2. ⚠️ *[Kiểm tra]:* Gắn `@PreAuthorize("hasRole('ADMIN')")` cho API `POST /api/v1/products`. Thử đăng nhập bằng tài khoản `CUSTOMER`, lấy Token gọi API này: Spring Security trả về mã HTTP nào (`403 Forbidden` hay `401 Unauthorized`)?
  3. ⚖️ *[So sánh]:* Phân quyền tập trung trong file cấu hình (`authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/admin/**").hasRole("ADMIN"))`) vs Phân quyền phân tán ngay trên đầu method (`@PreAuthorize`). Ưu và nhược điểm của mỗi cách?
  4. 🏢 *[Kiểm tra quyền sở hữu]:* Viết biểu thức SpEL phức tạp kiểm tra người dùng chỉ được xem đơn hàng của chính mình: `@PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")`.
  5. 🏢 *[Thực tế]:* Phân biệt rõ: **401 Unauthorized** (Bạn chưa chứng minh được bạn là ai - Chưa đăng nhập) vs **403 Forbidden** (Tôi biết bạn là ai, nhưng bạn không đủ quyền hạn thực hiện hành động này).
* **Từ khóa:** `@EnableMethodSecurity`, `@PreAuthorize SpEL Expression`, `401 vs 403 HTTP Status Semantics`, `Resource Ownership Verification`.

---

### 📌 Task 41: Tùy biến lỗi 401 và 403 theo chuẩn `ApiResponse`
* **Mục tiêu:** Viết `CustomAuthenticationEntryPoint` và `CustomAccessDeniedHandler`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tại sao khi bị lỗi 401 hoặc 403, `GlobalExceptionHandler` (`@RestControllerAdvice`) lại **hoàn toàn bất lực không thể bắt được lỗi này**? (Gợi ý: Lỗi xảy ra ở tầng Security Filter Chain TRƯỚC KHI request chạm tới `DispatcherServlet` và Controller).
  2. 🔬 *[Cơ chế EntryPoint]:* `AuthenticationEntryPoint` được kích hoạt khi nào? Làm sao để tự tay ghi mã JSON vào `HttpServletResponse` bằng `ObjectMapper`?
  3. 🔬 *[Cơ chế AccessDenied]:* `AccessDeniedHandler` được kích hoạt khi nào?
  4. ⚠️ *[Bẫy lỗi]:* Nếu quên đặt `response.setContentType("application/json;charset=UTF-8")`, Client có thể nhận về dữ liệu bị lỗi font tiếng Việt hoặc hiểu nhầm là text/plain không?
  5. 🏢 *[Thực tế]:* Đăng ký 2 handler này vào `SecurityFilterChain` qua `exceptionHandling(...)` để đảm bảo 100% tất cả lỗi trong hệ thống đều trả về cấu trúc JSON thống nhất `{ code, message, result }`.
* **Từ khóa:** `AuthenticationEntryPoint (401 Handler)`, `AccessDeniedHandler (403 Handler)`, `Filter Layer vs Controller Layer Exception Handling`, `ObjectMapper Direct Response Writing`.

---

## ⚡ GIAI ĐOẠN 7: Bất Đồng Bộ, Upload File & Caching (Tasks 42 - 47)

### 📌 Task 42: Xây Dựng Service Upload File Ảnh Sản Phẩm (Local Storage)
* **Mục tiêu:** Viết API `POST /api/v1/products/{id}/image` nhận `MultipartFile` và lưu vào thư mục `uploads/`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi Client gửi request `multipart/form-data`, file được truyền tải dưới dạng các luồng byte (Byte Stream) như thế nào qua mạng?
  2. ⚠️ *[Bẫy lỗi trùng tên]:* Nếu 2 người cùng upload file tên `anh-san-pham.jpg`, làm sao để file sau không ghi đè và làm mất file trước? (Giải pháp: Tự sinh tên file duy nhất bằng `UUID.randomUUID().toString() + extension`).
  3. ⚖️ *[So sánh]:* Lưu file trên ổ cứng cục bộ của Server (Local Disk) vs Lưu trên Cloud Storage chuyên dụng (AWS S3, Cloudinary). Khi bạn mở rộng hệ thống chạy 3 Server (Cluster/Load Balancing), lưu local sẽ làm 2 server còn lại không tìm thấy ảnh như thế nào?
  4. 🔄 *[Đánh đổi]:* Lưu file vào Database dưới dạng BLOB (`byte[]`) có ưu điểm là backup DB là backup luôn cả ảnh, nhưng tại sao đây là tối kỵ trong thiết kế hệ thống? (Làm dung lượng DB phình to hàng chục GB, làm sập bộ nhớ cache RAM của DB).
  5. 🏢 *[Thực tế]:* Trong DB bảng `products`, ta chỉ lưu chuỗi đường dẫn URL tương đối (ví dụ: `/uploads/products/uuid-123.jpg`).
* **Từ khóa:** `MultipartFile Spring Boot`, `UUID Filename Sanitization`, `Local Storage vs Object Storage (AWS S3)`, `BLOB in Database Anti-Pattern`.

---

### 📌 Task 43: Kiểm Tra Bảo Mật File Upload (Security Validation)
* **Mục tiêu:** Chặn upload file độc hại (.exe, .php, .sh), giới hạn dung lượng 5MB.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Lỗ hổng thực tế]:* Nếu Hacker đổi tên file virus `shell.php` hoặc `trojan.exe` thành `avatar.jpg` rồi upload lên server, việc chỉ kiểm tra đuôi mở rộng file bằng `filename.endsWith(".jpg")` có bị qua mặt hoàn toàn không?
  2. 🔬 *[Giải pháp Magic Bytes]:* Khái niệm **Magic Bytes (File Signature)** là gì? Làm sao thư viện Apache Tika hoặc Java NIO có thể đọc các byte đầu tiên của file (`FF D8 FF` cho JPEG, `89 50 4E 47` cho PNG) để xác định định dạng file thực sự?
  3. ⚠️ *[Tấn công Path Traversal]:* Nếu hacker đặt tên file là `../../../../etc/passwd` hoặc `../../System32/file.dll`, làm sao hàm `Path.normalize()` ngăn chặn việc ghi đè vào thư mục nhạy cảm của hệ điều hành?
  4. ⚖️ *[Giới hạn dung lượng]:* Cấu hình `spring.servlet.multipart.max-file-size=5MB` và `max-request-size=10MB` trong `application.properties` bảo vệ server khỏi tấn công làm cạn kiệt ổ cứng (Disk Exhaustion Denial of Service) ra sao?
  5. 🏢 *[Thực tế]:* Viết class `FileValidator` kiểm tra: (1) File không rỗng $\rightarrow$ (2) Dung lượng $\le 5MB$ $\rightarrow$ (3) Magic Bytes đúng chuẩn định dạng ảnh hợp lệ.
* **Từ khóa:** `File Upload Security Magic Bytes`, `Path Traversal Prevention`, `Apache Tika MIME Detection`, `Multipart Max File Size Configuration`.

---

### 📌 Task 44: Bật Tính Năng Chạy Ngầm Bất Đồng Bộ (`@EnableAsync`)
* **Mục tiêu:** Bật `@EnableAsync` và viết service gửi email thông báo chạy ngầm.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Khi một method được đánh dấu `@Async`, Spring AOP tạo ra một Proxy và đẩy việc thực thi method đó sang một Thread Worker riêng biệt từ `TaskExecutor` như thế nào?
  2. ⚠️ *[Bẫy lỗi Thread Pool]:* Mặc định nếu không tự cấu hình Thread Pool, Spring sẽ dùng `SimpleAsyncTaskExecutor` (Tạo một Thread MỚI TINH cho mỗi request ngầm và không tái sử dụng). Điều gì sẽ xảy ra cho hệ thống nếu có 10.000 đơn hàng cùng lúc? (Sập server do cạn kiệt tài nguyên tạo Thread của Hệ điều hành).
  3. ⚖️ *[Cấu hình ThreadPoolTaskExecutor]:* Ý nghĩa của 3 tham số cốt lõi: `corePoolSize` (Số thread luôn giữ sẵn), `maxPoolSize` (Số thread tối đa khi quá tải), `queueCapacity` (Hàng đợi chứa các tác vụ chờ)?
  4. 🔄 *[Đánh đổi & Xử lý lỗi]:* Hàm `@Async` trả về `void` (Fire-and-Forget). Nếu quá trình gửi email ngầm bị lỗi (Mất mạng, sai mật khẩu SMTP), làm sao hệ thống biết để ghi log khi luồng chính của Controller đã trả về thành công cho khách từ lâu? (Gợi ý: `AsyncUncaughtExceptionHandler`).
  5. 🏢 *[Thực tế]:* Viết class `AsyncConfig` định cấu hình `ThreadPoolTaskExecutor` chuẩn cho môi trường Production.
* **Từ khóa:** `@EnableAsync & @Async`, `SimpleAsyncTaskExecutor vs ThreadPoolTaskExecutor`, `AsyncUncaughtExceptionHandler`, `CorePoolSize vs MaxPoolSize vs QueueCapacity`.

---

### 📌 Task 45: Tách Rời Logic Bằng Spring Event (`ApplicationEventPublisher`)
* **Mục tiêu:** Khi tạo đơn thành công, bắn `OrderCreatedEvent` để module thông báo tự động lắng nghe.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kiến trúc]:* Mô hình **Event-Driven Architecture (EDA) nội bộ** giúp giảm độ phụ thuộc (Decoupling) giữa `OrderService` và `EmailService` như thế nào? (OrderService không cần `@Autowired EmailService`).
  2. ⚠️ *[Bẫy lỗi Transaction]:* Nếu đơn hàng lưu vào DB bị lỗi và rollback, nhưng Event gửi email đã bị bắn đi trước đó, khách hàng sẽ nhận được email chúc mừng cho một đơn hàng không hề tồn tại! Làm sao giải quyết bằng `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`?
  3. ⚖️ *[So sánh]:* Đồng bộ Event (`@EventListener`) vs Bất đồng bộ Event (`@Async @EventListener`). Khi nào cần dùng kết hợp cả hai?
  4. 🔄 *[Đánh đổi]:* Việc sử dụng Event giúp code rất sạch và tuân thủ Open/Closed Principle (dễ dàng thêm listener tích hợp điểm thưởng, gửi tin Telegram mà không sửa 1 dòng code trong OrderService), nhưng đánh đổi lại việc lần theo vết luồng code (Code navigation/Debug) sẽ khó khăn hơn ra sao?
  5. 🏢 *[Thực tế]:* Tạo class `OrderCreatedEvent`, dùng `eventPublisher.publishEvent(...)` trong `OrderServiceImpl`, và tạo `OrderNotificationListener` lắng nghe sự kiện sau khi commit thành công.
* **Từ khóa:** `ApplicationEventPublisher`, `@TransactionalEventListener AFTER_COMMIT`, `Event-Driven Loose Coupling`, `Open/Closed Principle in Action`.

---

### 📌 Task 46: Tích Hợp Redis & Bật Caching Cho Ứng Dụng
* **Mục tiêu:** Cài đặt Redis (qua Docker), tích hợp `spring-boot-starter-data-redis` và bật `@EnableCaching`.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Tốc độ truy xuất dữ liệu trên bộ nhớ RAM (In-memory Data Store như Redis) nhanh hơn truy xuất đĩa cứng (PostgreSQL) từ 100 đến 1.000 lần như thế nào?
  2. 🔬 *[Cơ chế Serialization]:* Mặc định Spring Data Redis dùng `JdkSerializationRedisSerializer` (Lưu dữ liệu dạng nhị phân khó đọc). Tại sao nên cấu hình `GenericJackson2JsonRedisSerializer` để dữ liệu lưu vào Redis dưới dạng JSON chuẩn?
  3. ⚠️ *[Bẫy lỗi TTL]:* Tại sao **BẮT BUỘC PHẢI LUÔN CẤU HÌNH TTL (Time-To-Live)** cho mọi Key trong Redis (ví dụ: tự hết hạn sau 60 phút)? Nếu không đặt TTL, chuyện gì sẽ xảy ra với bộ nhớ RAM của Redis Server khi dữ liệu tích tụ qua nhiều năm?
  4. ⚖️ *[So sánh]:* **Local Cache** (Caffeine Cache lưu ngay trong RAM của Spring App) vs **Distributed Cache** (Redis Server độc lập). Khi chạy 5 cụm Server Spring Boot, Local Cache sẽ bị lỗi không đồng nhất dữ liệu (Cache Inconsistency) ra sao?
  5. 🏢 *[Thực tế]:* Viết class `RedisConfig` cấu hình `RedisCacheManager` với TTL mặc định là 30 phút và serialize định dạng JSON.
* **Từ khóa:** `Redis In-Memory Performance`, `GenericJackson2JsonRedisSerializer`, `Cache TTL (Time To Live)`, `Local Cache vs Distributed Cache Inconsistency`.

---

### 📌 Task 47: Áp Dụng `@Cacheable` và `@CacheEvict` Cho Danh Mục
* **Mục tiêu:** Cache danh sách danh mục và xóa cache tự động khi có thay đổi.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Mô hình **Cache-Aside Pattern (Lazy Loading Cache)** hoạt động theo 3 bước nào: (1) Đọc Cache $\rightarrow$ (2) Nếu Cache Miss thì đọc DB $\rightarrow$ (3) Ghi ngược lại Cache?
  2. 🔬 *[Cơ chế Xóa Cache]:* Annotation `@CacheEvict(value = "categories", allEntries = true)` hoạt động như thế nào khi Admin thêm/sửa/xóa một danh mục?
  3. ⚠️ *[Vấn đề Dữ liệu Cũ (Stale Data)]:* Nếu ai đó vào thẳng Database bằng DBeaver để sửa tên danh mục mà không qua API, dữ liệu trong Redis có biết để tự cập nhật không? Người dùng trên Web sẽ nhìn thấy dữ liệu gì cho đến khi Key hết hạn (TTL)?
  4. ⚠️ *[Rủi ro Hệ thống]:* Ba thảm họa kinh điển của hệ thống Caching: **Cache Penetration** (Query ID không tồn tại làm liên tục đánh sập DB), **Cache Breakdown** (Key hot vừa hết hạn thì 10.000 request cùng lao vào DB), **Cache Avalanche** (Hàng loạt Key cùng hết hạn vào 1 giây). Cách phòng tránh cơ bản?
  5. 🏢 *[Thực hành]:* Gắn `@Cacheable(value = "categories")` vào `getAllCategories()`. Chạy thử: Gọi lần 1 có log SQL trong terminal, gọi lần 2 không còn log SQL và tốc độ phản hồi giảm từ 50ms xuống 2ms!
* **Từ khóa:** `Cache-Aside Pattern`, `@Cacheable & @CacheEvict`, `Stale Data Problem`, `Cache Penetration vs Breakdown vs Avalanche`.

---

## 🧪 GIAI ĐOẠN 8: Kiểm Thử Toàn Diện, Giám Sát, Docker & CI/CD (Tasks 48 - 52)

### 📌 Task 48: Viết Unit Test Đầu Tiên Với JUnit 5 Cho `CategoryMapper`
* **Mục tiêu:** Viết `CategoryMapperTest` kiểm tra chuyển đổi Entity $\leftrightarrow$ DTO.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Kim tự tháp kiểm thử (Testing Pyramid) gồm 3 tầng: Unit Test (Đáy lớn nhất), Integration Test (Tầng giữa), E2E Test (Đỉnh). Tại sao Unit Test cần phải chiếm số lượng nhiều nhất và có tốc độ chạy nhanh nhất (vài mili-giây)?
  2. 🔬 *[Cấu trúc Test chuẩn]:* Cấu trúc **AAA (Arrange - Act - Assert)** hoặc **Given - When - Then** giúp một bài kiểm thử rõ ràng, dễ đọc như thế nào?
  3. ⚠️ *[Bẫy lỗi Null]:* Viết test case kiểm tra trường hợp đặc biệt (Edge case): Nếu truyền `null` vào hàm `toResponse(null)` hoặc `toEntity(null)`, mapper của bạn có ném lỗi `NullPointerException` làm sập app không hay trả về `null` an toàn?
  4. ⚖️ *[So sánh]:* Dùng `Assertions.assertEquals` của JUnit 5 vs Thư viện Fluent Assertions `AssertJ` (`assertThat(result.getName()).isEqualTo("Điện thoại")`). Cách nào đọc thuận theo ngôn ngữ tự nhiên hơn?
  5. 🏢 *[Thực tế]:* Viết test case hoàn chỉnh kiểm tra cả trường hợp đầy đủ dữ liệu và trường hợp dữ liệu rỗng.
* **Từ khóa:** `Testing Pyramid`, `AAA (Arrange-Act-Assert) Pattern`, `JUnit 5 vs AssertJ`, `Edge Case Null Safety Testing`.

---

### 📌 Task 49: Viết Unit Test Cho `CategoryServiceImpl` Với Mockito
* **Mục tiêu:** Dùng `@Mock` và `@InjectMocks` để test logic Service độc lập hoàn toàn với Database.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Mocking là gì? Tại sao khi viết Unit Test cho tầng Service, ta bắt buộc phải "giả lập" (Mock) tầng Repository mà không được kết nối đến Database thật? (Đảm bảo tính Độc lập - Isolation và Tốc độ thực thi).
  2. 🔬 *[Kỹ thuật Giả lập]:* Phương thức `when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory))` chỉ định cho Mockito làm gì khi Service gọi hàm tìm kiếm?
  3. ⚠️ *[Test Luồng Lỗi]:* Làm sao dùng `assertThrows(AppException.class, () -> categoryService.getCategoryById(99L))` để chứng minh rằng: Khi không tìm thấy danh mục trong DB, Service thực sự ném ra đúng ngoại lệ `CATEGORY_NOT_FOUND`?
  4. 🔬 *[Xác minh Hành vi]:* Phương thức `verify(categoryRepository, times(1)).save(any())` dùng để kiểm tra điều gì? (Chứng minh phương thức lưu thực sự đã được gọi đúng 1 lần).
  5. 🏢 *[Thực tế]:* Viết đầy đủ 2 bài test cho `createCategory`: (1) Test tạo thành công $\rightarrow$ (2) Test tạo thất bại do trùng tên danh mục (`CATEGORY_NAME_EXISTED`).
* **Từ khóa:** `Mockito @Mock & @InjectMocks`, `Stubbing with when().thenReturn()`, `assertThrows Exception Testing`, `Mockito verify() Behavior Verification`.

---

### 📌 Task 50: Viết Integration Test & Tìm Hiểu Contract Testing *(MỚI BỔ SUNG)*
* **Mục tiêu:** Viết `CategoryControllerTest` với `MockMvc` và tìm hiểu khái niệm Consumer-Driven Contract Testing.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất MockMvc]:* `MockMvc` giả lập môi trường Servlet Container (Tomcat) như thế nào mà không cần phải thực sự mở một cổng mạng (Port 8080) thật trên máy tính?
  2. 🔬 *[Phạm vi Test]:* So sánh `@WebMvcTest(CategoryController.class)` (Slice Test - Chỉ khởi động tầng Controller + Filter) vs `@SpringBootTest + @AutoConfigureMockMvc` (Full Integration Test).
  3. ⚠️ *[Kiểm tra JSON]:* Làm sao dùng cú pháp `jsonPath("$.code").value(1000)` và `jsonPath("$.result.name").value("Laptop")` để đối soát từng trường trong JSON trả về?
  4. 🔬 *[Khái niệm Contract Testing]:* Khi hệ thống phân rã thành nhiều Microservices hoặc Frontend/Backend độc lập, tại sao Unit Test & Integration Test vẫn không phát hiện được lỗi khi Backend đổi kiểu dữ liệu một trường? **Consumer-Driven Contract Testing (Pact / Spring Cloud Contract)** bảo vệ hợp đồng API giữa 2 bên ra sao?
  5. 🏢 *[Thực tế]:* Viết Integration Test kiểm tra: Gửi body rỗng `{ "name": "" }` $\rightarrow$ Nhận về `400 Bad Request` chuẩn JSON ApiResponse.
* **Từ khóa:** `MockMvc Framework`, `@WebMvcTest vs @SpringBootTest`, `JsonPath Assertions`, `Consumer-Driven Contract Testing (Pact Concept)`.

---

### 📌 Task 51: Tích Hợp Health Check & Metrics Với Spring Boot Actuator
* **Mục tiêu:** Thêm `spring-boot-starter-actuator` và cấu hình endpoint giám sát sức khỏe hệ thống.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất]:* Endpoint `/actuator/health` kiểm tra trạng thái của ứng dụng như thế nào? Nó kiểm tra những thành phần nào bên dưới (Database Connection Pool, Redis, Ổ đĩa Disk Space)?
  2. ⚠️ *[Bảo mật Actuator]:* Endpoint `/actuator/env` (Xem biến môi trường/mật khẩu DB) hoặc `/actuator/heapdump` (Tải toàn bộ bộ nhớ RAM) cực kỳ nguy hiểm. Tại sao trong môi trường Production tuyệt đối không được mở `management.endpoints.web.exposure.include=*` mà chỉ nên mở `health, info, metrics`?
  3. 🔬 *[Hệ sinh thái Giám sát]:* Ba thành phần trong mô hình giám sát kinh điển của doanh nghiệp: **Actuator/Micrometer** (Thu thập số liệu Metrics) $\rightarrow$ **Prometheus** (Lưu trữ dữ liệu Time-Series) $\rightarrow$ **Grafana** (Vẽ biểu đồ trực quan về CPU, RAM, Latency, TPS).
  4. ⚖️ *[Liveness vs Readiness Probes]:* Khác nhau giữa **Liveness** (Ứng dụng còn sống không, nếu chết thì Kubernetes khởi động lại) và **Readiness** (Ứng dụng đã sẵn sàng nhận Request chưa, nếu DB chưa kết nối xong thì chưa cho nhận traffic)?
  5. 🏢 *[Thực tế]:* Cấu hình trong `application.properties`: Mở endpoint `health` với chi tiết `show-details=always` ở môi trường Dev để xem chi tiết tình trạng kết nối PostgreSQL và Redis.
* **Từ khóa:** `Spring Boot Actuator`, `Actuator Security Best Practices`, `Prometheus & Grafana Ecosystem`, `Liveness and Readiness Probes for Kubernetes`.

---

### 📌 Task 52: Đóng Gói Docker & Tự Động Hóa CI/CD Với GitHub Actions *(MỚI BỔ SUNG)*
* **Mục tiêu:** Viết `Dockerfile` Multi-stage, `docker-compose.yml`, và file `.github/workflows/ci.yml` tự động build/test khi push code.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Multi-stage Build]:* Trong `Dockerfile`, tại sao nên chia làm 2 giai đoạn: Stage 1 (Dùng Image `maven:3.9-eclipse-temurin-21` để build file `.jar`) và Stage 2 (Dùng Image `eclipse-temurin:21-jre-alpine` siêu nhẹ chỉ để chạy file `.jar`)? Kỹ thuật này giúp giảm dung lượng Image từ 800MB xuống 150MB như thế nào?
  2. 🔬 *[Mạng trong Docker Compose]:* Trong file `docker-compose.yml`, khi Spring Boot kết nối với PostgreSQL, tại sao URL kết nối lại là `jdbc:postgresql://postgres-db:5432/postgres` (dùng tên Service của Docker) thay vì `localhost:5432`? (Cơ chế Docker DNS & Internal Bridge Network).
  3. 🔬 *[Cơ chế CI/CD Pipeline]:* Khái niệm **Continuous Integration (Tích hợp liên tục - CI)** là gì? Khi lập trình viên push code lên GitHub, GitHub Actions tự động khởi chạy Runner ảo để chạy lệnh `mvn clean test` như thế nào?
  4. ⚠️ *[Chặn Merge Code Lỗi]:* Nếu có 1 Unit Test bị fail, GitHub Actions sẽ đánh dấu đỏ (Build Failed) và chặn không cho Merge code vào nhánh `main`. Điều này bảo vệ sản phẩm như thế nào?
  5. 🏢 *[Thực hành Tối thượng]:* Tạo file `.github/workflows/ci.yml` đơn giản chạy `mvn clean verify` mỗi khi có Pull Request.
* **Từ khóa:** `Multi-stage Dockerfile Best Practices`, `docker-compose.yml Networking`, `GitHub Actions CI Pipeline (.github/workflows/ci.yml)`, `Automated Quality Gates`.

---

## 🏆 BẠN ĐÃ SỞ HỮU MỘT LỘ TRÌNH ĐẲNG CẤP SENIOR!

Lộ trình này không chỉ dạy bạn gõ code Spring Boot, mà rèn luyện cho bạn **tư duy toàn diện của một Kỹ sư Phần mềm thực thụ (Software Engineer)**:
- Nắm chắc kiến trúc dữ liệu và tối ưu hiệu năng DB.
- Hiểu sâu bảo mật, giao dịch đa luồng, và cache.
- Tự động hóa kiểm thử, giám sát và triển khai CI/CD.

👉 **Hãy bắt đầu ngay với [Task 1: Tạo class trừu tượng BaseEntity](file:///Users/kyanh/Documents/Learn/learn_spring/LEARNING_ROADMAP.md#task-1-t%E1%BA%A1o-class-tr%E1%BB%ABu-t%C6%B0%E1%BB%A3ng-baseentity)!**
