# 🧭 LỘ TRÌNH 50 MICRO-TASKS NÂNG CẤP SPRING BOOT (HỌC SÂU - KHÔNG NẢN - TỰ CHỦ TƯ DUY)

> **Phương pháp học chủ động:**
> 1. Mỗi nhiệm vụ là một **bước siêu nhỏ (15 - 30 phút)**, không thể bị quá tải.
> 2. Trước khi viết code, bạn **tự trả lời 4 câu hỏi tư duy**.
> 3. Bạn tự suy nghĩ giải pháp $\rightarrow$ chia sẻ với AI $\rightarrow$ AI đóng vai trò là **Mentor/Phản biện** để hoàn thiện tư duy của bạn, không làm hộ từ đầu đến cuối.

---

## 🧱 GIAI ĐOẠN 1: Chuẩn Hóa Entity & Dữ Liệu Tự Động (Nhiệm vụ 1 - 6)

### 📌 Task 1: Tạo class trừu tượng `BaseEntity`
* **Mục tiêu:** Tạo class cha chứa 2 trường `createdAt` và `updatedAt`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* Nếu có 10 bảng trong DB, việc khai báo lặp đi lặp lại 2 trường này ở 10 Entity có vi phạm nguyên lý DRY (Don't Repeat Yourself) không?
  2. *Nếu không làm?* Khi cần đổi kiểu dữ liệu của ngày giờ (ví dụ từ `LocalDateTime` sang `Instant`), ta phải sửa bao nhiêu file?
  3. *Giải pháp khác?* Có cách nào tạo cột tự động ở tầng Database (SQL default `CURRENT_TIMESTAMP`) không? So sánh với việc quản lý ở tầng Java JPA?
  4. *Sự đánh đổi:* Việc dùng kế thừa Entity trong JPA có gây khó khăn gì khi đọc mã nguồn không?
* **Từ khóa:** `@MappedSuperclass`, `BaseEntity JPA`.

---

### 📌 Task 2: Cấu hình JPA Auditing tự động điền thời gian
* **Mục tiêu:** Gắn annotation để Spring tự điền ngày giờ khi Insert/Update mà không cần gõ code thủ công.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* Tại sao không nên để developer tự gọi `entity.setCreatedAt(LocalDateTime.now())` trong Service? Rủi ro quên gọi hoặc lấy lệch múi giờ là gì?
  2. *Bản chất:* Listener `@EntityListeners(AuditingEntityListener.class)` bắt sự kiện gì trong vòng đời của Entity (PrePersist, PreUpdate)?
  3. *Nếu quên:* Nếu quên thêm `@EnableJpaAuditing` ở file Main thì các trường ngày giờ sẽ có giá trị gì khi lưu vào DB?
* **Từ khóa:** `@EnableJpaAuditing`, `@CreatedDate`, `@LastModifiedDate`, `AuditingEntityListener`.

---

### 📌 Task 3: Cho các Entity kế thừa `BaseEntity`
* **Mục tiêu:** Áp dụng `BaseEntity` cho `Category`, `Product`, `Customer`, `Order`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* `OrderItem` có nhất thiết phải kế thừa `BaseEntity` không, hay chỉ cần lưu ngày tạo ở bảng `Order` cha là đủ?
  2. *Kiểm chứng:* Chạy ứng dụng và quan sát câu lệnh SQL Hibernate sinh ra trong console: Các cột `created_at` và `updated_at` đã xuất hiện trong bảng chưa?
* **Từ khóa:** `Entity Inheritance @MappedSuperclass`.

---

### 📌 Task 4: Thêm cờ Xóa Mềm (`isDeleted`) cho `Category` & `Product`
* **Mục tiêu:** Thêm trường `private boolean isDeleted = false;` vào Entity.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* Khác biệt giữa "Xóa vật lý" (Hard Delete - mất vĩnh viễn) và "Xóa logic" (Soft Delete - ẩn đi) là gì?
  2. *Tình huống:* Nếu một khách hàng đã mua sản phẩm A, sau đó chủ shop bấm xóa sản phẩm A. Nếu xóa cứng, đơn hàng cũ của khách hàng sẽ bị lỗi gì khi hiển thị lại?
  3. *Đánh đổi:* Bảng dữ liệu sẽ ngày càng lớn vì dữ liệu cũ không bao giờ bị xóa hẳn. Cần có chiến lược lưu trữ dữ liệu cũ (Archive) ra sao trong thực tế?
* **Từ khóa:** `Soft Delete Pattern`, `Active Flag`.

---

### 📌 Task 5: Tự động hóa Xóa Mềm với `@SQLDelete` và `@SQLRestriction`
* **Mục tiêu:** Cấu hình để khi gọi `repository.delete(product)` thì Hibernate tự chạy câu lệnh `UPDATE products SET is_deleted = true`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* Nếu không dùng `@SQLDelete`, ta phải tự viết hàm `product.setDeleted(true); repository.save(product);` ở khắp nơi. Điều đó có nguy cơ gì?
  2. *Tự động lọc:* Trong Hibernate 6, `@SQLRestriction("is_deleted = false")` tự động chèn thêm điều kiện `WHERE is_deleted = false` vào mọi câu `SELECT` như thế nào?
  3. *Ngoại lệ:* Khi Admin muốn xem lại danh sách "Thùng rác" (những sản phẩm đã bị xóa mềm) thì `@SQLRestriction` có gây cản trở không? Làm sao để bypass nó?
* **Từ khóa:** `@SQLDelete`, `@SQLRestriction` (Hibernate 6), `@Where` (cũ).

---

### 📌 Task 6: Tinh chỉnh Service và kiểm tra tính năng Xóa Mềm
* **Mục tiêu:** Gọi API `DELETE /api/products/1` và kiểm tra database: dòng dữ liệu vẫn còn nhưng `is_deleted = true`, gọi `GET /api/products` không còn thấy sản phẩm đó.
* **Bộ câu hỏi tư duy:**
  1. *Kiểm chứng:* Dùng công cụ quản lý DB (DBeaver/pgAdmin) xem giá trị cột `is_deleted` thay đổi ra sao.
  2. *Tư duy UX:* Khi người dùng cố gắng xem chi tiết sản phẩm đã bị xóa mềm (`GET /api/products/1`), server nên trả về `404 Not Found` hay thông báo "Sản phẩm này đã ngừng kinh doanh"?
* **Từ khóa:** `Soft Delete Verification`, `REST Status Codes`.

---

## 📄 GIAI ĐOẠN 2: Phân Trang & Sắp Xếp Dữ Liệu (Nhiệm vụ 7 - 11)

### 📌 Task 7: Thiết kế DTO chuẩn `PageResponse<T>`
* **Mục tiêu:** Tạo class Generic `PageResponse<T>` chứa: `List<T> content`, `pageNo`, `pageSize`, `totalElements`, `totalPages`, `isLast`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao?* Tại sao không nên trả trực tiếp đối tượng `org.springframework.data.domain.Page<T>` của Spring ra ngoài Controller?
  2. *Tính đóng gói:* Việc tự định nghĩa `PageResponse<T>` giúp API độc lập với framework Spring như thế nào nếu sau này đổi thư viện?
* **Từ khóa:** `Custom PageResponse DTO`, `Generic Response Pagination`.

---

### 📌 Task 8: Áp dụng `Pageable` vào `ProductRepository` & `ProductService`
* **Mục tiêu:** Đổi hàm `getAllProducts()` nhận tham số `int pageNo, int pageSize, String sortBy, String sortDir`.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* `PageRequest.of(pageNo, pageSize, Sort.by(...))` tạo ra câu lệnh SQL `LIMIT ... OFFSET ...` như thế nào?
  2. *Lưu ý chỉ số:* Trong Spring Data JPA, trang đầu tiên bắt đầu từ số 0 hay số 1? Tại sao ở tầng UI người dùng thường thấy trang 1? Ta cần chuyển đổi ở đâu?
* **Từ khóa:** `Pageable`, `PageRequest.of`, `Sort.Direction`.

---

### 📌 Task 9: Thêm tham số phân trang vào `ProductController`
* **Mục tiêu:** Endpoint `GET /api/products?page=0&size=10&sortBy=price&sortDir=asc`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Tại sao phải đặt `@RequestParam(defaultValue = "0")` cho các tham số phân trang?
  2. *Nếu không đặt defaultValue:* Điều gì xảy ra khi người dùng chỉ gõ `GET /api/products` mà không truyền tham số nào?
* **Từ khóa:** `@RequestParam defaultValue`, `Controller Pagination Endpoint`.

---

### 📌 Task 10: Xử lý ngoại lệ tham số phân trang không hợp lệ
* **Mục tiêu:** Bắt các trường hợp người dùng truyền `page < 0`, `size > 100`, hoặc `sortBy` là tên một cột không hề tồn tại trong DB.
* **Bộ câu hỏi tư duy:**
  1. *Bảo mật & Hiệu năng:* Tại sao phải giới hạn `maxSize = 100`? Nếu người dùng truyền `size=1000000` thì tính năng phân trang có còn tác dụng bảo vệ server không?
  2. *Bắt lỗi:* Khi người dùng truyền `sortBy=hack_column`, Hibernate sẽ ném lỗi `PropertyReferenceException`. Ta nên bắt lỗi này ở `GlobalExceptionHandler` như thế nào?
* **Từ khóa:** `Pagination Validation`, `PropertyReferenceException Handling`.

---

### 📌 Task 11: Mở rộng phân trang cho `Category` và `Customer`
* **Mục tiêu:** Áp dụng kiến thức vừa học để phân trang danh sách Khách hàng và Danh mục.
* **Bộ câu hỏi tư duy:**
  1. *Tái sử dụng:* Helper method nào có thể dùng chung để chuyển đổi từ `Page<T>` của Spring sang `PageResponse<R>` DTO mà không phải viết lặp lại code?
* **Từ khóa:** `Reusable Pagination Mapper`.

---

## 🛡️ GIAI ĐOẠN 3: Validation Chuyên Sâu & Xử Lý Ngoại Lệ (Nhiệm vụ 12 - 16)

### 📌 Task 12: Bổ sung Validation định dạng cho `CustomerRequest`
* **Mục tiêu:** Validate số điện thoại Việt Nam bằng `@Pattern` Regex (`^(0[3|5|7|8|9])+([0-9]{8})$`).
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Tại sao không nên chỉ kiểm tra `phoneNumber.length() == 10`?
  2. *Biểu thức chính quy (Regex):* Biểu thức trên kiểm tra những điều kiện gì của một đầu số di động hợp lệ tại Việt Nam?
* **Từ khóa:** `Jakarta Bean Validation @Pattern`, `Vietnam Phone Regex`.

---

### 📌 Task 13: Tạo Custom Annotation `@PhoneNumber`
* **Mục tiêu:** Tự tạo annotation `@PhoneNumber` và class `PhoneNumberValidator implements ConstraintValidator`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Việc tạo Custom Annotation `@PhoneNumber` giúp code sạch và tái sử dụng ở nhiều Request DTO khác nhau như thế nào so với việc copy-paste chuỗi Regex `@Pattern`?
  2. *Bản chất:* Hai phương thức `initialize` và `isValid` trong `ConstraintValidator` làm nhiệm vụ gì?
* **Từ khóa:** `Custom ConstraintValidator`, `Custom Annotation Java`.

---

### 📌 Task 14: Chuẩn hóa thông báo lỗi Validation trong `GlobalExceptionHandler`
* **Mục tiêu:** Trả về danh sách chi tiết lỗi cho từng trường: `{ "email": "Email không đúng định dạng", "phoneNumber": "Số điện thoại không hợp lệ" }`.
* **Bộ câu hỏi tư duy:**
  1. *Trải nghiệm người dùng:* Nếu người dùng điền sai 3 trường trên form, việc trả về thông báo của CẢ 3 TRƯỜNG cùng lúc khác gì so với việc chỉ báo lỗi của trường đầu tiên rồi bắt người dùng submit lại 3 lần?
  2. *Bản chất:* Làm sao lấy được danh sách `FieldError` từ `MethodArgumentNotValidException`?
* **Từ khóa:** `MethodArgumentNotValidException`, `BindingResult getFieldErrors`.

---

### 📌 Task 15: Validate giá trị logic nghiệp vụ cho `ProductRequest`
* **Mục tiêu:** Đảm bảo `price > 0` (`@DecimalMin`), `quantity >= 0` (`@Min(0)`), tên sản phẩm không được chỉ chứa toàn dấu cách trống.
* **Bộ câu hỏi tư duy:**
  1. *Phân biệt:* Sự khác biệt giữa `@NotNull`, `@NotEmpty` và `@NotBlank` là gì? Khi nào dùng annotation nào cho kiểu `String`, `Integer`, `BigDecimal`?
* **Từ khóa:** `@NotBlank vs @NotEmpty vs @NotNull`, `@DecimalMin`.

---

### 📌 Task 16: Bắt lỗi trùng lặp dữ liệu tầng Database (`DataIntegrityViolationException`)
* **Mục tiêu:** Bắt lỗi khi 2 request cùng cố tạo Customer với cùng 1 email vào đúng 1 thời điểm.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Dù trong Service đã có lệnh `if (customerRepository.existsByEmail(email))` nhưng tại sao Database vẫn có thể bị ném lỗi trùng Unique Constraint khi có 2 request chạy song song?
  2. *Xử lý ngoại lệ:* Bắt `DataIntegrityViolationException` trong `GlobalExceptionHandler` và chuyển thành `ApiResponse` thông báo "Dữ liệu đã tồn tại trong hệ thống".
* **Từ khóa:** `DataIntegrityViolationException`, `Race condition on Unique Constraint`.

---

## 🔍 GIAI ĐOẠN 4: Truy Vấn Nâng Cao & Tối Ưu Database (Nhiệm vụ 17 - 23)

### 📌 Task 17: Viết câu JPQL tùy biến đầu tiên với `@Query`
* **Mục tiêu:** Viết hàm tìm các sản phẩm có giá nằm trong khoảng `minPrice` đến `maxPrice`.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Trong JPQL `SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice`, `Product` ở đây là tên Class Entity trong Java hay tên bảng trong Database?
  2. *Tham số:* Tại sao nên luôn dùng `@Param("minPrice")` để gán tham số thay vì nối chuỗi SQL (Chống lỗi SQL Injection)?
* **Từ khóa:** `Spring Data JPA @Query`, `JPQL Named Parameters`.

---

### 📌 Task 18: Viết câu JPQL tìm kiếm sản phẩm theo tên danh mục
* **Mục tiêu:** Lấy danh sách sản phẩm thuộc về một danh mục cụ thể bằng cách `JOIN` trong JPQL.
* **Bộ câu hỏi tư duy:**
  1. *Truy vấn:* Viết câu JPQL: `SELECT p FROM Product p WHERE p.category.name = :categoryName`.
  2. *Hiệu năng:* JPA tự động sinh câu lệnh SQL `INNER JOIN` hay `LEFT JOIN` xuống Database?
* **Từ khóa:** `JPQL Join Query`.

---

### 📌 Task 19: Tối ưu bộ nhớ với DTO Projection
* **Mục tiêu:** Tạo `ProductSummaryResponse` (chỉ gồm `id`, `name`, `price`) và viết câu query chỉ SELECT 3 cột này.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Nếu bảng có 1 triệu dòng, việc chỉ SELECT 3 cột thay vì SELECT toàn bộ 15 cột giúp tiết kiệm bao nhiêu băng thông và bộ nhớ RAM?
  2. *Cú pháp:* `SELECT new com.example.learn_spring.dto.response.ProductSummaryResponse(p.id, p.name, p.price) FROM Product p`.
* **Từ khóa:** `Constructor Expression JPQL`, `DTO Projection`.

---

### 📌 Task 20: Làm quen với JPA Specification (Criteria API)
* **Mục tiêu:** Tạo class `ProductSpecification` chứa điều kiện lọc `hasCategory(Long categoryId)`.
* **Bộ câu hỏi tư duy:**
  1. *Vấn đề:* Nếu có 5 tiêu chí lọc (Tên, Danh mục, Giá từ, Giá đến, Còn hàng), nếu dùng method thường ta phải viết $2^5 = 32$ hàm khác nhau. `Specification` giải quyết bài toán này như thế nào?
  2. *Bản chất:* `Specification<Product>` là một Functional Interface nhận vào `(root, query, criteriaBuilder)`. Ba đối tượng này đại diện cho cái gì trong câu SQL?
* **Từ khóa:** `JpaSpecificationExecutor`, `Specification<T>`, `CriteriaBuilder.equal`.

---

### 📌 Task 21: Thêm điều kiện lọc khoảng giá và tên vào `ProductSpecification`
* **Mục tiêu:** Viết thêm các hàm `priceGreaterThanOrEqualTo`, `priceLessThanOrEqualTo`, `nameLike`.
* **Bộ câu hỏi tư duy:**
  1. *Xử lý null:* Nếu người dùng không truyền `minPrice` (giá trị là `null`), hàm Specification nên trả về cái gì (`criteriaBuilder.conjunction()` hoặc `null`) để không đưa điều kiện này vào câu SQL?
* **Từ khóa:** `CriteriaBuilder.greaterThanOrEqualTo`, `CriteriaBuilder.like`.

---

### 📌 Task 22: Ghép nối Specification thành API Tìm Kiếm Linh Hoạt
* **Mục tiêu:** API `GET /api/products/search?categoryId=1&minPrice=100&maxPrice=500&keyword=phone`.
* **Bộ câu hỏi tư duy:**
  1. *Ghép điều kiện:* Sử dụng `Specification.where(...).and(...)` trong Service để kết hợp các tiêu chí như thế nào?
  2. *Kết hợp phân trang:* `productRepository.findAll(spec, pageable)` vừa lọc vừa phân trang một cách tự động ra sao?
* **Từ khóa:** `Dynamic Specification Chaining`, `Specification with Pageable`.

---

### 📌 Task 23: Đánh Index Database và phân tích bằng `EXPLAIN ANALYZE`
* **Mục tiêu:** Thêm `@Table(indexes = { @Index(name = "idx_product_name", columnList = "name") })` cho `Product`.
* **Bộ câu hỏi tư duy:**
  1. *Thực hành:* Mở terminal chạy `EXPLAIN ANALYZE SELECT * FROM products WHERE name = 'iPhone 15';` trước và sau khi đánh index.
  2. *Đọc kết quả:* Sự khác biệt giữa `Seq Scan` (quét toàn bộ bảng) và `Bitmap Index Scan / Index Scan` (quét theo chỉ mục)? Thời gian thực thi (Execution Time) giảm bao nhiêu lần?
* **Từ khóa:** `PostgreSQL EXPLAIN ANALYZE`, `B-Tree Indexing`, `@Table @Index`.

---

## 🛒 GIAI ĐOẠN 5: Nghiệp Vụ Chặt Chẽ & Xử Lý Giao Dịch (Nhiệm vụ 24 - 30)

### 📌 Task 24: Thiết kế Luồng Vòng Đời Trạng Thái Đơn Hàng
* **Mục tiêu:** Xây dựng bảng quy tắc chuyển đổi trạng thái: `PENDING -> CONFIRMED -> SHIPPED -> DELIVERED`.
* **Bộ câu hỏi tư duy:**
  1. *Nghiệp vụ:* Đơn hàng ở trạng thái `DELIVERED` (Đã giao thành công) có được phép chuyển sang `CANCELLED` (Hủy) không?
  2. *Nghiệp vụ:* Khi đơn hàng ở trạng thái nào thì được phép hủy và hoàn lại số lượng tồn kho cho sản phẩm?
* **Từ khóa:** `Order State Machine`, `Business Rule Validation`.

---

### 📌 Task 25: Viết hàm Validate Chuyển Trạng Thái Trong `OrderService`
* **Mục tiêu:** Ném `AppException(ErrorCode.INVALID_ORDER_STATUS_CHANGE)` nếu cố tình chuyển đổi trạng thái trái quy tắc.
* **Bộ câu hỏi tư duy:**
  1. *Cấu trúc code:* Dùng cấu trúc `switch-case` hoặc phương thức `boolean canTransitionTo(OrderStatus nextStatus)` ngay trong enum `OrderStatus` thì cách nào hướng đối tượng (OOP) hơn?
* **Từ khóa:** `Enum methods Java`, `State Transition Logic`.

---

### 📌 Task 26: Tìm hiểu cơ chế Rollback của `@Transactional`
* **Mục tiêu:** Hiểu rõ khi nào transaction được commit và khi nào bị rollback.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Mặc định Spring chỉ rollback khi gặp `RuntimeException` (Unchecked). Nếu gặp `Exception` thông thường (Checked Exception), transaction có bị rollback không?
  2. *Khắc phục:* Tại sao thực hành chuẩn là luôn khai báo `@Transactional(rollbackFor = Exception.class)`?
* **Từ khóa:** `@Transactional(rollbackFor = Exception.class)`, `Checked vs Unchecked Exception Rollback`.

---

### 📌 Task 27: Tạo kịch bản lỗi giả lập để kiểm chứng Rollback
* **Mục tiêu:** Trong hàm `createOrder`, sau khi đã trừ kho sản phẩm, cố tình ném ra một ngoại lệ ở dòng cuối cùng.
* **Bộ câu hỏi tư duy:**
  1. *Kiểm chứng:* Kiểm tra Database: Số lượng sản phẩm có bị trừ không? Đơn hàng có bị lưu vào bảng `orders` không?
  2. *Ý nghĩa:* Tính toàn vẹn dữ liệu (Atomicity trong ACID: Tất cả cùng thành công hoặc tất cả cùng quay về trạng thái ban đầu) được đảm bảo như thế nào?
* **Từ khóa:** `ACID Properties in Database`, `Atomicity Spring Transaction`.

---

### 📌 Task 28: Tìm hiểu lỗi Tranh Chấp Số Lượng Tồn Kho (Race Condition)
* **Mục tiêu:** Phân tích tình huống 2 khách hàng cùng mua chiếc áo cuối cùng (kho = 1) vào cùng một tích tắc.
* **Bộ câu hỏi tư duy:**
  1. *Hiện tượng:* Luồng 1 đọc `quantity = 1`. Trước khi luồng 1 kịp lưu, luồng 2 cũng đọc `quantity = 1`. Cả 2 luồng đều kiểm tra hợp lệ và cùng trừ 1 $\rightarrow$ Kho bị âm (-1). Lỗi này gọi là gì?
  2. *Giải pháp:* Tại sao chỉ dùng `if (product.getQuantity() >= buyQuantity)` là chưa đủ trong môi trường có hàng nghìn người dùng đồng thời?
* **Từ khóa:** `Race Condition Inventory`, `Concurrency Issues in E-commerce`.

---

### 📌 Task 29: Áp dụng Khóa Lạc Quan (Optimistic Lock với `@Version`)
* **Mục tiêu:** Thêm `@Version private Long version;` vào Entity `Product`.
* **Bộ câu hỏi tư duy:**
  1. *Cơ chế:* Hibernate tự động sinh câu lệnh `UPDATE products SET quantity = 0, version = version + 1 WHERE id = 1 AND version = 0` như thế nào?
  2. *Phát hiện xung đột:* Khi luồng 2 cố update với `version = 0` (đã cũ vì luồng 1 đã tăng lên 1), Hibernate sẽ ném lỗi gì (`OptimisticLockException`)?
  3. *Đánh đổi:* Khóa lạc quan phù hợp với hệ thống đọc nhiều hay hệ thống tranh chấp ghi cực cao?
* **Từ khóa:** `@Version`, `OptimisticLockingFailureException`.

---

### 📌 Task 30: Bắt lỗi `OptimisticLockingFailureException` ở Controller
* **Mục tiêu:** Bắt ngoại lệ xung đột và trả về thông báo: "Sản phẩm đang có người khác đặt mua, vui lòng thử lại sau giây lát".
* **Bộ câu hỏi tư duy:**
  1. *Trải nghiệm người dùng:* Khi gặp lỗi này, hệ thống nên tự động thử lại (Retry Pattern) hay thông báo cho người dùng biết?
* **Từ khóa:** `Optimistic Lock Exception Handling`, `Spring Retry (Gợi ý mở rộng)`.

---

## 🔐 GIAI ĐOẠN 6: Bảo Mật Với Spring Security 6 & JWT (Nhiệm vụ 31 - 38)

### 📌 Task 31: Thiết kế Entity `User` và `Role`
* **Mục tiêu:** Tạo bảng `users` (`id`, `username`, `password`, `email`, `role`, `isActive`).
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Tại sao không nên lưu Role trực tiếp bằng String tự do mà nên dùng Enum (`Role.ADMIN`, `Role.CUSTOMER`)?
  2. *Liên kết:* Bảng `Customer` có nên liên kết 1-1 với bảng `User` không, hay gộp chung thông tin vào bảng `User`?
* **Từ khóa:** `User Entity Design`, `Role-based Access Control (RBAC)`.

---

### 📌 Task 32: Mã hóa mật khẩu với `BCryptPasswordEncoder` & Viết API Đăng Ký
* **Mục tiêu:** Tạo bean `PasswordEncoder` và mã hóa mật khẩu trước khi lưu vào DB.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Tại sao tuyệt đối không được dùng mã hóa MD5 hay SHA-256 thuần để lưu mật khẩu (do quá nhanh và dễ bị giải mã bằng Rainbow Table)?
  2. *Bản chất:* BCrypt sinh ra chuỗi Salt ngẫu nhiên như thế nào để cùng 1 mật khẩu "123456" mã hóa 2 lần ra 2 chuỗi băm hoàn toàn khác nhau?
* **Từ khóa:** `BCryptPasswordEncoder`, `Salted Hash Password`.

---

### 📌 Task 33: Tích hợp thư viện JWT & Viết `JwtTokenProvider`
* **Mục tiêu:** Viết class sinh chuỗi Token chứa: `username`, `role`, thời gian hết hạn (`expiration = 1 ngày`), và ký bằng khóa bí mật (`SECRET_KEY`).
* **Bộ câu hỏi tư duy:**
  1. *Cấu trúc:* Chuỗi JWT gồm 3 phần ngăn cách bởi dấu chấm (`xxxxx.yyyyy.zzzzz`). Mỗi phần chứa thông tin gì?
  2. *Bảo mật:* Tại sao Client không thể tự ý sửa đổi quyền từ `ROLE_CUSTOMER` thành `ROLE_ADMIN` trong chuỗi JWT (Cơ chế kiểm tra chữ ký Signature)?
* **Từ khóa:** `JWT Generation`, `HMAC SHA-256`, `Nimbus JOSE+JWT` hoặc `jjwt`.

---

### 📌 Task 34: Viết API Đăng Nhập (`POST /api/auth/login`)
* **Mục tiêu:** Nhận `username` + `password`, kiểm tra mật khẩu qua `passwordEncoder.matches(...)`, nếu đúng thì trả về Token.
* **Bộ câu hỏi tư duy:**
  1. *Xử lý lỗi:* Nếu đăng nhập sai, ta nên báo "Sai tên đăng nhập" hay "Sai mật khẩu", hay chỉ nên báo chung chung "Tên đăng nhập hoặc mật khẩu không chính xác"? (Gợi ý: Tránh tấn công User Enumeration).
* **Từ khóa:** `Login Authentication Flow`, `User Enumeration Prevention`.

---

### 📌 Task 35: Viết `JwtAuthenticationFilter` (`OncePerRequestFilter`)
* **Mục tiêu:** Đón mọi request, lấy Header `Authorization: Bearer <token>`, giải mã và nạp User vào `SecurityContextHolder`.
* **Bộ câu hỏi tư duy:**
  1. *Luồng đi:* Nếu request không có Token hoặc Token hết hạn thì Filter làm gì (Bỏ qua cho đi tiếp để Security chặn sau, hay ném lỗi ngay)?
  2. *Bộ nhớ:* `SecurityContextHolder` lưu thông tin người dùng ở đâu trong suốt vòng đời của 1 request (`ThreadLocal`)?
* **Từ khóa:** `OncePerRequestFilter`, `UsernamePasswordAuthenticationToken`, `SecurityContextHolder`.

---

### 📌 Task 36: Cấu hình `SecurityFilterChain` trong Spring Security 6
* **Mục tiêu:** Cấu hình: API `/api/auth/**` và Swagger mở công khai (`permitAll`); các API khác bắt buộc phải có Token (`authenticated`). Tắt `csrf` (vì dùng REST API stateless).
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Tại sao với REST API dùng JWT (Stateless) ta lại tắt bảo vệ CSRF (`csrf.disable()`)? Tấn công CSRF dựa trên cơ chế gì của Cookie/Session?
* **Từ khóa:** `SecurityFilterChain`, `SessionCreationPolicy.STATELESS`, `csrf.disable()`.

---

### 📌 Task 37: Phân quyền API với `@PreAuthorize`
* **Mục tiêu:** Bật `@EnableMethodSecurity`. Thêm `@PreAuthorize("hasRole('ADMIN')")` cho các hàm Thêm/Sửa/Xóa sản phẩm.
* **Bộ câu hỏi tư duy:**
  1. *Kiểm tra:* Thử dùng tài khoản `CUSTOMER` gọi API `POST /api/products` và quan sát: Spring Security trả về mã lỗi gì (403 Forbidden hay 401 Unauthorized)?
  2. *Phân biệt:* Khác nhau giữa **401 Unauthorized** (Chưa đăng nhập / Token không hợp lệ) và **403 Forbidden** (Đã đăng nhập nhưng không đủ quyền hạn)?
* **Từ khóa:** `@PreAuthorize`, `@EnableMethodSecurity`, `401 vs 403 HTTP Status`.

---

### 📌 Task 38: Tùy biến lỗi 401 và 403 theo chuẩn `ApiResponse`
* **Mục tiêu:** Viết `CustomAuthenticationEntryPoint` (bắt 401) và `CustomAccessDeniedHandler` (bắt 403) để trả về JSON theo đúng chuẩn `{ code, message, result }`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao:* Tại sao lỗi 401/403 không rơi vào `GlobalExceptionHandler` thông thường? (Vì lỗi xảy ra ở tầng Filter Chain trước khi request kịp chạm tới `DispatcherServlet` và Controller).
* **Từ khóa:** `AuthenticationEntryPoint`, `AccessDeniedHandler`, `Filter Exception Handling`.

---

## ⚡ GIAI ĐOẠN 7: Bất Đồng Bộ, Upload File & Caching (Nhiệm vụ 39 - 44)

### 📌 Task 39: Xây Dựng Service Upload File Ảnh Sản Phẩm (Local Storage)
* **Mục tiêu:** Viết API `POST /api/products/{id}/image` nhận `MultipartFile` và lưu vào thư mục `uploads/`.
* **Bộ câu hỏi tư duy:**
  1. *Kiến trúc:* Tại sao trong DB chỉ nên lưu đường dẫn (Path/URL) ví dụ `uploads/iphone-15.jpg` thay vì lưu toàn bộ file vào cột dạng `byte[]`?
* **Từ khóa:** `MultipartFile`, `Files.copy`, `File Storage Service`.

---

### 📌 Task 40: Kiểm Tra Bảo Mật File Upload (Security Validation)
* **Mục tiêu:** Chặn upload file không phải là ảnh (chỉ cho phép `.jpg`, `.png`), chặn file dung lượng $> 5MB$.
* **Bộ câu hỏi tư duy:**
  1. *Lỗ hổng bảo mật:* Nếu hacker đổi tên file mã độc `hack.php` hoặc `script.sh` thành `hack.jpg` rồi tải lên server, kiểm tra đuôi mở rộng file (.jpg) có đủ an toàn không?
  2. *Giải pháp:* Khái niệm **Magic Bytes** (chữ ký nhị phân đầu file) giúp xác thực định dạng file thực sự như thế nào?
* **Từ khóa:** `File Upload Security`, `MIME Type Checking`, `Magic Bytes File Validation`.

---

### 📌 Task 41: Bật Tính Năng Chạy Ngầm Bất Đồng Bộ (`@EnableAsync`)
* **Mục tiêu:** Tạo `EmailService` với phương thức `@Async public void sendOrderConfirmationEmail(...)`.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Khi gắn `@Async`, Spring tạo ra luồng (Thread) mới để thực thi hàm này như thế nào?
  2. *Cấu hình ThreadPool:* Tại sao trong dự án thực tế ta phải tự cấu hình `ThreadPoolTaskExecutor` (Core pool size, Max pool size, Queue capacity) thay vì dùng Thread Pool mặc định của Spring?
* **Từ khóa:** `@EnableAsync`, `@Async`, `ThreadPoolTaskExecutor`.

---

### 📌 Task 42: Tách Rời Logic Đặt Hàng & Gửi Email Bằng Spring Event
* **Mục tiêu:** Trong `OrderServiceImpl`, khi tạo đơn thành công, chỉ cần gọi `eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder))`.
* **Bộ câu hỏi tư duy:**
  1. *Nguyên lý thiết kế (SOLID):* Việc `OrderServiceImpl` không cần biết đến sự tồn tại của `EmailService` giúp code tuân thủ nguyên lý Đơn trách nhiệm (Single Responsibility) và Giảm phụ thuộc (Loose Coupling) ra sao?
  2. *Xử lý sự kiện:* Class `OrderNotificationListener` lắng nghe sự kiện bằng `@EventListener` như thế nào?
* **Từ khóa:** `ApplicationEventPublisher`, `@EventListener`, `Event-Driven Spring`.

---

### 📌 Task 43: Tích Hợp Redis & Bật Caching Cho Ứng Dụng
* **Mục tiêu:** Thêm dependency `spring-boot-starter-data-redis`, bật `@EnableCaching`.
* **Bộ câu hỏi tư duy:**
  1. *Tại sao dùng Redis:* Tốc độ đọc dữ liệu từ bộ nhớ RAM (Redis) nhanh hơn đọc từ đĩa cứng (PostgreSQL) khoảng bao nhiêu lần (tính bằng micro-giây vs mili-giây)?
  2. *Cơ chế:* Cần cấu hình `RedisCacheManager` và `GenericJackson2JsonRedisSerializer` để dữ liệu lưu vào Redis ở dạng JSON dễ đọc như thế nào?
* **Từ khóa:** `Spring Boot Redis Cache`, `RedisCacheManager`, `RedisSerializer`.

---

### 📌 Task 44: Áp Dụng `@Cacheable` và `@CacheEvict` Cho Danh Mục
* **Mục tiêu:** Gắn `@Cacheable(value = "categories")` ở hàm `getAllCategories()`, và gắn `@CacheEvict(value = "categories", allEntries = true)` ở các hàm Thêm/Sửa/Xóa.
* **Bộ câu hỏi tư duy:**
  1. *Kiểm chứng:* Gọi API lấy danh mục lần 1 (thấy log Hibernate query SQL). Gọi lần 2 (không thấy log SQL nào vì dữ liệu lấy từ Redis).
  2. *Vấn đề dữ liệu cũ (Stale Data):* Nếu quên gắn `@CacheEvict` khi Admin đổi tên danh mục thì người dùng sẽ nhìn thấy tên cũ hay tên mới?
* **Từ khóa:** `@Cacheable`, `@CacheEvict`, `Cache-Aside Pattern`.

---

## 🧪 GIAI ĐOẠN 8: Kiểm Thử Tự Động, Logging & Đóng Gói (Nhiệm vụ 45 - 50)

### 📌 Task 45: Viết Unit Test Đầu Tiên Với JUnit 5 Cho `CategoryMapper`
* **Mục tiêu:** Viết class `CategoryMapperTest` kiểm tra hàm `toEntity()` và `toResponse()`.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Tại sao Unit Test cho Mapper không cần khởi động cả ứng dụng Spring Boot (`@SpringBootTest`) mà chỉ cần chạy bằng JUnit thuần (tốc độ chạy dưới 10 mili-giây)?
  2. *Khẳng định:* Dùng các lệnh `assertEquals`, `assertNotNull` để kiểm tra kết quả ra sao?
* **Từ khóa:** `JUnit 5 @Test`, `Assertions.assertEquals`.

---

### 📌 Task 46: Viết Unit Test Cho `CategoryServiceImpl` Với Mockito
* **Mục tiêu:** Dùng `@Mock CategoryRepository` và `@InjectMocks CategoryServiceImpl`.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Tại sao khi test tầng Service ta lại "giả lập" (Mock) tầng Repository thay vì gọi DB thật?
  2. *Kỹ thuật:* Cách dùng `when(categoryRepository.findById(1L)).thenReturn(Optional.of(category))` để định nghĩa hành vi giả lập.
  3. *Test lỗi:* Cách dùng `assertThrows(AppException.class, () -> categoryService.getCategoryById(99L))` để kiểm tra trường hợp không tìm thấy dữ liệu.
* **Từ khóa:** `Mockito @Mock`, `@InjectMocks`, `when().thenReturn()`, `assertThrows`.

---

### 📌 Task 47: Viết Integration Test Cho Controller Với `MockMvc`
* **Mục tiêu:** Viết `CategoryControllerTest` với `@WebMvcTest` hoặc `@SpringBootTest` + `AutoConfigureMockMvc`.
* **Bộ câu hỏi tư duy:**
  1. *Khác biệt:* Integration Test khác Unit Test ở điểm nào? (Kiểm tra cả việc map JSON, validation `@Valid`, HTTP Status Code và Filter).
  2. *Cú pháp:* `mockMvc.perform(get("/api/categories")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(1000))`.
* **Từ khóa:** `MockMvc`, `@WebMvcTest`, `jsonPath Assertions`.

---

### 📌 Task 48: Ghi Log Chuẩn (SLF4J) & Tự Động Gắn `Trace-Id` Với MDC Filter
* **Mục tiêu:** Tạo `CorrelationIdFilter` tự động sinh 1 chuỗi UUID `traceId` và nạp vào `MDC.put("traceId", traceId)`.
* **Bộ câu hỏi tư duy:**
  1. *Tình huống thực tế:* Khi server có 10.000 user gọi API cùng lúc, log in ra terminal xen lẫn nhau. Nhờ có `traceId` xuất hiện ở đầu mỗi dòng log, làm sao ta lọc ra được toàn bộ hành trình của đúng 1 request bị lỗi?
* **Từ khóa:** `SLF4J Logger`, `MDC (Mapped Diagnostic Context)`, `Correlation ID Pattern`.

---

### 📌 Task 49: Tích Hợp Health Check Với Spring Boot Actuator
* **Mục tiêu:** Thêm `spring-boot-starter-actuator`, truy cập `/actuator/health` để kiểm tra trạng thái Database và Disk Space.
* **Bộ câu hỏi tư duy:**
  1. *Giám sát tự động:* Làm sao các hệ thống như Kubernetes hoặc AWS Load Balancer biết server Spring Boot còn sống hay đã bị treo để tự động khởi động lại container?
* **Từ khóa:** `Spring Boot Actuator`, `/actuator/health`, `Liveness and Readiness Probes`.

---

### 📌 Task 50: Viết `Dockerfile` & `docker-compose.yml` Đóng Gói Toàn Bộ Hệ Thống
* **Mục tiêu:** Chỉ cần chạy `docker compose up -d` là tự động chạy: App Spring Boot + PostgreSQL + Redis.
* **Bộ câu hỏi tư duy:**
  1. *Bản chất:* Multi-stage build trong Dockerfile (Stage 1: Maven build ra file jar, Stage 2: Chỉ lấy file jar chạy trên JRE siêu nhẹ) giúp giảm dung lượng image từ 600MB xuống 150MB như thế nào?
  2. *Môi trường:* Lợi ích của việc "chạy ở máy tôi được thì lên server chắc chắn chạy được" của Docker là gì?
* **Từ khóa:** `Multi-stage Dockerfile Spring Boot`, `docker-compose.yml`, `Containerization`.

---

## 🏆 KẾ HOẠCH BẮT ĐẦU:
Hãy xem mỗi Task là một "viên gạch nhỏ". Khi bạn xây xong 50 viên gạch này, bạn không chỉ có một project hoàn chỉnh mà còn sở hữu **tư duy kiến trúc vững vàng của một Backend Developer chuyên nghiệp**.

👉 **Bắt đầu với Task 1 ngay bây giờ:** Hãy đọc lại câu hỏi của **Task 1** và cho tôi biết suy nghĩ của bạn!
