# 🧭 LỘ TRÌNH SPRING BOOT THỰC CHIẾN — PHẦN 2: TRUY VẤN, CONCURRENCY & BẢO MẬT TOÀN DIỆN (TASKS 22 - 51)

> **🎯 Quy Chuẩn Thiết Kế Lộ Trình:**
> - **1 Task = Đúng 1 Mục Tiêu Duy Nhất (Single Focus)**: Không nhồi nhét, không pha trộn nhiều khái niệm trong 1 bài toán.
> - **Ngưỡng thử thách chuẩn $\le 20\%$**: Mỗi bước tiến lên một nấc thang tự nhiên, chuyển tiếp mượt mà, không nhảy cóc kiến thức.
> - **100% Thực hành (Coding-First)**: Mọi câu hỏi đều gắn liền với file code đang làm việc.
> - **Khung Tư Duy 5 Chiều (5D Framework)** cho từng bài toán.

---

## 🔍 GIAI ĐOẠN 4: Tối Ưu Truy Vấn, N+1 & Tìm Kiếm Động (Tasks 22 - 30)

### 📌 Task 22: Viết Câu Truy Vấn JPQL Tùy Biến Đầu Tiên Với `@Query`
* **Hành động code:** Thêm hàm `@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")` vào `ProductRepository`.
* **Mục tiêu duy nhất:** Hiểu bản chất ngôn ngữ truy vấn thực thể JPQL (Java Persistence Query Language) độc lập cơ sở dữ liệu.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất JPQL]:* JPQL thao tác trên cái gì (Entity Class và Field Java) khác gì với SQL thuần thao tác trên Table và Column?
  2. ⚠️ *[Lỗi Thường Gặp]:* Tại sao viết `SELECT * FROM products` trong `@Query` lại ném lỗi `IllegalArgumentException` lúc khởi động app?
  3. ⚖️ *[So sánh]:* **JPQL** vs **Native Query** (`nativeQuery = true`). Khi nào BẮT BUỘC phải dùng Native Query (ví dụ: dùng hàm JSONB của PostgreSQL)?
  4. 🔄 *[Đánh đổi]:* JPQL an toàn kiểu dữ liệu và tự động đổi dialect theo DB, nhưng có hạn chế gì khi cần viết câu query phân tích dữ liệu siêu phức tạp?
  5. 🏢 *[Thực tế]:* Viết API lọc sản phẩm theo khoảng giá và xem câu lệnh SQL thực tế Hibernate sinh ra ở Terminal.
* **Từ khóa:** `JPQL (@Query)`, `Native Query vs JPQL`, `Entity-based Querying`, `Database Independence`.

---

### 📌 Task 23: Viết Câu JPQL Tìm Kiếm Sản Phẩm Theo Tên Danh Mục
* **Hành động code:** Viết câu truy vấn `@Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")`.
* **Mục tiêu duy nhất:** Nắm vững cú pháp liên kết quan hệ (JOIN) trong JPQL và xử lý so khớp chuỗi không phân biệt hoa thường.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất JPQL JOIN]:* Tại sao trong JPQL ta viết `JOIN p.category c` mà không cần viết điều kiện `ON p.category_id = c.id` (Hibernate tự suy ra từ mapping `@ManyToOne`)?
  2. ⚠️ *[Vấn đề Hiệu năng]:* Dùng `LIKE '%keyword%'` (dấu `%` ở đầu) khiến Database không thể tận dụng B-Tree Index ra sao?
  3. ⚖️ *[So sánh]:* `INNER JOIN` (bỏ qua sản phẩm chưa có danh mục) vs `LEFT JOIN` (giữ lại cả sản phẩm chưa gán danh mục).
  4. 🔄 *[Bảo mật]:* Sử dụng `:categoryName` (Named Parameter) bảo vệ chống tấn công SQL Injection thế nào so với việc nối chuỗi?
  5. 🏢 *[Thực tế]:* Gọi API tìm kiếm theo danh mục "Điện tử" và quan sát câu SQL JOIN sinh ra ở log console.
* **Từ khóa:** `JPQL JOIN`, `Case-insensitive Search (LOWER)`, `Named Parameter Binding`, `B-Tree Index Degradation`.

---

### 📌 Task 24: Bắt & Đo Lường Lỗi N+1 Query Thực Tế Với Hibernate Statistics
* **Hành động code:** Bật `hibernate.generate_statistics=true` trong `application-dev.yml`, gọi API lấy danh sách đơn hàng kèm chi tiết và nhìn công tơ mét số câu query sinh ra.
* **Mục tiêu duy nhất:** Tận mắt nhìn thấy lỗi N+1 Query làm bùng nổ số lượng truy vấn database và học cách đo đạc chính xác.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất N+1]:* Lỗi N+1 Query sinh ra khi nào (1 câu SELECT lấy N bản ghi cha, sau đó vòng lặp gọi getter lấy con sinh thêm N câu SELECT con)?
  2. ⚠️ *[Thảm họa Tải cao]:* Nếu bảng Order có 100 dòng, số câu query gửi tới Database là bao nhiêu (101 câu)? Database bị nghẽn mạng và cạn Connection Pool ra sao?
  3. ⚖️ *[Giải pháp]:* So sánh: Dùng **JOIN FETCH** (`SELECT o FROM Order o JOIN FETCH o.orderItems`) vs Dùng `@EntityGraph`.
  4. 🔄 *[Đánh đổi của JOIN FETCH]:* JOIN FETCH giải quyết triệt để N+1 nhưng tại sao **KHÔNG ĐƯỢC** dùng `JOIN FETCH` cùng lúc với phân trang `Pageable` trên tập hợp `@OneToMany` (Lỗi Warning Memory Pagination)?
  5. 🏢 *[Thực tế]:* Sửa câu query trong Repository bằng `JOIN FETCH`, gọi lại API và quan sát thống kê: Số lượng query giảm từ 21 câu xuống đúng 1 câu duy nhất!
* **Từ khóa:** `N+1 Query Problem`, `Hibernate Statistics`, `JOIN FETCH`, `@EntityGraph`, `Memory Pagination Pitfall`.

---

### 📌 Task 25: Tối Ưu Bộ Nhớ Với DTO Projection
* **Hành động code:** Tạo interface `ProductSummaryProjection` (chỉ chứa `getId()`, `getName()`, `getPrice()`) hoặc dùng Class-based DTO với từ khóa `SELECT new com.example...Dto(...)`.
* **Mục tiêu duy nhất:** Chỉ chọn đúng các cột cần dùng, tránh việc nạp toàn bộ Entity vào Hibernate Persistence Context (L1 Cache).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Projection]:* Khi chỉ cần hiển thị màn hình danh sách, việc nạp toàn bộ Entity chứa 20 cột (kèm mô tả dài hàng ngàn ký tự) gây lãng phí bộ nhớ RAM JVM thế nào?
  2. 🔬 *[Persistence Context]:* Các đối tượng lấy ra qua DTO Projection có bị Hibernate quản lý trạng thái (Dirty Checking) không? Tại sao điều này giúp tăng tốc độ xử lý?
  3. ⚖️ *[So sánh]:* **Interface-based Projection** (Spring Data sinh Proxy ngầm) vs **Constructor-based Projection (Class DTO)**. Cách nào an toàn type và hiệu năng cao hơn?
  4. 🔄 *[Đánh đổi]:* DTO Projection tối ưu RAM tối đa nhưng không thể gọi `save()` trực tiếp để cập nhật mà phải load Entity nếu muốn sửa.
  5. 🏢 *[Thực tế]:* Viết API `GET /api/v1/products/summary` dùng DTO Projection và quan sát câu SQL Hibernate sinh ra: Chỉ `SELECT id, name, price` thay vì `SELECT *`.
