# 🚀 Lộ Trình 29 Bài Tập TypeScript & ES6+ Cốt Lõi (Thực Chiến React & Express)

> **Mục tiêu:** Tối ưu hóa 100% thời gian học, **cắt bỏ toàn bộ Type Gymnastics hàn lâm**, chỉ tập trung vào những kỹ thuật bạn sẽ viết và đọc **hằng ngày** trong các dự án **React (Next.js)** và **Backend Express / NestJS**.

---

## 🧭 Cấu Trúc 4 Chặng Luyện Tập (29 Bài Cốt Lõi)

| Chặng | Cấp độ | Số bài | Ứng dụng thực tế trong React & Express |
| :--- | :--- | :---: | :--- |
| **Chặng 1** | 🟢 Nền tảng Types & Cú pháp | Bài 01 - 10 | Props, State cơ bản, Union literal, Interface DTO, xử lý `null`/`undefined` |
| **Chặng 2** | 🟡 Thu hẹp kiểu & Generics | Bài 11 - 16 | React Async State (`idle`/`loading`), Custom Type Guard validate `req.body`, Generic Fetcher & Repo |
| **Chặng 3** | 🟠 Utility Types & Form State | Bài 17 - 22 | DTO (Pick/Omit), Form State typing, `Prettify`, trích xuất kiểu Hook/API với `infer` |
| **Chặng 4** | 🔴 Advanced & ES6+ Runtime | Bài 23 - 29 | Safe `try/catch` unknown error, Typed Event Emitter, In-Memory `Map`, Batch Promises, Template Literals |

---

## 🟢 CHẶNG 1: NỀN TẢNG TYPES & CÚ PHÁP CỐT LÕI
*Mục tiêu: Nắm vững hệ thống kiểu cơ bản để định nghĩa Props, State và Interface.*

---

### Bài 01: Định kiểu biến nguyên thủy & Return type
- **Ứng dụng thực tế:** Định kiểu cho helper functions và handlers trong React/Express.
- **Yêu cầu:** Viết hàm `formatGreeting(name: string, age: number, isVip: boolean): string`. Nếu `isVip === true` trả về `"[VIP] Xin chào Alice (25 tuổi)"`, ngược lại trả về `"Xin chào Alice (25 tuổi)"`.
- **Keywords:** `typescript primitive types`, `typescript function return type annotation`
- **Input / Output Mẫu:**
```typescript
formatGreeting("Alice", 25, true);  // "[VIP] Xin chào Alice (25 tuổi)"
formatGreeting("Bob", 30, false);   // "Xin chào Bob (30 tuổi)"
```
- **Starter Code:**
```typescript
function formatGreeting(name: string, age: number, isVip: boolean): string {
  // your code here
}
```

---

### Bài 02: Array & Readonly Array (Bất biến State)
- **Ứng dụng thực tế:** Đảm bảo React State hoặc Config không bị mutate ngoài ý muốn.
- **Yêu cầu:** Tạo type `ScoreList = readonly number[]`. Viết hàm `calculateAverage(scores: ScoreList): number` tính điểm trung bình (mảng rỗng trả về 0). Đảm bảo `scores.push()` bị báo lỗi compile.
- **Keywords:** `typescript readonly array`, `immutable state react`
- **Input / Output Mẫu:**
```typescript
const myScores: ScoreList = [8, 9, 10, 7];
calculateAverage(myScores); // 8.5
calculateAverage([]);       // 0

// @ts-expect-error - Không được phép sửa mảng readonly
myScores.push(5);
```

---

### Bài 03: Tuples & Destructuring (Mô hình Custom Hook)
- **Ứng dụng thực tế:** Hiểu cách React định kiểu cho `useState` dạng `[state, setState]`.
- **Yêu cầu:** Định nghĩa kiểu `GeoLocation = [latitude: number, longitude: number, placeName?: string]`. Viết hàm `printLocation(loc: GeoLocation): string`.
- **Keywords:** `typescript tuple types`, `custom hook return tuple`
- **Input / Output Mẫu:**
```typescript
const loc1: GeoLocation = [10.762622, 106.660172, "TP. Hồ Chí Minh"];
const loc2: GeoLocation = [21.028511, 105.804817];

printLocation(loc1); // "TP. Hồ Chí Minh (10.762622, 106.660172)"
printLocation(loc2); // "Tọa độ: (21.028511, 105.804817)"
```

