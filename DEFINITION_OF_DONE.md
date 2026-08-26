# 🎯 TIÊU CHUẨN NGHIỆM THU KỸ SƯ (DEFINITION OF DONE - DoD)

> Trong các công ty công nghệ chuyên nghiệp, một tính năng chỉ được xem là **"Hoàn Thành (Done)"** khi thỏa mãn đầy đủ các tiêu chuẩn kiểm soát chất lượng (Quality Gates).
> Không có chuyện *"code chạy được trên máy tôi là xong"*. 

Dưới đây là **Checklist 4 Tiêu Chuẩn** bạn bắt buộc phải tự kiểm tra trước khi đánh dấu hoàn thành bất kỳ Task nào trong lộ trình:

---

## 📋 BẢNG CHECKLIST NGHIỆM THU

### 1. 🧠 Hiểu Sâu & Thấu Đáo Tư Duy 5D (Mindset Verification)
- [ ] **Đã tự trả lời và giải thích được 5 câu hỏi tư duy** của Task mà không cần mở tài liệu chép lại:
  - 🔬 *Bản chất:* Hiểu bên dưới JVM / Spring Framework / PostgreSQL đang thực sự chạy cái gì.
  - ⚠️ *Rủi ro:* Biết hệ thống sẽ gãy ở đâu nếu code này gặp tải cao hoặc dữ liệu dị dạng (Edge Cases).
  - ⚖️ *So sánh:* Nêu được ít nhất 1 giải pháp thay thế và lý do chọn cách hiện tại.
  - 🔄 *Đánh đổi:* Biết rõ cách làm này được gì và phải trả giá bằng cái gì (RAM, CPU, I/O, thời gian bảo trì).
  - 🏢 *Thực tế:* Hiểu cách các dự án lớn ngoài đời áp dụng quy chuẩn này.

---

### 2. 🧹 Tiêu Chuẩn Code Sạch (Clean Code & Best Practices)
- [ ] **Không thừa / rác:** Xóa sạch các import không dùng (`Optimize Imports`), xóa các đoạn code bị comment vô nghĩa (`// todo`, `// dead code`).
- [ ] **Không `System.out.println`:** Toàn bộ log phải dùng qua SLF4J Logger (`log.info()`, `log.error()`, `log.warn()`).
- [ ] **Không Hardcode (Magic Numbers / Magic Strings):**
  - Các hằng số (mã lỗi, chuỗi role, regex, thời gian expire) phải đưa vào Enum hoặc Constant (`ErrorCode`, `AppConstants`).
- [ ] **Đặt tên chuẩn Java & RESTful:**
  - Tên biến/hàm: `camelCase` mang ý nghĩa hành động (ví dụ: `findActiveProductsByCategoryId`).
  - Tên class: `PascalCase` rõ vai trò (`ProductServiceImpl`, `CategoryResponse`).
  - URI API: danh từ số nhiều, chữ thường, nối bằng gạch ngang (ví dụ: `/api/order-items`).
- [ ] **Đóng gói dữ liệu (Encapsulation):**
  - Không trả trực tiếp Entity ra ngoài Controller.
  - Mọi Request/Response đều đi qua DTO và Mapper.

---

### 3. ⚙️ Biên Dịch & Kiểm Tra Tĩnh (Build & Static Analysis)
- [ ] **Biên dịch sạch:** Chạy lệnh build Maven không phát sinh bất kỳ lỗi cú pháp hay cảnh báo nghiêm trọng nào:
  ```bash
  JAVA_HOME=/Users/kyanh/Library/Java/JavaVirtualMachines/ms-21.0.12.1/Contents/Home ./mvnw clean compile -DskipTests
  # Kết quả bắt buộc: [INFO] BUILD SUCCESS
  ```
- [ ] **Không có cảnh báo IDE:** Không có gạch chân đỏ (Compile Error) hoặc gạch chân vàng cảnh báo nghiêm trọng trong file code vừa tạo/chỉnh sửa.

---

### 4. 🧪 Kiểm Thử Thực Tế (Runtime & Verification)
- [ ] **Test luồng thành công (Happy Case):**
  - Gọi API qua Swagger UI (`/swagger-ui.html`) hoặc Postman với dữ liệu chuẩn $\rightarrow$ Trả về đúng mã HTTP (`200 OK`, `201 Created`) và body JSON theo chuẩn `ApiResponse`.
- [ ] **Test luồng dữ liệu lỗi (Edge / Validation Case):**
  - Cố tình gửi thiếu dữ liệu, sai định dạng email/phone, id không tồn tại $\rightarrow$ `GlobalExceptionHandler` bắt chính xác và trả về mã lỗi (`400 Bad Request`, `404 Not Found`, `409 Conflict`) kèm thông điệp rõ ràng, không làm văng Exception 500 kèm StackTrace.
- [ ] **Kiểm tra log câu lệnh SQL Hibernate:**
  - Quan sát Terminal: Đảm bảo số lượng câu query SQL sinh ra đúng như kỳ vọng (không phát sinh N+1 câu query vô lý).

---

## 🏆 NGUYÊN TẮC VÀNG
> *"Viết code như thể người bảo trì dự án sau này là một kẻ bạo lực biết rõ địa chỉ nhà của bạn."*  
> Hãy giữ cho từng commit, từng dòng code của bạn luôn đạt tiêu chuẩn cao nhất!