* **Từ khóa:** `Spring Data Projection`, `Constructor-based DTO Projection`, `Hibernate Persistence Context Bypass`, `Memory Optimization`.

---

### 📌 Task 26: Làm Quen Với JPA Specification (Criteria API)
* **Hành động code:** Cho `ProductRepository` kế thừa `JpaSpecificationExecutor<Product>`, viết một hàm Specification lọc theo trạng thái còn hàng.
* **Mục tiêu duy nhất:** Hiểu cách xây dựng câu truy vấn động an toàn kiểu dữ liệu (Type-safe Dynamic Query) bằng Java Code thay vì ghép chuỗi SQL.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Criteria API]:* `Root<T>`, `CriteriaQuery<?>`, `CriteriaBuilder` đóng vai trò gì trong việc mô hình hóa một câu lệnh SQL bằng hướng đối tượng?
  2. ⚠️ *[Nỗi sợ Ghép chuỗi SQL]:* Nếu dùng cộng chuỗi: `"WHERE 1=1 " + (name != null ? "AND name = " + name : "")`, nguy cơ lỗi cú pháp và SQL Injection nguy hiểm ra sao?
  3. ⚖️ *[So sánh]:* **Specification (Criteria API)** vs **QueryDSL**. Điểm mạnh của Specification là tích hợp sẵn trong Spring Data không cần cài thêm plugin sinh code (`Q-classes`).
  4. 🔄 *[Đánh đổi]:* Cú pháp Criteria API dài dòng và khó đọc hơn JPQL, nhưng đem lại khả năng ghép điều kiện lọc động linh hoạt tuyệt đối.
  5. 🏢 *[Thực tế]:* Viết Specification đơn giản nhất: `(root, query, cb) -> cb.isFalse(root.get("isDeleted"))`.
* **Từ khóa:** `JpaSpecificationExecutor`, `JPA Specification`, `CriteriaBuilder`, `Type-safe Dynamic Query`.

---

### 📌 Task 27: Thêm Điều Kiện Lọc Khoảng Giá Và Tên Vào `ProductSpecification`
* **Hành động code:** Tạo class tiện ích `ProductSpecs` chứa các hàm static: `hasName(String name)`, `priceBetween(BigDecimal min, BigDecimal max)`, `hasCategoryId(Long categoryId)`.
* **Mục tiêu duy nhất:** Module hóa các điều kiện lọc thành các viên gạch độc lập (Composable Specifications) theo nguyên lý Single Responsibility.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Kỹ thuật Composable]:* Toán tử `Specification.where().and(...)` kết hợp các biểu thức logic SQL (`AND`, `OR`) như thế nào?
  2. ⚠️ *[Xử lý Điều kiện Null]:* Nếu người dùng không truyền `minPrice` thì hàm Specification trả về cái gì (`null` hoặc `cb.conjunction()`) để không ảnh hưởng đến các điều kiện lọc khác?
  3. 🔬 *[So khớp Chuỗi]:* Dùng `cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")` để tìm kiếm không phân biệt hoa thường.
  4. 🔄 *[Đánh đổi]:* Tách nhỏ từng Specification giúp tái sử dụng và viết Unit Test cho từng điều kiện lọc cực kỳ dễ dàng.
  5. 🏢 *[Thực tế]:* Viết test kết hợp 2 điều kiện: vừa có tên chứa "iPhone" vừa có giá từ 10 triệu đến 20 triệu.
* **Từ khóa:** `Composable Specification`, `Specification.where().and()`, `Null-safe Predicate Building`.

---

### 📌 Task 28: Ghép Nối Specification Thành API Tìm Kiếm Linh Hoạt
* **Hành động code:** Tạo `ProductFilterRequest` chứa các tiêu chí lọc tùy chọn, cập nhật Controller tiếp nhận và truyền vào Repository cùng với `Pageable`.
* **Mục tiêu duy nhất:** Hoàn thiện API tìm kiếm đa tiêu chí chuyên nghiệp kết hợp đồng thời cả Lọc động (Dynamic Filter) và Phân trang (Pagination).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Kiến trúc]:* Làm sao tầng Controller truyền DTO tiêu chí lọc xuống Service để tự động ráp thành một Specification hoàn chỉnh?
  2. ⚠️ *[Bẫy lỗi Hiệu năng]:* Nếu khách hàng gửi request không chọn bất kỳ bộ lọc nào (tất cả trường đều null), Specification sẽ sinh ra câu query gì (`SELECT * FROM products LIMIT 10`)?
  3. ⚖️ *[So sánh]:* Viết 10 hàm `findBy...` khác nhau trong Repository vs Dùng 1 hàm `findAll(Specification, Pageable)` duy nhất.
  4. 🔄 *[Đánh đổi]:* Specification rất mạnh cho các bộ lọc thông thường, nhưng với tính năng tìm kiếm văn bản toàn văn (Full-text Search) hàng triệu sản phẩm thì nên chuyển sang công nghệ gì (Elasticsearch)?
  5. 🏢 *[Thực tế]:* Mở Postman test: Lọc chỉ theo tên, lọc chỉ theo giá, lọc kết hợp cả tên + giá + danh mục + phân trang.
* **Từ khóa:** `Dynamic Search API`, `Specification + Pageable Integration`, `Flexible Filtering Architecture`.

---

### 📌 Task 29: Đánh Index Database Và Phân Tích Bằng `EXPLAIN ANALYZE`
* **Hành động code:** Tạo Migration/DDL thêm Index cho cột `category_id`, `price`; dùng DBeaver/pgAdmin chạy `EXPLAIN ANALYZE` câu query tìm kiếm để so sánh thời gian thực thi trước và sau khi đánh index.
* **Mục tiêu duy nhất:** Hiểu bản chất cấu trúc chỉ mục B-Tree trong Database và cách đo lường chi phí truy vấn thực tế.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất B-Tree Index]:* Cấu trúc cây B-Tree giúp giảm độ phức tạp tìm kiếm từ $O(N)$ (Quét tuần tự cả bảng - Seq Scan) xuống $O(\log N)$ (Quét chỉ mục - Index Scan) ra sao?
  2. 🔬 *[Đọc EXPLAIN ANALYZE]:* Ý nghĩa của 2 chỉ số quan trọng nhất: **Execution Time** (Thời gian thực thi) và **Cost** (Chi phí tài nguyên CPU/IO).
  3. ⚠️ *[Tác dụng phụ của Index]:* Tại sao không nên đánh index lên TẤT CẢ các cột trong bảng (Làm chậm thao tác `INSERT`, `UPDATE`, `DELETE` và tốn dung lượng đĩa)?
  4. ⚖️ *[Composite Index]:* Khi nào cần đánh Index ghép trên nhiều cột (ví dụ: `(category_id, price)`)? Thứ tự các cột trong Index ghép quan trọng thế nào?
  5. 🏢 *[Thực tế]:* Quan sát kết quả `EXPLAIN ANALYZE`: Thời gian query giảm từ 85ms xuống 1.2ms sau khi có index!
* **Từ khóa:** `Database Indexing (B-Tree)`, `EXPLAIN ANALYZE`, `Seq Scan vs Index Scan`, `Index Trade-offs (Read vs Write)`.

---

