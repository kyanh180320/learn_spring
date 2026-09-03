# 🚀 Lộ Trình 30 Bài Tập TypeScript & ES6+ Cốt Lõi (Thực Chiến React & Express)

> **Mục tiêu:** Tối ưu hóa 100% thời gian học, **cắt bỏ toàn bộ Type Gymnastics hàn lâm**, chỉ tập trung vào những kỹ thuật bạn sẽ viết và đọc **hằng ngày** trong các dự án **React (Next.js)** và **Backend Express / NestJS**.

---

## 🧭 Cấu Trúc 4 Chặng Luyện Tập (30 Bài Cốt Lõi Thực Chiến)

| Chặng | Cấp độ | Số bài | Ứng dụng thực tế trong React & Express |
| :--- | :--- | :---: | :--- |
| **Chặng 1** | 🟢 Nền tảng Types & Cú pháp | Bài 01 - 10 | Props, State cơ bản, Union literal, Interface DTO, xử lý `null`/`undefined` (`??`, `?.`) |
| **Chặng 2** | 🟡 Thu hẹp kiểu & Generics | Bài 11 - 16 | React Async State (`idle`/`loading`), Custom Type Guard validate `req.body`, Generic Fetcher & Repo |
| **Chặng 3** | 🟠 Utility Types & Form State | Bài 17 - 20 | DTO (`Partial`/`Pick`/`Omit`), Form State typing, Unwrap Hook & Promise với `ReturnType`/`Awaited` |
| **Chặng 4** | 🔴 ES6+ Runtime & Async Thực Chiến | Bài 21 - 30 | Safe `try/catch`, Destructuring, Spread/Rest Immutability, Dynamic Keys, Map/Set, Object.entries |

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

### Bài 20: Built-in Utility Types: `ReturnType<T>` & `Awaited<T>` (Unwrap Hook & Async API)
- **Ứng dụng thực tế:** 
  1. Trích xuất kiểu State trả về từ một Custom Hook trong React mà thư viện ngoài không export sẵn Type (`ReturnType<typeof useAuth>`).
  2. Trích xuất kiểu dữ liệu thực tế bên trong Promise của một hàm Async API trong Backend (`Awaited<ReturnType<typeof fetchUser>>`).
- **Yêu cầu:** Không cần dùng `infer` phức tạp, hãy sử dụng 2 utility chính chủ có sẵn của TypeScript:
  1. Dùng `ReturnType<typeof useAuth>` để lấy kiểu `AuthState`.
  2. Dùng `Awaited<ReturnType<typeof fetchUserData>>` để lấy kiểu `UserDetail`.
- **Keywords:** `typescript returntype`, `typescript awaited utility`, `extract type from async function`
- **Input / Output Mẫu (Type Check):**
```typescript
function useAuth() {
  return { user: { id: "1", name: "Alice" }, token: "jwt_123" };
}

async function fetchUserData(id: number) {
  return { id, name: "Alice", email: "alice@test.com", role: "admin" as const };
}

// 1. Trích xuất kiểu trả về của Hook
type AuthState = ReturnType<typeof useAuth>;
// { user: { id: string; name: string }; token: string }

// 2. Unwrap Promise của hàm async (chính chủ từ TS 4.5+)
type UserDetail = Awaited<ReturnType<typeof fetchUserData>>;
// { id: number; name: string; email: string; role: "admin" }
```

---

## 🔴 CHẶNG 4: ES6+ RUNTIME, IMMUTABILITY & ASYNC THỰC CHIẾN
*Mục tiêu: Làm chủ các tính năng runtime ES6+ quan trọng nhất kết hợp cùng Type Safety để xây dựng ứng dụng React & Express.*

---

### Bài 21: Safe `try/catch` & Unknown Error Handling
- **Ứng dụng thực tế:** Trong TypeScript (chế độ strict), biến lỗi trong `catch (err: unknown)` luôn có kiểu `unknown`. Viết hàm tiện ích trích xuất message lỗi an toàn cho React và Express.
- **Yêu cầu:**
  1. Viết hàm `getErrorMessage(error: unknown): string`. Nếu `error instanceof Error` trả về `error.message`, nếu là string trả về chính nó, ngược lại trả về `"Unknown error"`.
  2. Viết hàm wrapper `safeAsync<T>(fn: () => Promise<T>): Promise<{ data: T; error: null } | { data: null; error: string }>`.