---

### Bài 04: Interface & Optional Properties (Component Props)
- **Ứng dụng thực tế:** Định nghĩa Props cho Component React và User Entity trong Express.
- **Yêu cầu:** Tạo interface `UserProfile` gồm: `id` (readonly number), `username` (string), `email` (string), `bio` (optional string). Viết hàm `getUserSummary(user: UserProfile): string`.
- **Keywords:** `typescript interface`, `optional properties ?`, `readonly modifier`
- **Input / Output Mẫu:**
```typescript
const u1: UserProfile = { id: 1, username: "kyanh", email: "kyanh@test.com" };
const u2: UserProfile = { id: 2, username: "alex", email: "alex@test.com", bio: "Fullstack Dev" };

getUserSummary(u1); // "kyanh (kyanh@test.com) - Chưa có bio"
getUserSummary(u2); // "alex (alex@test.com) - Fullstack Dev"
```

---

### Bài 05: Union Types & Thu hẹp kiểu với `typeof`
- **Ứng dụng thực tế:** Xử lý linh hoạt Props nhận chuỗi hoặc số (như `width="100%"` hoặc `width={100}`).
- **Yêu cầu:** Viết hàm `formatPrice(price: string | number): string`. Nếu là `number`, format `$100.00`; nếu là `string`, trim và trả về `$ + chuỗi`.
- **Keywords:** `typescript union types`, `typescript type narrowing typeof`
- **Input / Output Mẫu:**
```typescript
formatPrice(49.5);      // "$49.50"
formatPrice("  99.9 "); // "$99.9"
```

---

### Bài 06: Literal Types & Status Enums
- **Ứng dụng thực tế:** Quản lý method API (`GET`/`POST`) và mã phản hồi HTTP trong Express.
- **Yêu cầu:** Tạo `type HttpMethod = "GET" | "POST" | "PUT" | "DELETE"` và `type HttpStatus = 200 | 201 | 400 | 401 | 404 | 500`. Viết hàm `isSuccessResponse(status: HttpStatus): boolean` (trả về true nếu là 200 hoặc 201).
- **Keywords:** `typescript literal types`, `type alias union`
- **Input / Output Mẫu:**
```typescript
isSuccessResponse(200); // true
isSuccessResponse(404); // false

// @ts-expect-error - 302 không nằm trong HttpStatus
isSuccessResponse(302);
```

---

### Bài 07: Numeric & String Enums
- **Ứng dụng thực tế:** Quản lý trạng thái đơn hàng / vai trò người dùng trong Database.
- **Yêu cầu:** Tạo enum `OrderState { PENDING = "PENDING", SHIPPING = "SHIPPING", DELIVERED = "DELIVERED", CANCELLED = "CANCELLED" }`. Viết hàm `canCancelOrder(state: OrderState): boolean` (chỉ cho hủy khi ở trạng thái `PENDING`).
- **Keywords:** `typescript enum`, `string enum vs union`
- **Input / Output Mẫu:**
```typescript
canCancelOrder(OrderState.PENDING);    // true
canCancelOrder(OrderState.SHIPPING);   // false
```

---

### Bài 08: `unknown` vs `any` (Nhận Dữ Liệu An Toàn)
- **Ứng dụng thực tế:** Xử lý dữ liệu chưa biết trước từ `req.body` (Express) hoặc `response.json()` (React).
- **Yêu cầu:** Viết hàm `inspectValue(value: unknown): string`. Dùng `typeof` / `Array.isArray` để in ra: `"Số: ..."` nếu là number, `"Chuỗi dài ... ký tự"` nếu là string, `"Mảng có ... phần tử"` nếu là array, ngược lại trả về `"Khác"`.
- **Keywords:** `typescript unknown vs any`, `type safe input validation`
- **Input / Output Mẫu:**
```typescript
inspectValue(42);        // "Số: 42"
inspectValue("hello");   // "Chuỗi dài 5 ký tự"
inspectValue([1, 2, 3]); // "Mảng có 3 phần tử"
```