### 📌 Task 30: (MỚI) Tối Ưu Connection Pooling HikariCP & Phát Hiện Connection Leak
* **Hành động code:** Cấu hình các tham số cốt lõi của HikariCP trong `application.yml` (`maximum-pool-size`, `minimum-idle`, `connection-timeout`, `idle-timeout`, `leak-detection-threshold = 2000ms`), tạo endpoint mô phỏng việc giữ connection quá lâu để kích hoạt cảnh báo rò rỉ.
* **Mục tiêu duy nhất:** Nắm vững cách quản lý vòng đời kết nối cơ sở dữ liệu với HikariCP và bảo vệ hệ thống khỏi sự cố cạn kiệt Connection Pool.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Connection Pool]:* Tại sao việc mở và đóng một kết nối TCP đến Database tốn kém (TCP Handshake, Xác thực SSL/Auth, Cấp phát bộ nhớ DB)? HikariCP tái sử dụng connection thế nào?
  2. ⚠️ *[Thảm họa Treo Server]:* Nếu đặt `maximum-pool-size=10` nhưng có 11 request đồng thời xử lý chậm quá `connection-timeout` (mặc định 30s), request thứ 11 sẽ bị lỗi gì (`SQLTransientConnectionException: Connection is not available`)?
  3. 🔬 *[Cơ chế Leak Detection]:* Cấu hình `leak-detection-threshold=2000` (2 giây) giúp HikariCP in ra stacktrace chính xác dòng code nào đang chiếm dụng kết nối mà chưa trả về pool ra sao?
  4. ⚖️ *[Công thức sizing pool]:* Quy tắc nổi tiếng của PostgreSQL: $\text{pool size} = (\text{core\_count} \times 2) + \text{effective\_spindle\_count}$. Tại sao đặt pool size quá lớn (ví dụ: 100) lại làm HỆ THỐNG CHẬM HƠN thay vì nhanh hơn (Nghẽn Context Switching ở CPU Database)?
  5. 🏢 *[Thực tế]:* Viết hàm giữ connection ngủ 3 giây, quan sát console in ra dòng cảnh báo đỏ: `Apparent connection leak detected` kèm tên luồng và file Java gây lỗi.
* **Từ khóa:** `HikariCP Tuning`, `Connection Pool Sizing Formula`, `leak-detection-threshold`, `SQLTransientConnectionException`, `Resource Leak Defense`.

---

## ⚡ GIAI ĐOẠN 5: Transaction, Quản Lý Trạng Thái & Xử Lý Tranh Chấp (Tasks 31 - 37)

### 📌 Task 31: Thiết Kế Luồng Vòng Đời Trạng Thái Đơn Hàng
* **Hành động code:** Tạo Enum `OrderStatus` gồm: `PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`; viết logic kiểm tra tính hợp lệ khi chuyển trạng thái (State Transition).
* **Mục tiêu duy nhất:** Ngăn chặn việc đơn hàng nhảy cóc trạng thái phi lý (ví dụ: từ `CANCELLED` mà lại chuyển sang `DELIVERED`).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Máy trạng thái]:* Tại sao không bao giờ được phép cho client gửi thẳng trạng thái mới lên để update vào DB mà phải thông qua các hàm nghiệp vụ có kiểm soát (`order.cancel()`, `order.ship()`)?
  2. ⚠️ *[Rủi ro Nghiệp vụ]:* Khách hàng đã hủy đơn hàng (`CANCELLED`), nếu hệ thống kho vẫn xuất hàng giao đi (`SHIPPING`) thì gây thiệt hại tài chính gì?
  3. ⚖️ *[So sánh]:* Dùng `if/else` kiểm tra trong Service vs Áp dụng **State Design Pattern** hoặc thư viện **Spring State Machine**. Khi nào nên dùng State Machine?
  4. 🔄 *[Đánh đổi]:* Kiểm soát chặt chẽ trạng thái làm tăng độ phức tạp của code nhưng bảo đảm tính toàn vẹn dữ liệu nghiệp vụ 100%.
  5. 🏢 *[Thực tế]:* Viết Unit Test cố tình chuyển từ `CANCELLED` sang `DELIVERED` và khẳng định hệ thống ném ngoại lệ `IllegalStateException`.
* **Từ khóa:** `OrderStatus Enum`, `Finite State Machine (FSM)`, `State Transition Validation`, `Business Invariant Protection`.

---

### 📌 Task 32: Cơ Chế Rollback Của `@Transactional` & Kịch Bản Lỗi Giả Lập
* **Hành động code:** Viết hàm `createOrder()` trừ tiền khách hàng và ném lỗi giả lập `RuntimeException`; quan sát cơ chế tự động Rollback giao dịch trong Database.
* **Mục tiêu duy nhất:** Hiểu sâu tính nguyên tử (Atomicity) trong chuẩn ACID và cơ chế Proxy AOP của Spring Transaction.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Rollback]:* Mặc định Spring `@Transactional` chỉ tự động rollback với những ngoại lệ nào (`RuntimeException` và `Error`, không tự rollback với Checked Exception như `Exception` hay `IOException`)?
  2. ⚠️ *[Cú lừa Checked Exception]:* Nếu trong hàm có `throw new IOException()`, tiền đã trừ có bị rollback không? Cách khắc phục: `@Transactional(rollbackFor = Exception.class)`.
  3. ⚠️ *[Cạm bẫy Gọi Nội bộ (Self-invocation)]:* Tại sao gọi hàm `@Transactional` từ một hàm khác trong CÙNG MỘT CLASS thì Transaction hoàn toàn bị vô hiệu hóa (Bypass Spring AOP Proxy)?
  4. ⚖️ *[Transaction Propagation]:* Phân biệt `REQUIRED` (dùng chung transaction có sẵn) vs `REQUIRES_NEW` (tạm dừng transaction cũ, mở transaction hoàn toàn độc lập).
  5. 🏢 *[Thực tế]:* Cố tình ném lỗi ở bước cuối cùng của việc tạo đơn: Xác nhận trong DB cả bản ghi Order và trừ tiền Customer đều biến mất như chưa từng có chuyện gì xảy ra!
* **Từ khóa:** `@Transactional(rollbackFor = Exception.class)`, `Self-invocation AOP Trap`, `Transaction Propagation (REQUIRED vs REQUIRES_NEW)`, `ACID Atomicity`.

---

### 📌 Task 33: Viết Test 2 Luồng Đơn Giản Tái Hiện Bug Trừ Kho Âm (Lost Update)
* **Hành động code:** Tạo test case dùng `CountDownLatch` hoặc `ExecutorService` cho 2 luồng cùng đọc số lượng tồn kho còn `1` và cùng bấm mua `1`.
* **Mục tiêu duy nhất:** Tái hiện lỗi kinh điển Race Condition / Lost Update trong môi trường đa luồng đồng thời mà không cần dùng API phức tạp.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Race Condition]:* Luồng A đọc kho = 1, Luồng B đọc kho = 1. Cả 2 luồng đều kiểm tra `1 >= 1` hợp lệ. Cả 2 cùng trừ và lưu lại kho = 0 $\rightarrow$ Kết quả: Bán được 2 sản phẩm nhưng kho chỉ trừ 1 (hoặc kho bị âm `-1`)!
  2. ⚠️ *[Hậu quả Thực tế]:* Trong các đợt Flash Sale (Shopee/Lazada), lỗi này gây ra hiện tượng Bán vượt số lượng thực tế (Overselling), doanh nghiệp không có hàng giao cho khách.
  3. ⚖️ *[Công cụ Tái hiện]:* `CountDownLatch(2)` giúp đồng bộ hóa 2 luồng xuất phát chính xác tại cùng một thời điểm mili-giây như thế nào?
  4. 🔄 *[Môi trường Test]:* Tại sao lỗi này hầu như không bao giờ xuất hiện khi test thủ công bằng tay (vì tay người không thể bấm 2 nút cách nhau dưới 1 mili-giây)?
  5. 🏢 *[Thực tế]:* Chạy bài test và nhìn thấy kết quả test bị FAIL (Số lượng kho bị sai lệch), chính thức ghi nhận lỗi để chuẩn bị khắc phục ở Task 34.
