# 🛠 CẨM NANG TỰ DEBUG & BẮT BỆNH SPRING BOOT (TROUBLESHOOTING GUIDE)

> Khi gặp lỗi (Bug), lập trình viên kém sẽ ngay lập tức hoảng loạn hoặc copy cả đoạn lỗi dài dán cho AI.  
> **Kỹ sư giỏi sẽ xem lỗi là manh mối (clue), bình tĩnh đọc log và cô lập vùng nghi ngờ trong 60 giây.**

---

## 🧭 BÍ KÍP 1: NGHỆ THUẬT ĐỌC STACKTRACE (TRONG 10 GIÂY)

Khi Terminal bắn ra 100 dòng chữ đỏ (StackTrace), **đừng đọc từ đầu đến cuối!**

```
java.lang.RuntimeException: Request processing failed
    at org.apache.catalina.core... (bỏ qua các dòng của framework)
    at org.springframework.web... (bỏ qua)
    ... 50 lines omitted
Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement
    at org.hibernate.exception.internal... (bỏ qua)
    ... 30 lines omitted
Caused by: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "categories_name_key"
  Detail: Key (name)=(Điện thoại) already exists.  <-- ĐÂY LÀ GỐC RỄ VẤN ĐỀ!
    at com.example.learn_spring.service.impl.CategoryServiceImpl.createCategory(CategoryServiceImpl.java:42) <-- DÒNG CODE BỊ LỖI!
```

### 🎯 3 Bước Đọc StackTrace Chuẩn:
1. **Bước 1: Cuộn thẳng xuống dưới cùng.** Tìm dòng chữ `Caused by:` cuối cùng. Đây chính là **nguyên nhân gốc rễ (Root Cause)** của lỗi.
2. **Bước 2: Tìm dòng code của dự án mình.** Lướt tìm các dòng có chứa package `com.example.learn_spring...` (bỏ qua toàn bộ các dòng `org.springframework...`, `org.hibernate...`, `org.apache.catalina...`).
3. **Bước 3: Bấm vào liên kết file và số dòng** (ví dụ: `CategoryServiceImpl.java:42`) để IDE nhảy thẳng tới đúng dòng code gây ra lỗi.

---

## 🔍 BÍ KÍP 2: BẬT LOG SOI TẬN GỐC CÂU LỆNH SQL CỦA HIBERNATE

Mặc định Hibernate chỉ hiện câu SQL với các dấu hỏi chấm (`WHERE id = ? AND is_deleted = ?`), bạn không biết Hibernate truyền giá trị gì vào.

### Cấu hình trong `src/main/resources/application.properties`:
```properties
# 1. Hiển thị câu lệnh SQL định dạng đẹp mắt
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 2. BẬT LOG SOI THAM SỐ THỰC TẾ TRUYỀN VÀO DẤU '?' (Hibernate 6)
logging.level.org.hibernate.orm.jdbc.bind=trace

# 3. Xem loại Type và Transaction
logging.level.org.hibernate.type.descriptor.sql=trace
logging.level.org.springframework.transaction=debug
```

### Kết quả bạn sẽ nhìn thấy trong Terminal:
```
Hibernate: 
    SELECT p.id, p.name, p.price 
    FROM products p 
    WHERE p.category_id = ? AND p.is_deleted = ?
binding parameter (1:BIGINT) <- [1]          <-- Tham số category_id = 1
binding parameter (2:BOOLEAN) <- [false]     <-- Tham số is_deleted = false
```

---

## 🐞 BÍ KÍP 3: KỸ THUẬT DEBUG BẰNG BREAKPOINT (TRONG INTELLIJ / VS CODE)

Thay vì mất thời gian thêm `System.out.println` rồi phải khởi động lại server, hãy dùng **Breakpoint (Điểm dừng)**:

1. **Đặt Breakpoint:** Nhấp chuột vào lề trái (bên cạnh số dòng) của dòng code nghi ngờ trong Controller hoặc Service (hiện chấm đỏ 🔴).
2. **Khởi động ứng dụng ở chế độ Debug:** Bấm vào biểu tượng **Con Bọ (Debug App 🪲)** thay vì nút Run tam giác.
3. **Gửi Request từ Swagger/Postman:** Khi luồng chạy tới dòng có Breakpoint, IDE sẽ dừng chương trình lại ngay lập tức.
4. **4 Phím Tắt Sống Còn Khi Debug:**
   - **Step Over (`F8` / `F10`):** Chạy tiếp sang dòng code tiếp theo trong cùng hàm.
   - **Step Into (`F7` / `F11`):** Nhảy sâu vào bên trong phương thức đang gọi (ví dụ nhảy từ Controller vào Service).
   - **Evaluate Expression (`Alt + F8` / `Cmd + F8`):** Mở cửa sổ tính toán, cho phép bạn gõ thử bất kỳ biểu thức Java nào để xem kết quả ngay lúc runtime mà không cần sửa code.
   - **Resume Program (`F9`):** Cho chương trình chạy tiếp bình thường đến Breakpoint tiếp theo (hoặc chạy xong).