---

### Bài 09: Nullish Coalescing (`??`) & Optional Chaining (`?.`)
- **Ứng dụng thực tế:** Đọc dữ liệu lồng nhau an toàn từ API mà không bị crash `Cannot read properties of undefined`.
- **Yêu cầu:** Cho `type AppConfig = { server?: { host?: string; port?: number } }`. Viết hàm `getServerUrl(config?: AppConfig): string` trả về `"http://[host]:[port]"`, mặc định host `"localhost"` và port `3000`.
- **Keywords:** `nullish coalescing ??`, `optional chaining ?.`, `fallback default config`
- **Input / Output Mẫu:**
```typescript
getServerUrl({ server: { host: "127.0.0.1", port: 8080 } }); // "http://127.0.0.1:8080"
getServerUrl({});                                            // "http://localhost:3000"
getServerUrl(undefined);                                     // "http://localhost:3000"
```

---

### Bài 10: `type` vs `interface` (Kế thừa DTO Thực Tế)
- **Ứng dụng thực tế:** Mở rộng User Schema cơ bản thành Admin Schema trong Express.
- **Yêu cầu:**
  1. Tạo `interface BaseUser { id: number; name: string; email: string }` và `interface AdminUser extends BaseUser { permissions: string[] }`.
  2. Tạo `type BaseProduct = { id: number; title: string; price: number }` và `type DiscountedProduct = BaseProduct & { discountPercent: number }`.
- **Keywords:** `interface extends vs intersection type &`, `dto inheritance`
- **Input / Output Mẫu:**
```typescript
const admin: AdminUser = { id: 1, name: "Admin", email: "admin@app.com", permissions: ["ALL"] };
const product: DiscountedProduct = { id: 10, title: "Laptop", price: 1000, discountPercent: 15 };
```

---

## 🟡 CHẶNG 2: THU HẸP KIỂU & GENERICS CHO SERVICE / REPO
*Mục tiêu: Làm chủ các pattern quản lý State bất đồng bộ và viết Service/Repository layer chuẩn mực.*

---

### Bài 11: Discriminated Unions (Pattern Async State React / Redux)
- **Ứng dụng thực tế:** Mô hình hóa đầy đủ 4 trạng thái của API trong React Hook (`idle` khi mới load, `loading` khi gọi API, `success` khi có data, `error` khi thất bại).
- **Yêu cầu:** Tạo type `AsyncState<T>` gồm 4 trạng thái:
  - `{ status: "idle" }`
  - `{ status: "loading" }`
  - `{ status: "success", data: T }`
  - `{ status: "error", error: string }`
  Viết hàm `renderState<T>(state: AsyncState<T>): string`.
- **Keywords:** `discriminated unions react state`, `tagged union pattern`
- **Input / Output Mẫu:**
```typescript
renderState({ status: "idle" });                      // "Chưa có dữ liệu"
renderState({ status: "loading" });                   // "Đang tải dữ liệu..."
renderState({ status: "success", data: "User List" }); // "Thành công: User List"
renderState({ status: "error", error: "404 Error" });  // "Lỗi: 404 Error"
```

---

### Bài 12: Custom Type Guard (Validate `req.body` trong Express)
- **Ứng dụng thực tế:** Viết middleware kiểm tra body request có đúng cấu trúc mong muốn hay không trước khi xử lý controller.
- **Yêu cầu:** Viết hàm `isCreateUserDto(obj: unknown): obj is { username: string; email: string; age?: number }`.
- **Keywords:** `typescript user-defined type guards`, `type predicate is`, `express body validation`
- **Input / Output Mẫu:**
```typescript
isCreateUserDto({ username: "alice", email: "alice@test.com" }); // true
isCreateUserDto({ username: "alice" });                          // false (thiếu email)
isCreateUserDto(null);                                           // false
```