* **Từ khóa:** `Race Condition`, `Lost Update Anomaly`, `Inventory Overselling Bug`, `CountDownLatch Concurrency Testing`.

---

### 📌 Task 34: Khắc Phục Bằng Khóa Lạc Quan (Optimistic Lock Với `@Version`)
* **Hành động code:** Thêm `@Version private Long version;` vào Entity `Product`, chạy lại bài test 2 luồng ở Task 33.
* **Mục tiêu duy nhất:** Hiểu cơ chế phát hiện xung đột không cần khóa dữ liệu (Non-blocking Concurrency Control).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `@Version`]:* Hibernate tự động sinh câu lệnh `UPDATE products SET stock = ?, version = version + 1 WHERE id = ? AND version = ?` như thế nào?
  2. ⚠️ *[Ngoại lệ Ném ra]:* Khi luồng thứ 2 cập nhật với `version` cũ đã bị thay đổi, Hibernate ném lỗi gì (`OptimisticLockException` / `ObjectOptimisticLockingFailureException`)?
  3. ⚖️ *[So sánh]:* Khóa Lạc quan (Optimistic) vs Khóa Bi quan (Pessimistic). Tại sao Optimistic Lock có hiệu năng đọc cao vượt trội (không bắt các luồng khác phải chờ xếp hàng)?
  4. 🔄 *[Kịch bản Phù hợp]:* Khi nào nên dùng Khóa Lạc quan (Tỷ lệ tranh chấp thấp hoặc trung bình: sửa thông tin cá nhân, đơn hàng thông thường)?
  5. 🏢 *[Thực tế]:* Chạy lại bài test Task 33: Luồng 1 mua thành công, Luồng 2 bị ném lỗi `OptimisticLockException` $\rightarrow$ Số lượng tồn kho được bảo vệ an toàn 100%!
* **Từ khóa:** `@Version Annotation`, `Optimistic Locking`, `ObjectOptimisticLockingFailureException`, `Non-blocking Concurrency`.

---

### 📌 Task 35: Xử Lý Lỗi Xung Đột Phiên Bản & Chiến Lược Thử Lại (Spring Retry)
* **Hành động code:** Tích hợp `@EnableRetry`, gắn `@Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))` vào hàm cập nhật.
* **Mục tiêu duy nhất:** Tự động thử lại giao dịch khi xảy ra xung đột phiên bản mà không đẩy lỗi về phía người dùng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Spring Retry]:* Spring Retry sử dụng AOP Proxy để bắt ngoại lệ chỉ định và thực thi lại method từ đầu ra sao?
  2. ⚠️ *[Đọc Lại Dữ Liệu Mới]:* Khi thử lại lần 2, tại sao bắt buộc phải đọc lại dữ liệu mới nhất từ Database kèm version mới để tính toán lại?
  3. ⚖️ *[Chiến lược Backoff]:* Tại sao không nên thử lại ngay lập tức mà nên có độ trễ (`delay = 100ms`) hoặc độ trễ hàm mũ (Exponential Backoff)?
  4. 🔄 *[Phương thức Fallback]:* Annotation `@Recover` dùng để làm gì khi đã thử lại quá số lần quy định (`maxAttempts = 3`) mà vẫn thất bại?
  5. 🏢 *[Thực tế]:* Chạy test đa luồng: Luồng 2 bị xung đột phiên bản $\rightarrow$ Tự động retry lần 2 thành công êm đẹp mà client không hề nhận bất kỳ lỗi nào!
* **Từ khóa:** `@Retryable & @Recover`, `Spring Retry`, `Exponential Backoff Strategy`, `Automatic Conflict Resolution`.

---

### 📌 Task 36: Khắc Phục Bằng Khóa Bi Quan (Pessimistic Lock Với `SELECT FOR UPDATE`)
* **Hành động code:** Thêm hàm `@Lock(LockModeType.PESSIMISTIC_WRITE)` vào `ProductRepository`.
* **Mục tiêu duy nhất:** Khóa cứng dòng dữ liệu ở tầng Database, ép các giao dịch phải xếp hàng tuần tự.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `SELECT FOR UPDATE`]:* Câu lệnh `SELECT ... FOR UPDATE` can thiệp vào cơ chế Row-level Lock của Database Engine (InnoDB / PostgreSQL) như thế nào?
  2. ⚠️ *[Hiện tượng Treo luồng]:* Trong khi Luồng 1 đang giữ khóa, Luồng 2 gọi hàm đọc sẽ ở trạng thái nào (Blocked / Treo chờ đến khi Luồng 1 commit)?
  3. ⚖️ *[So sánh Toàn diện]:* **Khóa Bi quan (Pessimistic)** vs **Khóa Lạc quan (Optimistic)**. Khi nào BẮT BUỘC dùng Khóa Bi quan (Flash Sale cực nóng, Giao dịch ví tiền tài chính - nơi việc retry tốn kém hơn việc chờ đợi)?
  4. 🔄 *[Đánh đổi Hiệu năng]:* Khóa bi quan loại bỏ 100% lỗi xung đột nhưng làm giảm đáng kể Throughput (số lượng request xử lý mỗi giây) của hệ thống ra sao?
  5. 🏢 *[Thực tế]:* Chạy test trừ kho với Pessimistic Lock: Cả 2 luồng đều xử lý thành công tuần tự mà không cần cơ chế retry.
* **Từ khóa:** `@Lock(LockModeType.PESSIMISTIC_WRITE)`, `SELECT FOR UPDATE`, `Row-level Locking`, `High-Contention Concurrency Control`.

---

### 📌 Task 37: Nhận Diện Deadlock & Cấu Hình Khóa Timeout
* **Hành động code:** Cố tình tạo kịch bản 2 luồng khóa chéo 2 sản phẩm A và B; cấu hình `jakarta.persistence.lock.timeout` để tránh hệ thống bị treo vĩnh viễn.
* **Mục tiêu duy nhất:** Hiểu nguyên nhân gây bế tắc (Deadlock) trong cơ sở dữ liệu và cách thiết lập thời gian chờ tối đa.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Deadlock]:* Kịch bản kinh điển: Luồng 1 giữ A chờ B, Luồng 2 giữ B chờ A. Cả 2 luồng đứng nhìn nhau chờ đợi vô tận ra sao?
  2. 🔬 *[Cơ chế Deadlock Detector của DB]:* Database Engine tự động phát hiện chu trình phụ thuộc (Wait-For Graph) và "giết" một trong 2 giao dịch (chọn làm nạn nhân - Victim) để giải phóng hệ thống thế nào?
  3. 🛡️ *[Quy tắc Phòng chống Deadlock]:* **Nguyên tắc vàng:** Mọi giao dịch trong hệ thống luôn luôn phải khóa tài nguyên theo cùng một thứ tự (ví dụ: luôn sắp xếp `productIds` tăng dần trước khi khóa)!
  4. ⚖️ *[Cấu hình Lock Timeout]:* Dùng `@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})` để tự hủy nếu chờ quá 3 giây.
  5. 🏢 *[Thực tế]:* Tái hiện Deadlock trong bài test và thấy Database ném `PessimisticLockingFailureException` hoặc `CannotAcquireLockException`.