- **Keywords:** `typescript try catch unknown error`, `safe async wrapper typescript`
- **Input / Output Mẫu:**
```typescript
// Sử dụng trong Express Controller / React Handler:
const result = await safeAsync(() => fetchUserData(123));

if (result.error) {
  console.log("Lỗi:", result.error); // error là string an toàn
} else {
  console.log("Data:", result.data);  // data được định kiểu chuẩn xác
}
```

---

### Bài 22: `Promise.all` & `Promise.allSettled` (Batch Requests Concurrency)
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

### Bài 23: ES6 `Map<K, V>` In-Memory Storage (Express Session / Cache)
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

### Bài 24: Type-Safe `Array.prototype.reduce` (Data Aggregation DTO)
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

### Bài 25: Object & Nested Destructuring với Aliasing & Default Values
- **Ứng dụng thực tế:** Bóc tách props trong React Component và parse `req.query` / `req.body` trong Express. Tránh nhầm lẫn kinh điển giữa cú pháp gán kiểu và đổi tên biến (alias).
- **Yêu cầu:** 
  1. Cho interface:
     ```typescript
     interface UserCardProps {
       id: string;
       fullName: string;
       role?: "ADMIN" | "MEMBER";
       address: { city: string; country: string };
     }
     ```
  2. Viết hàm `formatUserCard(props: UserCardProps): string` thực hiện destructuring:
     - Đổi tên `fullName` thành `displayName` bằng cú pháp alias (`fullName: displayName`).
     - Gán giá trị mặc định cho `role` là `"MEMBER"` nếu không truyền.
     - Bóc tách lồng nhau (nested destructuring) trường `city` từ `address`.
     - Trả về chuỗi: `"[displayName] (role) - city"`.
  3. Viết hàm `getFirstAndRest<T>(items: T[]): { first?: T; rest: T[] }` dùng Array Destructuring và Rest syntax.
- **Keywords:** `typescript destructuring alias rename`, `destructuring default values`, `nested destructuring`
- **Input / Output Mẫu:**
```typescript
const user1: UserCardProps = {
  id: "u1",
  fullName: "Kỳ Anh",
  address: { city: "Đà Nẵng", country: "VN" }
};

formatUserCard(user1); 
// "[Kỳ Anh] (MEMBER) - Đà Nẵng"

const user2: UserCardProps = {
  id: "u2",
  fullName: "Alex",
  role: "ADMIN",
  address: { city: "Hà Nội", country: "VN" }
};

formatUserCard(user2); 
// "[Alex] (ADMIN) - Hà Nội"

getFirstAndRest([1, 2, 3, 4]); // { first: 1, rest: [2, 3, 4] }
getFirstAndRest([]);           // { first: undefined, rest: [] }
```

---

### Bài 26: Spread Operator & Immutable State Updates (React State & Redux)
- **Ứng dụng thực tế:** Cập nhật Object lồng nhau và Mảng trong React State mà không mutate trực tiếp object ban đầu (nguyên tắc bất biến Immutability của React/Redux).
- **Yêu cầu:** Cho kiểu:
  ```typescript
  interface CartItem { id: string; name: string; price: number; quantity: number }
  interface ShoppingCartState { items: CartItem[]; coupon?: { code: string; discountPercent: number } }
  ```
  1. Viết hàm `addItem(state: ShoppingCartState, newItem: CartItem): ShoppingCartState`: Thêm một món hàng mới vào mảng `items` bằng Spread Operator (không dùng `push`).
  2. Viết hàm `updateQuantity(state: ShoppingCartState, itemId: string, quantity: number): ShoppingCartState`: Cập nhật số lượng của sản phẩm có `id` tương ứng (trả về mảng mới dùng `.map()` và Spread, không mutate item cũ).
  3. Viết hàm `applyCoupon(state: ShoppingCartState, code: string, discount: number): ShoppingCartState`: Cập nhật nested object `coupon` bằng Spread.