---

### Bài 13: Generic Function Thực Chiến (`fetchApi<T>` Async Client)
> *Đã nâng cấp: Kết hợp Generics và Async/Await thực tế thay vì wrapper đơn giản.*
- **Ứng dụng thực tế:** Viết hàm gọi API dùng chung (tương tự `axios.get<T>` hay `fetch`), tự động parse JSON và trả về đúng kiểu dữ liệu Generic `T`.
- **Yêu cầu:** Viết hàm `fetchApi<T>(url: string): Promise<{ data: T; status: number }>`.
- **Keywords:** `typescript generic async function`, `typed api fetcher client`
- **Input / Output Mẫu:**
```typescript
interface Product { id: number; name: string; price: number }

// Giả lập gọi API:
const response = await fetchApi<Product>("/api/products/1");
console.log(response.data.name);  // Type suy luận chuẩn xác là string
console.log(response.data.price); // Type suy luận chuẩn xác là number
```

---

### Bài 14: Generic Interface & Repository Pattern (Backend Service Layer)
- **Ứng dụng thực tế:** Xây dựng Interface chuẩn cho tầng Database Repository (TypeORM, Prisma, Mongoose).
- **Yêu cầu:** Tạo `interface CrudRepository<T, ID>` có các phương thức: `findById(id: ID): Promise<T | null>`, `save(entity: T): Promise<T>`, `deleteById(id: ID): Promise<boolean>`.
- **Keywords:** `typescript generic interface`, `repository pattern backend`
- **Input / Output Mẫu:**
```typescript
interface User { id: number; name: string }

class InMemoryUserRepo implements CrudRepository<User, number> {
  // your implementation
}
```

---

### Bài 15: `keyof` & Indexed Access Types (Truy cập thuộc tính an toàn)
- **Ứng dụng thực tế:** Viết hàm sắp xếp mảng theo key bất kỳ (`sortBy(users, "name")`) có autocomplete chuẩn xác.
- **Yêu cầu:** Tạo hàm `getProperty<T, K extends keyof T>(obj: T, key: K): T[K]`.
- **Keywords:** `typescript keyof operator`, `indexed access types T[K]`
- **Input / Output Mẫu:**
```typescript
const user = { id: 1, name: "Alice", isVip: true };
const val1 = getProperty(user, "name");  // Type: string, Value: "Alice"
const val2 = getProperty(user, "isVip"); // Type: boolean, Value: true

// @ts-expect-error - "salary" không có trong user
getProperty(user, "salary");
```

---

### Bài 16: Exhaustiveness Checking với `never` (Bảo vệ Reducer / Action)
- **Ứng dụng thực tế:** Đảm bảo khi thêm một Action mới vào Redux reducer, TypeScript sẽ bắt buộc phải viết case xử lý, không bao giờ bỏ sót.
- **Yêu cầu:** Cho `type Action = { type: "LOGIN"; user: string } | { type: "LOGOUT" } | { type: "UPDATE_THEME"; theme: "dark" | "light" }`. Viết hàm `appReducer(action: Action): string` có default case dùng `assertNever(x: never): never`.
- **Keywords:** `exhaustiveness checking never`, `redux reducer exhaustiveness`
- **Input / Output Mẫu:**
```typescript
function assertNever(x: never): never {
  throw new Error(`Unexpected action: ${JSON.stringify(x)}`);
}
```

---

## 🟠 CHẶNG 3: UTILITY TYPES & FORM STATE THỰC CHIẾN
*Mục tiêu: Tự tin xử lý DTO, form validation state và trích xuất kiểu dữ liệu từ Hook/Promise.*

---

### Bài 17: `MyPartial<T>` & `MyReadonly<T>` (Patch API & Immutable State)
- **Ứng dụng thực tế:** Định kiểu cho API Update Partial (`PATCH /users/:id` chỉ gửi lên các field cần sửa).
- **Yêu cầu:**
  1. Viết `type MyPartial<T> = { [K in keyof T]?: T[K] }`.
  2. Viết `type MyReadonly<T> = { readonly [K in keyof T]: T[K] }`.