* **Từ khóa:** `Database Deadlock`, `Lock Ordering Strategy`, `jakarta.persistence.lock.timeout`, `PessimisticLockingFailureException`.

---

## 🔒 GIAI ĐOẠN 6: Authentication, Authorization & Security Toàn Diện (Tasks 38 - 48)

### 📌 Task 38: Thiết Kế Entity `User` Và `Role`
* **Hành động code:** Tạo `User`, `Role`, bảng trung gian `users_roles` (`@ManyToMany`), implement interface `UserDetails` của Spring Security.
* **Mục tiêu duy nhất:** Chuẩn hóa mô hình phân quyền theo vai trò (RBAC - Role-Based Access Control) tương thích hoàn toàn với Spring Security Core.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Interface `UserDetails`]:* Các hàm `getAuthorities()`, `isAccountNonLocked()`, `isEnabled()` giúp Spring Security kiểm soát trạng thái đăng nhập ra sao?
  2. ⚠️ *[Tiền tố ROLE_]:* Tại sao trong Spring Security, vai trò luôn phải có tiền tố `ROLE_` (ví dụ: `ROLE_ADMIN`, `ROLE_CUSTOMER`) khi dùng `hasRole()`?
  3. ⚖️ *[So sánh FetchType]:* Quan hệ `@ManyToMany` giữa User và Role nên để `FetchType.EAGER` hay `FetchType.LAZY`? Tại sao LAZY an toàn hơn nhưng cần cẩn thận lỗi `LazyInitializationException`?
  4. 🔄 *[Đánh đổi]:* Phân quyền theo Vai trò (Role-based) vs Phân quyền theo Hành động chi tiết (Permission-based / Authority: `ORDER_READ`, `PRODUCT_WRITE`).
  5. 🏢 *[Thực tế]:* Khởi tạo Entity và quan sát Hibernate sinh ra đúng 3 bảng: `users`, `roles`, và bảng quan hệ `users_roles`.
* **Từ khóa:** `UserDetails Implementation`, `Role-Based Access Control (RBAC)`, `ROLE_ Prefix Convention`, `LazyInitializationException in Security`.

---

### 📌 Task 39: Cấu Hình `PasswordEncoder` Với `BCryptPasswordEncoder`
* **Hành động code:** Khai báo `@Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }`.
* **Mục tiêu duy nhất:** Hiểu thuật toán băm mật khẩu một chiều có muối (Salted Hash) và lý do tuyệt đối không lưu mật khẩu dạng Plaintext.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất BCrypt & Salt]:* Tại sao cùng một mật khẩu `"123456"`, mỗi lần gọi `bCryptPasswordEncoder.encode("123456")` lại sinh ra một chuỗi băm hoàn toàn khác nhau? Muối (Salt) được nhúng ở đâu trong chuỗi kết quả?
  2. ⚠️ *[Tấn công Rainbow Table]:* Cơ chế sinh muối ngẫu nhiên của BCrypt vô hiệu hóa hoàn toàn các bảng tra cứu mật khẩu băm sẵn (Rainbow Table) như thế nào?
  3. ⚖️ *[So sánh]:* **MD5 / SHA-256** (Các hàm băm nhanh dùng cho kiểm tra toàn vẹn file - Cực kỳ nguy hiểm cho mật khẩu vì hacker có thể thử hàng tỷ pass/giây trên GPU) vs **BCrypt / Argon2** (Hàm băm chậm có chủ đích - Adaptive Hash).
  4. 🔄 *[Độ phức tạp (Cost Factor / Strength)]:* Tham số `strength` mặc định là `10` trong BCrypt có ý nghĩa gì ($2^{10} = 1024$ vòng lặp tính toán)? Tăng lên 12 thì tốn thêm bao nhiêu CPU?
  5. 🏢 *[Thực tế]:* Viết Unit Test dùng `passwordEncoder.matches("raw_password", encodedPassword)` để kiểm tra tính chính xác của mật khẩu.
* **Từ khóa:** `BCryptPasswordEncoder`, `Salted Hash`, `Adaptive Hash Function`, `Rainbow Table Defense`, `Password Security Best Practices`.

---

### 📌 Task 40: Xây Dựng API Đăng Ký Tài Khoản (`POST /api/v1/auth/register`)
* **Hành động code:** Viết DTO `RegisterRequest`, mã hóa mật khẩu bằng `passwordEncoder.encode()` trước khi lưu vào DB, gán Role mặc định `ROLE_CUSTOMER`.
* **Mục tiêu duy nhất:** Hoàn thiện luồng đăng ký tài khoản an toàn và kiểm tra tính duy nhất của email/username.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bảo mật Lưu trữ]:* Tại sao thuộc tính `password` trong DTO Response tuyệt đối **KHÔNG ĐƯỢC PHÉP TRẢ VỀ** (kể cả chuỗi đã băm)?
  2. ⚠️ *[Race Condition Đăng ký]:* Nhắc lại Task 20: Tầng DB Unique Constraint bảo vệ hệ thống thế nào nếu người dùng click nút Đăng ký 2 lần liên tiếp?
  3. ⚖️ *[Gán Role]:* Tại sao không bao giờ cho phép Client tự truyền `role` lên trong request đăng ký (Hacker tự gửi `role: "ROLE_ADMIN"` để chiếm quyền hệ thống)?
  4. 🔄 *[Transaction]:* Tại sao hàm đăng ký cần `@Transactional` khi thao tác trên cả bảng `users` và bảng trung gian `users_roles`?
  5. 🏢 *[Thực tế]:* Gọi Postman đăng ký tài khoản mới, vào Database kiểm tra: Cột `password` hiển thị chuỗi bắt đầu bằng `$2a$10$...`.
* **Từ khóa:** `User Registration Flow`, `Privilege Escalation Prevention`, `DTO Response Sanitization`, `BCrypt Database Inspection`.

---

### 📌 Task 41: Tích Hợp Thư Viện JWT & Viết `JwtTokenProvider`
* **Hành động code:** Thêm dependency `jjwt-api`, `jjwt-impl`, `jjwt-jackson`, tạo class `JwtTokenProvider` chứa các hàm `generateToken()`, `validateToken()`, `getUsernameFromToken()`.
* **Mục tiêu duy nhất:** Nắm vững cấu trúc 3 phần của JSON Web Token (Header, Payload, Signature) và cơ chế ký bảo mật.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Cấu trúc JWT]:* 3 phần phân tách bởi dấu chấm `.` gồm những gì: **Header** (Thuật toán ký), **Payload** (Claims, thông tin User), **Signature** (Chữ ký mật mã)?
  2. ⚠️ *[Lỗ hổng Lộ Dữ liệu]:* Phần Payload của JWT chỉ được mã hóa dạng `Base64URL`, bất kỳ ai cũng có thể giải mã đọc được. Tại sao **TUYỆT ĐỐI KHÔNG ĐƯA MẬT KHẨU** hay thông tin nhạy cảm vào JWT Payload?
  3. 🔬 *[Chữ ký Số]:* Thuật toán HMAC-SHA256 sử dụng `JWT_SECRET` bí mật để ký như thế nào? Nếu hacker tự sửa `userId=1` thành `userId=2` trong Payload, tại sao chữ ký Signature lập tức bị vô hiệu hóa?
  4. ⚖️ *[Thời gian sống (Expiration)]:* Tại sao Access Token bắt buộc phải có thời gian sống ngắn (ví dụ: 15 - 30 phút)?
  5. 🏢 *[Thực tế]:* Sinh ra 1 chuỗi JWT, copy dán vào trang web `jwt.io` để phân tích trực quan cấu trúc Header, Payload và Verify Signature.