---

## ⚠️ TOP 8 LỖI KINH ĐIỂN TRONG SPRING BOOT & CÁCH SỬA TRONG 30 GIÂY

### 1. `LazyInitializationException: could not initialize proxy - no Session`
* **Nguyên nhân:** Cố gắng truy cập vào một thuộc tính `FetchType.LAZY` (ví dụ `order.getCustomer().getName()`) khi Transaction / Session của Hibernate đã bị đóng lại ngoài tầng Controller.
* **Cách sửa:**
  - Cách 1: Thêm `@Transactional` ở tầng Service để giữ Session mở trong suốt quá trình mapper.
  - Cách 2: Dùng `JOIN FETCH` trong câu query Repository để load dữ liệu cần thiết trước.

---

### 2. `TransientPropertyValueException: object references an unsaved transient instance`
* **Nguyên nhân:** Cố gắng lưu Entity con (ví dụ `OrderItem`) đang trỏ tới một Entity cha (ví dụ `Order`) mà Entity cha này chưa từng được lưu vào Database (`save()`).
* **Cách sửa:** Lưu Entity cha trước (`orderRepository.save(order)`) rồi mới gán vào Entity con, hoặc thêm `cascade = CascadeType.ALL` vào quan hệ.

---

### 3. `DataIntegrityViolationException: could not execute statement; constraint [...]`
* **Nguyên nhân:** Vi phạm ràng buộc ở Database: Trùng khóa chính/khóa duy nhất (Unique), hoặc khóa ngoại (Foreign Key) trỏ tới một `id` không hề tồn tại trong bảng cha.
* **Cách sửa:** Kiểm tra dữ liệu xem có bị trùng `email`, `name`, hoặc `categoryId` gửi lên có thực sự tồn tại trong DB không.

---

### 4. `NullPointerException (NPE) at ...`
* **Nguyên nhân:** Bạn đang gọi phương thức trên một biến có giá trị là `null` (ví dụ: `request.getName().trim()` khi `getName()` trả về `null`).
* **Cách sửa:** Đặt Breakpoint kiểm tra biến nào bị `null`. Thêm điều kiện kiểm tra `if (obj != null)` hoặc dùng `Optional.ofNullable()`.

---

### 5. `MethodArgumentNotValidException`
* **Nguyên nhân:** Dữ liệu Client gửi lên vi phạm các annotation validation trong Request DTO (`@NotBlank`, `@Min`, `@Size`...).
* **Cách sửa:** Đọc log JSON trả về từ `GlobalExceptionHandler` để biết chính xác trường nào bị sai và bổ sung dữ liệu hợp lệ trong Body request.

---

### 6. `NoResourceFoundException: No static resource api/products`
* **Nguyên nhân:** Gõ sai đường dẫn URL (ví dụ thiếu `/` ở đầu: `api/products` thay vì `/api/products`), hoặc sai HTTP Method (gọi `GET` vào một endpoint chỉ khai báo `@PostMapping`).
* **Cách sửa:** Kiểm tra lại URL và Method trong Controller và Swagger UI.

---

### 7. `Circular Dependency Exception: The dependencies of some of the beans in the application context form a cycle`
* **Nguyên nhân:** Service A tiêm (Inject) Service B, đồng thời Service B lại tiêm Service A $\rightarrow$ Spring không biết phải khởi tạo class nào trước.
* **Cách sửa:** Tách phần logic chung ra một Service C thứ ba, hoặc dùng `@Lazy` tại constructor.

---

### 8. `Port 8080 was already in use`
* **Nguyên nhân:** Cổng 8080 đang bị một ứng dụng Spring Boot khác (hoặc tiến trình ngầm) chiếm dụng.
* **Cách sửa trên macOS/Linux:**
  ```bash
  # Tìm tiến trình đang chiếm cổng 8080
  lsof -i :8080
  # Diệt tiến trình (thay PID bằng số tìm được)
  kill -9 <PID>
  ```

---

## 🎯 CHIẾN LƯỢC TỰ CỨU (4 BƯỚC DIVIDE & CONQUER)
1. **Cô lập lỗi:** Lỗi nằm ở đâu? (Tầng Controller tiếp nhận sai $\rightarrow$ Tầng Service xử lý logic sai $\rightarrow$ Hay Tầng Repository / Database query sai?).
2. **Tái hiện tối giản (Minimal Reproducible Example):** Thử gọi API đó bằng Swagger với dữ liệu đơn giản nhất có thể.
3. **Đặt Breakpoint:** Lần theo từng bước nhảy của dữ liệu qua các tầng.
4. **Xác minh giả thuyết:** *"Tôi nghi ngờ biến `category` bị null"* $\rightarrow$ Đặt Breakpoint soi biến `category` xem giả thuyết có đúng không.