- **Keywords:** `typescript mapped types`, `partial update dto`
- **Input / Output Mẫu (Type Check):**
```typescript
interface UserProfile { name: string; age: number; email: string }

type UpdateUserDto = MyPartial<UserProfile>;
// { name?: string; age?: number; email?: string }
```

---

### Bài 18: `MyPick<T, K>` & `MyOmit<T, K>` (Tạo DTO & Filter Props)
- **Ứng dụng thực tế:** Tạo `CreateUserDto` loại bỏ trường `id` tự sinh, hoặc lọc bỏ các props nhạy cảm như `passwordHash`.
- **💡 Gợi ý tư duy (Hint):**
  - `MyPick<T, K extends keyof T> = { [P in K]: T[P] }`.
  - `MyExclude<T, U> = T extends U ? never : T` (Distributive Conditional Type).
  - `MyOmit<T, K> = MyPick<T, MyExclude<keyof T, K>>`.
- **Keywords:** `pick and omit implementation`, `dto data transfer object`
- **Input / Output Mẫu (Type Check):**
```typescript
interface UserEntity { id: number; name: string; email: string; passwordHash: string }

type CreateUserDto = MyOmit<UserEntity, "id">;
// { name: string; email: string; passwordHash: string }

type UserCardProps = MyPick<UserEntity, "id" | "name">;
// { id: number; name: string }
```

---

### Bài 19: Form State Typing (`FormErrors<T>` & `FormTouched<T>`)
- **Ứng dụng thực tế:** Xây dựng Hook quản lý form validation (tương tự React Hook Form / Formik).
- **Yêu cầu:** Viết `type FormErrors<T> = { [K in keyof T]?: string }` và `type FormTouched<T> = { [K in keyof T]?: boolean }`.
- **Keywords:** `react form validation typing`, `mapped types practical example`
- **Input / Output Mẫu (Type Check):**
```typescript
interface LoginForm { email: string; password: string }

type LoginErrors = FormErrors<LoginForm>;     // { email?: string; password?: string }
type LoginTouched = FormTouched<LoginForm>;   // { email?: boolean; password?: boolean }
```

---

### Bài 20: Utility Kinh Điển: `Prettify<T>` (Debug Type trong VSCode)
- **Ứng dụng thực tế:** Khi gộp nhiều Type Intersection (`Props & HTMLAttributes`), VSCode hover rất rối. `Prettify` giúp hiển thị một Object phẳng sạch sẽ.
- **Yêu cầu:** Viết `type Prettify<T> = { [K in keyof T]: T[K] } & {}`.
- **Keywords:** `typescript prettify helper`, `flatten intersection type vscode`
- **Input / Output Mẫu (Type Check):**
```typescript
type DirtyType = { id: string } & { title: string } & { isCompleted: boolean };

type CleanType = Prettify<DirtyType>;
// Khi hover vào CleanType trong VSCode: { id: string; title: string; isCompleted: boolean }
```

---

### Bài 21: Từ khóa `infer`: Tự viết lại `MyReturnType<T>`
- **Ứng dụng thực tế:** Lấy kiểu dữ liệu trả về của một Custom Hook hoặc hàm utility mà thư viện không export sẵn type.
- **💡 Gợi ý tư duy (Hint):**
  - Cú pháp: `type MyReturnType<T> = T extends (...args: any[]) => infer R ? R : never`.
- **Keywords:** `typescript infer keyword`, `extract hook return type`
- **Input / Output Mẫu (Type Check):**
```typescript
function useAuth() {
  return { user: { id: "1", name: "Alice" }, token: "jwt_token_123" };
}

type AuthState = MyReturnType<typeof useAuth>;
// { user: { id: string; name: string }; token: string }
```

---