* **Từ khóa:** `JSON Web Token (JWT)`, `jjwt Library`, `HMAC-SHA256 Signature`, `Base64URL vs Encryption`, `Token Expiration Time`.

---

### 📌 Task 42: Viết API Đăng Nhập (`POST /api/v1/auth/login`)
* **Hành động code:** Xác thực thông tin đăng nhập bằng `AuthenticationManager.authenticate(...)`, nếu thành công thì sinh JWT Token trả về `AuthResponse`.
* **Mục tiêu duy nhất:** Sử dụng đúng chuẩn cơ chế xác thực của Spring Security thay vì tự viết câu truy vấn kiểm tra mật khẩu bằng tay.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Luồng Xác thực Spring Security]:* `AuthenticationManager` ủy quyền cho `DaoAuthenticationProvider` gọi `UserDetailsService.loadUserByUsername()` và kiểm tra mật khẩu bằng `PasswordEncoder` ra sao?
  2. ⚠️ *[Bẫy lỗi Bảo mật Thông tin]:* Khi người dùng nhập sai mật khẩu, thông báo lỗi trả về nên là: *"Sai mật khẩu"* hay *"Tên đăng nhập hoặc mật khẩu không chính xác"*? Tại sao (Ngăn chặn hành vi User Enumeration - rà soát tài khoản)?
  3. ⚖️ *[So sánh]:* Tự viết code query user rồi gọi `passwordEncoder.matches()` vs Dùng `AuthenticationManager`. Tại sao dùng `AuthenticationManager` chuẩn hơn (kích hoạt các sự kiện `AuthenticationSuccessEvent`, `BadCredentialsEvent`)?
  4. 🔄 *[Đánh đổi]:* Đăng nhập sinh JWT (Stateless) không cần lưu session trên server, giúp mở rộng đa server (Scale-out) cực kỳ dễ dàng.
  5. 🏢 *[Thực tế]:* Gọi API login trên Postman với tài khoản đã đăng ký ở Task 40, nhận về chuỗi JWT Token thành công.
* **Từ khóa:** `AuthenticationManager`, `UsernamePasswordAuthenticationToken`, `BadCredentialsException`, `User Enumeration Prevention`.

---

### 📌 Task 43: (MỚI) Cơ Chế Refresh Token Rotation & Thu Hồi Token (Logout Blacklist Với Redis)
* **Hành động code:** Mở rộng luồng Auth: Trả về cặp `accessToken` (15 phút) và `refreshToken` (7 ngày); viết API `POST /api/v1/auth/refresh` và API `POST /api/v1/auth/logout` lưu Access Token bị hủy vào Redis Blacklist với TTL bằng thời gian sống còn lại.
* **Mục tiêu duy nhất:** Giải quyết nhược điểm lớn nhất của JWT Stateless (không thể hủy token trước hạn) và bảo vệ an toàn khi Refresh Token bị đánh cắp bằng cơ chế Rotation.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Stateless vs Revocation]:* Tại sao JWT thuần không có hàm "Logout"? Nếu Client xóa token ở trình duyệt nhưng hacker đã kịp chụp lại token thì hacker có tiếp tục dùng được cho đến khi hết hạn không?
  2. 🔬 *[Cơ chế Redis Token Blacklist]:* Khi User bấm Đăng xuất, tại sao chỉ cần lưu `jti` (JWT ID) hoặc chuỗi token vào Redis với `EXPIRE` bằng thời gian còn lại của token (Sau thời gian đó token tự hết hạn, Redis tự động giải phóng RAM)?
  3. ⚠️ *[Cơ chế Refresh Token Rotation]:* Mỗi lần dùng Refresh Token để lấy Access Token mới, server hủy Refresh Token cũ và cấp Refresh Token mới toanh. Nếu Refresh Token cũ bị dùng lại lần 2 $\rightarrow$ Server phát hiện token bị đánh cắp và ngay lập tức hủy toàn bộ session của User đó ra sao?
  4. ⚖️ *[So sánh]:* Lưu Session trong Database vs Cặp Access Token + Refresh Token có Redis Blacklist.
  5. 🏢 *[Thực tế]:* Đăng nhập $\rightarrow$ Lấy token gọi API $\rightarrow$ Gọi API Logout $\rightarrow$ Thử dùng lại token cũ: Bị chặn ngay lập tức với mã lỗi `401 Unauthorized`.
* **Từ khóa:** `Refresh Token Rotation`, `Token Revocation Strategy`, `Redis Token Blacklist`, `JWT ID (jti)`, `Compromised Token Detection`.

---

### 📌 Task 44: (MỚI) Khóa Tài Khoản Tự Động Sau N Lần Đăng Nhập Sai (Brute-Force Defense)
* **Hành động code:** Tạo `LoginAttemptService` sử dụng Redis/Caffeine để đếm số lần đăng nhập thất bại theo Username/IP; nếu sai quá 5 lần liên tiếp, cập nhật trạng thái hoặc tạm khóa đăng nhập trong 15 phút, ném ngoại lệ `LockedException`.
* **Mục tiêu duy nhất:** Ngăn chặn triệt để tấn công dò quét vét cạn mật khẩu (Brute-Force & Credential Stuffing).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Tấn công Brute-Force]:* Hacker sử dụng botnet gửi hàng ngàn request đăng nhập mỗi giây để thử các mật khẩu phổ biến nhất làm sập hệ thống hoặc xâm nhập tài khoản thế nào?
  2. 🔬 *[Bắt sự kiện Spring Security]:* Triển khai `ApplicationListener<AuthenticationFailureBadCredentialsEvent>` và `AuthenticationSuccessEvent` để tự động tăng số đếm lỗi và reset số đếm khi đăng nhập đúng ra sao?
  3. ⚠️ *[Bẫy Khóa Vĩnh Viễn]:* Tại sao nên áp dụng khóa mềm có thời hạn (ví dụ: 15 phút) thay vì khóa cứng vĩnh viễn bắt Admin mở tay (Tránh kẻ xấu lợi dụng tính năng này cố tình gõ sai để khóa tài khoản của người khác - Denial of Service tài khoản)?
  4. ⚖️ *[Theo dõi theo IP vs Theo dõi theo Username]:* Nếu hacker dùng 10.000 IP botnet khác nhau để dò mật khẩu của đúng 1 tài khoản `admin` thì khóa theo IP hay khóa theo Username hiệu quả hơn?
  5. 🏢 *[Thực tế]:* Cố tình nhập sai mật khẩu 5 lần liên tiếp: Ở lần thứ 6, dù nhập đúng mật khẩu hệ thống vẫn báo: "Tài khoản tạm thời bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút".
* **Từ khóa:** `Account Lockout Policy`, `AuthenticationFailureBadCredentialsEvent`, `Brute-Force Defense`, `LockedException`, `Credential Stuffing Prevention`.

---

