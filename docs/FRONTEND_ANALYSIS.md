# Frontend Vanilla hiện tại

## Công nghệ và cấu trúc

Frontend trong `frontend/` dùng HTML5, CSS3 và JavaScript ES modules, chạy bằng static server. Không dùng React, TypeScript hoặc framework UI.

| File | Trách nhiệm |
|---|---|
| `index.html` | Entry point |
| `css/*.css` | Giao diện luxury, layout, components và responsive |
| `js/api.js` | Fetch client, JWT, refresh token và chuẩn hóa lỗi |
| `js/state.js` | Session/token local và format tiền; không lưu dữ liệu nghiệp vụ |
| `js/repository.js` | Adapter giữa model UI và REST API `/api/v1` |
| `js/auth.js` | Login view và login API |
| `js/ui.js` | Modal, toast, form, table, escaping |
| `js/app.js` | Hash routing, menu/RBAC và màn hình UC1–UC8 |

## Luồng dữ liệu

```text
UI event → repository.js → api.js/Fetch → Spring Boot → MySQL
```

LocalStorage chỉ giữ access token, refresh token và user summary. Dữ liệu member/package/class/registration/attendance/payment không còn lấy từ mock hoặc LocalStorage.

## Phân quyền giao diện

- ADMIN: UC1–UC8, nhân sự, report.
- STAFF: hội viên, package, lớp/lịch, registration, attendance, payment; không quản lý nhân sự và không xem report doanh thu.
- TRAINER: lịch được phân công, roster và attendance; không payment/report.
- MEMBER: profile, package của mình, lịch mở, đăng ký/hủy, attendance/payment của mình.

Frontend chỉ hỗ trợ trải nghiệm; backend vẫn là nguồn quyết định quyền và ownership.

## Trạng thái UX

- Có loading state khi đổi route.
- API error được hiển thị bằng toast hoặc error card.
- Bảng có empty state, tìm kiếm và phân trang giao diện.
- Các thao tác hủy/ngừng có bước xác nhận.
- Profile, logout và refresh token gọi backend thật.

## Giới hạn xác minh

JavaScript đã qua `node --check`, static server và CORS/API đã được smoke-test. Cần kiểm tra thủ công browser console và toàn bộ tương tác trực quan trước khi trình diễn cuối.