### Bài 22: `PromiseValue<T>` (Unwrap Async API Response)
- **Ứng dụng thực tế:** Trích xuất kiểu dữ liệu thực tế bên trong `Promise<T>` khi gọi hàm async.
- **Yêu cầu:** Viết `type PromiseValue<T> = T extends Promise<infer V> ? V : T`.
- **Keywords:** `typescript unwrap promise infer`, `async return type`
- **Input / Output Mẫu (Type Check):**
```typescript
async function fetchUserById(id: number) {
  return { id, name: "Alice", role: "admin" };
}

type UserResponse = PromiseValue<ReturnType<typeof fetchUserById>>;
// { id: number; name: string; role: string }
```

---

## 🔴 CHẶNG 4: ADVANCED PATTERNS & ES6+ CHO REACT & EXPRESS
*Mục tiêu: Kết hợp Type Safety với các tính năng runtime ES6+ quan trọng nhất trong phát triển ứng dụng.*

---

### Bài 23: Safe `try/catch` & Unknown Error Handling
> *Đã bổ sung: Giải quyết khoảng trống lớn nhất khi xử lý lỗi trong Express & React.*
- **Ứng dụng thực tế:** Trong TypeScript (chế độ strict), biến lỗi trong `catch (err: unknown)` luôn có kiểu `unknown`. Viết hàm tiện ích trích xuất message lỗi an toàn.
- **Yêu cầu:**
  1. Viết hàm `getErrorMessage(error: unknown): string`. Nếu `error instanceof Error` trả về `error.message`, nếu là string trả về chính nó, ngược lại trả về `"Unknown error"`.
  2. Viết hàm wrapper `safeAsync<T>(fn: () => Promise<T>): Promise<{ data: T; error: null } | { data: null; error: string }>`.
- **Keywords:** `typescript try catch unknown error`, `safe async wrapper typescript`
- **Input / Output Mẫu:**
```typescript
// Sử dụng trong Express Controller / React Handler:
const result = await safeAsync(() => fetchUserData("123"));

if (result.error) {
  console.log("Lỗi:", result.error); // error là string an toàn
} else {
  console.log("Data:", result.data);  // data được định kiểu chuẩn xác
}
```

---

### Bài 24: Template Literal Types (Action Types & Event Names)
- **Ứng dụng thực tế:** Tự động sinh tên action Redux (ví dụ: `"SET_USER" | "RESET_USER"`) hoặc CSS Class.
- **Yêu cầu:** Cho `type Action = "SET" | "RESET"` và `type Entity = "USER" | "PRODUCT"`. Tạo type `ActionType = `${Action}_${Entity}``.
- **Keywords:** `typescript template literal types`, `redux action type generation`
- **Input / Output Mẫu (Type Check):**
```typescript
type AppAction = ActionType;
// Expected: "SET_USER" | "SET_PRODUCT" | "RESET_USER" | "RESET_PRODUCT"
```

---

### Bài 25: `KeysOfType<T, ValueType>` & `PickByType<T, ValueType>`
- **Ứng dụng thực tế:** Lọc các trường trong form schema chỉ lấy trường dạng chuỗi hoặc số để render đúng component Input.
- **Yêu cầu:**
  1. Viết `type KeysOfType<T, ValueType> = { [K in keyof T]: T[K] extends ValueType ? K : never }[keyof T]`.
  2. Viết `type PickByType<T, ValueType> = Pick<T, KeysOfType<T, ValueType>>`.
- **Keywords:** `typescript key remapping as`, `pick properties by type`
- **Input / Output Mẫu (Type Check):**
```typescript
interface ProductForm { id: number; title: string; price: number; inStock: boolean }

type TextFields = KeysOfType<ProductForm, string>; // "title"
type NumericProps = PickByType<ProductForm, number>; // { id: number; price: number }
```

---

### Bài 26: Type-Safe Event Emitter (Socket.io & Event Bus)
- **Ứng dụng thực tế:** Xây dựng Event Bus cho Socket.io hoặc component communication đảm bảo gửi đúng event và payload.
- **Yêu cầu:** Tạo class `TypedEventEmitter<EventMap>` có method `on` và `emit`.
- **Keywords:** `typescript type-safe event emitter`, `socket.io generic typing`
- **Input / Output Mẫu:**
```typescript
interface SocketEvents {
  "chat:message": { room: string; message: string };
  "user:online": { userId: string };
}

const socket = new TypedEventEmitter<SocketEvents>();

socket.emit("chat:message", { room: "general", message: "Hello!" });

// @ts-expect-error - Sai tên event hoặc payload
socket.emit("chat:message", { room: 123 });
```