### 📌 Task 45: Viết `JwtAuthenticationFilter` (`OncePerRequestFilter`)
* **Hành động code:** Tạo class `JwtAuthenticationFilter` kế thừa `OncePerRequestFilter`, trích xuất Header `Authorization: Bearer <token>`, xác thực và nạp thông tin vào `SecurityContextHolder`.
* **Mục tiêu duy nhất:** Hiểu cơ chế chặn bắt request của Filter Chain và thiết lập danh tính người dùng cho luồng hiện tại.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `OncePerRequestFilter`]:* Tại sao nên kế thừa `OncePerRequestFilter` thay vì `Filter` thông thường (Đảm bảo filter chỉ thực thi duy nhất 1 lần cho mỗi HTTP request, kể cả khi có forward/include nội bộ)?
  2. 🔬 *[Cơ chế `SecurityContextHolder`]:* Khi nạp `new UsernamePasswordAuthenticationToken(userDetails, null, authorities)` vào `SecurityContextHolder.getContext().setAuthentication(...)`, Spring Security nhận diện người dùng ở các tầng sau (Controller, Service) như thế nào?
  3. ⚠️ *[Xử lý Token Hết hạn/Không hợp lệ]:* Nếu token hết hạn hoặc sai chữ ký, filter có nên ném Exception văng ra ngoài không hay chỉ đơn giản là không set Authentication để Filter Chain tiếp tục đi tiếp (để ExceptionTranslationFilter xử lý)?
  4. ⚖️ *[Kiểm tra Blacklist]:* Tích hợp kiểm tra token có nằm trong Redis Blacklist (từ Task 43) ngay trong hàm `doFilterInternal` như thế nào?
  5. 🏢 *[Thực tế]:* Gắn token vào Header Postman, gọi API và kiểm tra Controller có lấy được thông tin người dùng qua `@AuthenticationPrincipal UserDetails userDetails`.
* **Từ khóa:** `OncePerRequestFilter`, `SecurityContextHolder`, `UsernamePasswordAuthenticationToken`, `Bearer Token Extraction`.

---

### 📌 Task 46: Cấu Hình `SecurityFilterChain` Trong Spring Security 6
* **Hành động code:** Tạo `@Configuration @EnableWebSecurity class SecurityConfig`, cấu hình `SecurityFilterChain` với `authorizeHttpRequests`, `sessionManagement(STATELESS)`, `csrf(AbstractHttpConfigurer::disable)`, `cors(Customizer.withDefaults())`, thêm các Security Headers bảo mật (X-Content-Type-Options, HSTS, CSP).
* **Mục tiêu duy nhất:** Nắm vững cấu trúc cấu hình mới dạng Functional / Lambda DSL trong Spring Security 6 và hiểu lý do tắt/bật từng tính năng.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Cơ chế FilterChainProxy]:* `SecurityFilterChain` quản lý danh sách các Filter bảo mật theo thứ tự như thế nào? Tại sao phải dùng `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`?
  2. ⚠️ *[Tại sao tắt CSRF]:* Tại sao với kiến trúc REST API phi trạng thái (Stateless) dùng JWT lưu ở Authorization Header, ta lại an toàn để tắt `csrf().disable()`? Tấn công CSRF dựa vào cơ chế gì của Trình duyệt (Tự động gửi kèm Cookie)?
  3. 🛡️ *[Security Headers]:* Cấu hình header `X-Frame-Options: DENY` (chống Clickjacking) và `X-Content-Type-Options: nosniff` (chống MIME Sniffing) bảo vệ ứng dụng ra sao?
  4. ⚖️ *[CORS]:* Tại sao nếu không cấu hình CORS, trình duyệt từ `http://localhost:3000` (React) sẽ chặn không cho nhận phản hồi từ Spring Boot `http://localhost:8080`?
  5. 🏢 *[Thực tế]:* Cấu hình phân luồng: Mở công khai các endpoint `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**` và bắt buộc đăng nhập với toàn bộ các endpoint còn lại.
* **Từ khóa:** `Spring Security 6 SecurityFilterChain`, `Lambda DSL Configuration`, `CSRF vs Stateless JWT`, `Security Headers (CSP, HSTS)`, `CORS Configuration`.

---

### 📌 Task 47: Phân Quyền API Với `@PreAuthorize`
* **Hành động code:** Bật `@EnableMethodSecurity` trong `SecurityConfig`, gắn `@PreAuthorize("hasRole('ADMIN')")` lên các API xóa/thêm sản phẩm, và `@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")` lên API xem đơn hàng.
* **Mục tiêu duy nhất:** Kiểm soát quyền truy cập chi tiết đến từng phương thức (Method-level Security) bằng biểu thức SpEL (Spring Expression Language).
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất `@PreAuthorize`]:* Spring AOP chặn trước khi method được gọi để đánh giá biểu thức SpEL như thế nào?
  2. ⚠️ *[Bẫy lỗi Quên Bật Config]:* Nếu gắn `@PreAuthorize` mà quên annotation `@EnableMethodSecurity` ở file cấu hình, chuyện gì sẽ xảy ra (Mọi người dùng đều vào được mà không bị kiểm tra quyền)?
  3. ⚖️ *[So sánh]:* `@PreAuthorize("hasRole('ADMIN')")` vs `@Secured("ROLE_ADMIN")`. Tại sao `@PreAuthorize` mạnh hơn nhiều (hỗ trợ SpEL, kiểm tra tham số: `#id == principal.id`)?
  4. 🔄 *[Phân quyền Dữ liệu (Row-level Security)]:* Viết biểu thức SpEL: `@PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")` để người dùng chỉ được xem đơn hàng của chính mình.
  5. 🏢 *[Thực tế]:* Dùng Token của tài khoản `CUSTOMER` gọi API Xóa sản phẩm của `ADMIN` và nhận về mã lỗi `403 Forbidden`.
* **Từ khóa:** `@EnableMethodSecurity`, `@PreAuthorize`, `Spring Expression Language (SpEL)`, `Method-level Security`, `Row-level Authorization`.

---

### 📌 Task 48: Tùy Biến Lỗi 401 Và 403 Theo Chuẩn `ApiResponse`
* **Hành động code:** Tạo `CustomAuthenticationEntryPoint` (bắt 401 Unauthorized) và `CustomAccessDeniedHandler` (bắt 403 Forbidden), ghi JSON lỗi trực tiếp ra HttpServletResponse.
* **Mục tiêu duy nhất:** Đồng nhất 100% cấu trúc phản hồi lỗi của hệ thống, không để Spring Security trả về trang HTML lỗi mặc định của Tomcat.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất EntryPoint vs AccessDenied]:* Phân biệt: **401 Unauthorized** (Chưa xuất trình danh tính / Token không hợp lệ) vs **403 Forbidden** (Đã biết danh tính nhưng không đủ quyền hạn thực hiện)?
  2. ⚠️ *[Tại sao `@ExceptionHandler` không bắt được]:* Tại sao `GlobalExceptionHandler` (`@RestControllerAdvice`) bất lực không bắt được lỗi 401/403 sinh ra từ Filter Chain của Spring Security (Lỗi xảy ra trước khi request chạm tới `DispatcherServlet`)?
  3. 🔬 *[Ghi Response Trực tiếp]:* Dùng `ObjectMapper` ghi chuỗi JSON `ApiResponse.error(...)` trực tiếp vào `response.getOutputStream()` và set `response.setStatus(401)`.
  4. ⚖️ *[So sánh]:* Tự ghi response thô vs Chuyển tiếp ngoại lệ sang `HandlerExceptionResolver` để tận dụng `GlobalExceptionHandler`.
  5. 🏢 *[Thực tế]:* Gọi API với Token sai $\rightarrow$ Nhận về JSON chuẩn: `{ "code": 40101, "message": "Yêu cầu đăng nhập hoặc token đã hết hạn" }`.
