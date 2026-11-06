# Pilates Studio - Vanilla Frontend

Đây là phiên bản chuyển đổi song song từ React sang HTML5, CSS3 và JavaScript ES6 modules. Không cần React, TypeScript, Vite hay Tailwind để chạy phần này.

## Chạy local

Mở `frontend/index.html` bằng một static server (khuyến nghị VS Code Live Server):

```powershell
py -m http.server 5500 --directory frontend
```

Sau đó truy cập `http://localhost:5500`.

Tài khoản demo: `admin@pilates.com`, `staff@pilates.com`, `trainer@pilates.com`, `member@pilates.com`; mật khẩu `123456`.

## Phân lớp

- `js/state.js`: mock state, seed data, session và helpers.
- `js/repository.js`: repository boundary và business rules CRUD.
- `js/auth.js`: login/logout flow.
- `js/ui.js`: modal, toast, form, table và badge dùng chung.
- `js/app.js`: hash routing, layout, RBAC và các màn hình UC1–UC8.
- `css/`: global, layout, components và responsive media queries.

Repository hiện vẫn dùng mock data để đối chiếu nghiệp vụ. Khi Spring Boot sẵn sàng, thay implementation trong `js/repository.js` bằng Fetch API, giữ nguyên interface mà UI đang gọi.