---

### Bài 27: ES6 `Map<K, V>` In-Memory Storage (Express Session / Cache)
> *Đã tinh chỉnh: Đơn giản hóa, tập trung vào việc áp dụng Map có kiểu Generic.*
- **Ứng dụng thực tế:** Quản lý User Sessions hoặc in-memory Key-Value store trong Backend Express.
- **Yêu cầu:** Tạo class `SessionStore<T>` bọc `Map<string, T>` với các method type-safe: `setSession(token: string, data: T): void`, `getSession(token: string): T | undefined`, `removeSession(token: string): boolean`.
- **Keywords:** `es6 map generic typescript`, `in memory session store`
- **Input / Output Mẫu:**
```typescript
interface UserSession { userId: number; role: "admin" | "user" }

const sessions = new SessionStore<UserSession>();
sessions.setSession("token_123", { userId: 1, role: "admin" });

const user = sessions.getSession("token_123");
console.log(user?.role); // "admin" (Type: "admin" | "user" | undefined)
```

---

### Bài 28: `Promise.all` & `Promise.allSettled` (Batch Requests Concurrency)
- **Ứng dụng thực tế:** Gọi đồng thời nhiều API ở React hoặc gom nhiều query độc lập ở Backend Express mà không sợ 1 task lỗi làm sập toàn bộ luồng.
- **Yêu cầu:** Viết hàm `fetchBatchSummaries<T>(promises: Promise<T>[]): Promise<{ successes: T[]; errors: string[] }>`. Dùng `Promise.allSettled`.
- **Keywords:** `promise.allsettled typescript`, `batch async requests handling`
- **Input / Output Mẫu:**
```typescript
const p1 = Promise.resolve("User data");
const p2 = Promise.reject(new Error("Order service down"));
const p3 = Promise.resolve("Notifications data");

const { successes, errors } = await fetchBatchSummaries([p1, p2, p3]);
// successes === ["User data", "Notifications data"]
// errors    === ["Order service down"]
```

---

### Bài 29: Type-Safe `Array.prototype.reduce` (Data Aggregation DTO)
- **Ứng dụng thực tế:** Gom nhóm dữ liệu trả về từ Database (ví dụ nhóm danh sách đơn hàng theo danh mục hoặc trạng thái).
- **Yêu cầu:** Viết hàm `groupBy<T, K extends PropertyKey>(arr: T[], getKey: (item: T) => K): Record<K, T[]>`.
- **Keywords:** `array reduce typescript typing`, `generic groupby backend data`
- **Input / Output Mẫu:**
```typescript
interface Order { id: string; status: "PENDING" | "DELIVERED"; amount: number }

const orders: Order[] = [
  { id: "o1", status: "PENDING", amount: 100 },
  { id: "o2", status: "DELIVERED", amount: 200 },
  { id: "o3", status: "PENDING", amount: 150 }
];

const grouped = groupBy(orders, (o) => o.status);
// Type: Record<"PENDING" | "DELIVERED", Order[]>
// Value: { PENDING: [o1, o3], DELIVERED: [o2] }
```

---

## 💡 Lời Khuyên Khi Luyện Tập
1. **Mục tiêu 4 - 5 bài mỗi ngày:** Hoàn thành toàn bộ lộ trình 29 bài này chỉ trong **1 tuần** (khoảng 6 ngày).
2. **Thực hành trực tiếp trên VSCode / TypeScript Playground:** Tự gõ lại code và thử các trường hợp đúng lẫn sai (`// @ts-expect-error`) để rèn luyện phản xạ.
3. **Áp dụng ngay vào Project:** Sau khi hoàn thành Chặng 2 và Chặng 3, bạn đã đủ 100% tự tin để bắt tay vào xây dựng ngay dự án **React + Express / NestJS** với chuẩn Type Safety cao nhất!