* **Từ khóa:** `AuthenticationEntryPoint (401)`, `AccessDeniedHandler (403)`, `Filter Chain Exception Handling`, `Uniform ApiResponse Structure`.

---

## 🌐 GIAI ĐOẠN 7: Bảo Mật Nâng Cao, OAuth2 & Rate Limiting (Tasks 49 - 51)

### 📌 Task 49: (MỚI) Đăng Nhập Bằng Mạng Xã Hội Với OAuth 2.0 (Google Login)
* **Hành động code:** Tích hợp `spring-boot-starter-oauth2-client`, cấu hình Client ID / Secret của Google trong `application.yml`, tạo `CustomOAuth2UserService` để đón User info sau khi đăng nhập Google thành công, tự động tạo tài khoản trong DB nếu chưa có và sinh JWT nội bộ.
* **Mục tiêu duy nhất:** Nắm vững luồng Authorization Code Grant chuẩn OAuth 2.0 / OIDC và tích hợp xác thực mạng xã hội liền mạch với hệ thống JWT nội bộ.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất Luồng OAuth 2.0]:* Trình bày 4 bước của luồng **Authorization Code Grant**: (1) Trình duyệt chuyển hướng sang Google Login $\rightarrow$ (2) Người dùng đồng ý cấp quyền $\rightarrow$ (3) Google trả Authorization Code về Redirect URI $\rightarrow$ (4) Spring Boot đổi Code lấy ID Token và Access Token từ Google Server.
  2. ⚠️ *[Bảo mật]:* Tại sao Client ID có thể để lộ nhưng Client Secret bắt buộc phải giữ kín tuyệt đối trong biến môi trường `${GOOGLE_CLIENT_SECRET}`?
  3. 🔬 *[Đồng bộ với DB Nội bộ]:* Khi người dùng đăng nhập bằng Google lần đầu, hệ thống lấy `email`, `name`, `avatar` từ `OAuth2User` để tạo bản ghi `User` mới trong bảng `users` với mật khẩu ngẫu nhiên không thể đoán được ra sao?
  4. ⚖️ *[Kiến trúc Kết hợp (Hybrid Auth)]:* Sau khi xác thực với Google thành công, tại sao hệ thống lại sinh JWT của chính ứng dụng mình trả về cho Frontend thay vì bắt Frontend dùng Google Access Token cho mọi API nghiệp vụ sau đó?
  5. 🏢 *[Thực tế]:* Truy cập `http://localhost:8080/oauth2/authorization/google`, đăng nhập bằng tài khoản Gmail thật, hệ thống tự động redirect về kèm theo JWT Token hợp lệ!
* **Từ khóa:** `OAuth 2.0 Authorization Code Flow`, `spring-boot-starter-oauth2-client`, `CustomOAuth2UserService`, `Social Login to JWT Exchange`.

---

### 📌 Task 50: Chống Tấn Công DoS & Brute-Force Bằng Rate Limiting (Bucket4j)
* **Hành động code:** Tích hợp thư viện `Bucket4j`, tạo `RateLimitingFilter` giới hạn mỗi địa chỉ IP chỉ được phép gọi tối đa 10 request / phút vào API Đăng nhập.
* **Mục tiêu duy nhất:** Bảo vệ các endpoint nhạy cảm khỏi bị quá tải và chống tấn công từ chối dịch vụ.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Thuật toán Token Bucket]:* Thuật toán Token Bucket hoạt động ra sao: Xô chứa tối đa $N$ token, mỗi giây tự động nhỏ giọt thêm $K$ token. Muốn thực hiện request phải rút được 1 token trong xô?
  2. ⚠️ *[Mã lỗi HTTP Chuẩn]:* Khi người dùng gọi quá giới hạn, mã lỗi HTTP chuẩn quốc tế bắt buộc phải trả về là gì (**429 Too Many Requests**)?
  3. 🔬 *[Header Phản hồi]:* Ý nghĩa của các HTTP Response Header chuẩn: `X-Rate-Limit-Remaining` (Số lượt còn lại), `X-Rate-Limit-Retry-After-Seconds` (Thời gian phải chờ trước khi thử lại).
  4. ⚖️ *[Giới hạn Trong Bộ nhớ vs Phân tán]:* Dùng `ConcurrentHashMap` lưu Bucket trong RAM của 1 server (đơn giản nhưng không đồng bộ khi scale nhiều server) vs Dùng Redis (sẽ học ở Task 69).
  5. 🏢 *[Thực tế]:* Dùng Postman Runner bắn liên tục 15 request trong 5 giây: 10 request đầu trả về 200 OK, từ request thứ 11 trả về `429 Too Many Requests`.
* **Từ khóa:** `Rate Limiting`, `Bucket4j Library`, `Token Bucket Algorithm`, `HTTP 429 Too Many Requests`, `DoS Defense`.

---

### 📌 Task 51: (MỚI) Tối Ưu Băng Thông Với HTTP ETag & Conditional GET (304 Not Modified)
* **Hành động code:** Đăng ký Bean `ShallowEtagHeaderFilter` trong Spring Boot, cấu hình Header `Cache-Control: max-age=60, must-revalidate` cho API lấy chi tiết sản phẩm và danh mục.
* **Mục tiêu duy nhất:** Tối ưu hóa băng thông mạng và giảm tải render cho Client khi dữ liệu không có sự thay đổi thông qua cơ chế Conditional HTTP Request.
* **Bộ 5 Câu hỏi Tư duy Kỹ sư:**
  1. 🔬 *[Bản chất ETag]:* ETag (Entity Tag) là gì? `ShallowEtagHeaderFilter` tự động băm nội dung JSON phản hồi (MD5/SHA) để sinh chuỗi hash ETag (ví dụ: `ETag: "0a1b2c3d4e"`) như thế nào?
  2. 🔬 *[Cơ chế 304 Not Modified]:* Khi Client gửi lại Header `If-None-Match: "0a1b2c3d4e"`, nếu nội dung chưa đổi, tại sao Spring Boot chỉ trả về HTTP Status `304 Not Modified` với Body rỗng (Tiết kiệm 100% băng thông tải payload dữ liệu)?
  3. ⚠️ *[Shallow ETag vs Deep ETag]:* `ShallowEtagHeaderFilter` giúp tiết kiệm băng thông mạng nhưng Database vẫn phải chạy câu query để sinh dữ liệu. Làm sao dùng trường `updatedAt` / `version` của Entity để tạo Deep ETag tránh luôn cả câu query DB?
  4. ⚖️ *[So sánh]:* **Client-side Cache (HTTP ETag, Cache-Control)** vs **Server-side Cache (Redis Cache)**. Tại sao cả 2 bổ trợ cho nhau hoàn hảo trong một hệ thống lớn?
  5. 🏢 *[Thực tế]:* Gọi API GET sản phẩm lần 1 (nhận 200 OK kèm Header ETag). Gửi lại request kèm `If-None-Match` $\rightarrow$ Nhận ngay `304 Not Modified` với dung lượng phản hồi 0 KB!
* **Từ khóa:** `HTTP ETag`, `ShallowEtagHeaderFilter`, `Conditional GET (If-None-Match)`, `HTTP 304 Not Modified`, `Bandwidth Optimization`.