- **Keywords:** `spread operator immutability react`, `immutable nested object update typescript`, `react state update array`
- **Input / Output Mẫu:**
```typescript
const initialCart: ShoppingCartState = {
  items: [{ id: "p1", name: "Chuột Gaming", price: 50, quantity: 1 }],
};

const cartWithItem = addItem(initialCart, { id: "p2", name: "Bàn phím cơ", price: 100, quantity: 1 });
// cartWithItem.items.length === 2; initialCart.items.length === 1 (Không bị mutate!)

const cartUpdatedQty = updateQuantity(cartWithItem, "p1", 3);
// cartUpdatedQty.items[0].quantity === 3; cartWithItem.items[0].quantity === 1

const cartWithCoupon = applyCoupon(cartUpdatedQty, "SALE20", 20);
// cartWithCoupon.coupon === { code: "SALE20", discountPercent: 20 }
```

---

### Bài 27: Rest Parameters & Forwarding Props (Wrapper Component Pattern)
- **Ứng dụng thực tế:** Xây dựng Reusable UI Components (Button, Input) trong React hoặc Wrapper Functions trong Backend Express. Gom các thuộc tính còn lại (`...restProps`) để truyền tiếp vào thẻ HTML hoặc hàm gốc.
- **Yêu cầu:**
  1. Cho interface `BaseButtonProps { label: string; isLoading?: boolean; variant?: "solid" | "outline"; [key: string]: unknown }`.
  2. Viết hàm `createButtonAttributes(props: BaseButtonProps): { buttonText: string; domAttributes: Record<string, unknown> }`.
  3. Dùng Rest destructuring: Bóc tách `label`, `isLoading = false`, phần còn lại gộp vào `domAttributes`.
  4. Nếu `isLoading === true`, gán thêm `domAttributes.disabled = true` và `buttonText = "Đang tải..."`, ngược lại `buttonText = label`.
- **Keywords:** `rest parameters forwarding props`, `wrapper component react typescript`, `rest properties destructuring`
- **Input / Output Mẫu:**
```typescript
const result = createButtonAttributes({
  label: "Xác nhận thanh toán",
  isLoading: false,
  variant: "solid",
  onClick: () => console.log("clicked"),
  "data-testid": "submit-btn"
});

console.log(result.buttonText); // "Xác nhận thanh toán"
console.log(result.domAttributes); 
// { variant: "solid", onClick: [Function], "data-testid": "submit-btn" } (Không chứa label, isLoading)
```

---

### Bài 28: Computed Property Names (`[key]: value` - Generic Dynamic Form)
- **Ứng dụng thực tế:** Viết hàm xử lý sự kiện `onChange` tổng quát cho Form trong React thay vì phải viết riêng rẽ từng hàm `handleNameChange`, `handleEmailChange`, `handleAgeChange`.
- **Yêu cầu:**
  1. Viết hàm generic `updateFormField<T, K extends keyof T>(form: T, field: K, value: T[K]): T`.
  2. Sử dụng cú pháp ES6 Computed Property Names `[field]: value` kết hợp Spread `{ ...form, [field]: value }`.
  3. Đảm bảo TypeScript kiểm tra chặt chẽ: `value` truyền vào phải khớp 100% với kiểu dữ liệu của `field` tương ứng trong form.
- **Keywords:** `es6 computed property names`, `typescript dynamic object key`, `generic form onchange handler`
- **Input / Output Mẫu (Type Check):**
```typescript
interface UserRegistrationForm {
  username: string;
  email: string;
  age: number;
  isSubscribed: boolean;
}

const form: UserRegistrationForm = {
  username: "kyanh",
  email: "kyanh@test.com",
  age: 24,
  isSubscribed: false,
};

const updated1 = updateFormField(form, "age", 25);               // Type: UserRegistrationForm, age = 25
const updated2 = updateFormField(form, "isSubscribed", true);    // Type: UserRegistrationForm, isSubscribed = true

// @ts-expect-error - Sai kiểu: age yêu cầu number, không thể truyền string
updateFormField(form, "age", "hai mươi lăm");

// @ts-expect-error - Sai tên trường: "salary" không tồn tại trong form
updateFormField(form, "salary", 5000);
```

---

