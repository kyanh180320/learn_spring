# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN (CHUẨN SINGLE RESPONSIBILITY & KHUNG 5D)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn 20%**: Mỗi bước tiến lên một nấc thang tự nhiên, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

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