### Bài 29: ES6 `Set<T>` Deduplication & O(1) Membership Check (React Tags & RBAC)
- **Ứng dụng thực tế:**
  1. Lọc trùng lặp danh sách Tags/Danh mục từ API bằng `new Set()` và Spread `[...]`.
  2. Xây dựng Module kiểm tra quyền hạn (Role-Based Access Control - RBAC) trong Express Middleware với tốc độ tìm kiếm O(1) thay vì dùng `Array.includes()` tốn O(N).
- **Yêu cầu:**
  1. Viết hàm `getUniqueTags(tags: string[]): string[]` sử dụng `new Set()` và cú pháp Spread.
  2. Tạo class `PermissionGuard` nhận mảng các quyền hạn (`string[]`), lưu trong thuộc tính `private permissions: Set<string>`.
  3. Cung cấp 3 method:
     - `hasPermission(perm: string): boolean` (dùng `this.permissions.has(perm)` O(1)).
     - `hasAnyPermission(perms: string[]): boolean` (kiểm tra có ít nhất 1 quyền, dùng `.some()`).
     - `hasAllPermissions(perms: string[]): boolean` (kiểm tra có đủ tất cả quyền, dùng `.every()`).
- **Keywords:** `es6 set deduplication array`, `typescript set type safe`, `rbac permission check o1 set`
- **Input / Output Mẫu:**
```typescript
// 1. Deduplicate tags
const rawTags = ["react", "typescript", "react", "nextjs", "typescript"];
getUniqueTags(rawTags); // ["react", "typescript", "nextjs"]

// 2. Permission Guard
const guard = new PermissionGuard(["USER_READ", "USER_WRITE", "PRODUCT_READ"]);

guard.hasPermission("USER_WRITE");                    // true (O(1))
guard.hasPermission("PRODUCT_DELETE");               // false
guard.hasAnyPermission(["ADMIN", "USER_READ"]);       // true
guard.hasAllPermissions(["USER_READ", "ORDER_READ"]); // false
```

---

### Bài 30: `Object.entries` & `Object.fromEntries` (DTO Sanitization & Query String)
- **Ứng dụng thực tế:**
  1. **Clean Payload:** Lọc bỏ các trường `undefined`, `null` hoặc chuỗi rỗng từ Form Object trước khi gửi POST/PUT lên Backend (tránh gửi data rác lên server).
  2. **Query Parser:** Chuyển đổi và biến đổi dữ liệu URLSearchParams hoặc DTO sang cấu trúc dữ liệu mong muốn trong Express/Next.js.
- **Yêu cầu:**
  1. Viết hàm generic `removeNullishFields<T extends Record<string, any>>(obj: T): Partial<T>`.
  2. Dùng `Object.entries(obj)` để lấy danh sách cặp `[key, value]`.
  3. Dùng `.filter()` loại bỏ các phần tử có `value === null` hoặc `value === undefined` hoặc `value === ""`.
  4. Dùng `Object.fromEntries()` để ráp ngược lại thành một object sạch sẽ.
- **Keywords:** `object.entries object.fromentries typescript`, `sanitize dto remove empty fields`, `filter object key value`
- **Input / Output Mẫu:**
```typescript
interface SearchQueryDto {
  keyword: string;
  category?: string | null;
  minPrice?: number | null;
  page: number;
}

const dirtyQuery: SearchQueryDto = {
  keyword: "bàn phím",
  category: null,
  minPrice: undefined,
  page: 1,
};

const cleanQuery = removeNullishFields(dirtyQuery);
console.log(cleanQuery);
// { keyword: "bàn phím", page: 1 } (Đã loại bỏ hoàn toàn category và minPrice!)
```

---

## 💡 Lời Khuyên Khi Luyện Tập
1. **Mục tiêu 4 - 5 bài mỗi ngày:** Hoàn thành toàn bộ lộ trình 30 bài cốt lõi này trong khoảng **6 - 7 ngày**.
2. **Thực hành trực tiếp trên VSCode / TypeScript Playground:** Tự gõ lại code và thử các trường hợp đúng lẫn sai (`// @ts-expect-error`) để rèn luyện phản xạ.
3. **Áp dụng ngay vào Project:** 30 bài này tập trung 100% vào những gì bạn sẽ viết và đọc mỗi ngày trong **React, Next.js và Express / NestJS** mà không hề có bất kỳ câu đố hàn lâm nào!
