# Trạng thái triển khai

## Đã hoàn thành

1. Audit schema MySQL 19 bảng, PK/FK/unique/index và Flyway V1–V2.
2. Backend Java 17/Spring Boot 3.3.5, JPA, Validation, Security, JWT access/refresh, BCrypt và CORS.
3. Entity/Repository/DTO/Service/Controller cho UC1–UC8 theo schema thực tế.
4. RBAC backend cho ADMIN, STAFF, TRAINER, MEMBER và ownership/trainer scope.
5. Transaction mua gói/payment, đăng ký/hủy lớp, session ledger, attendance và audit log.
6. Frontend Vanilla giữ nguyên giao diện; dữ liệu nghiệp vụ chuyển từ LocalStorage sang Fetch API.
7. Seeder bị tắt mặc định; chỉ bật rõ ràng bằng `DEMO_DATA_ENABLED=true` hoặc trong test H2.
8. Unit/integration tests H2 và smoke test MySQL thật cho bốn vai trò.

## Kiểm tra trước khi phát hành

- Chạy `mvn clean test` và `mvn package`.
- Khởi động bằng environment DB/JWT, xác nhận `/actuator/health`.
- Phục vụ `frontend/` tại cổng 5173, kiểm tra login và browser console.
- Kiểm thử CRUD, đăng ký/hủy, điểm danh, payment và report trên môi trường dữ liệu thử riêng.
- Không chạy migration mới nếu chưa có kế hoạch và quyền DDL được phê duyệt.

## Migration

Schema hiện ở Flyway V2 và không cần migration mới. Nếu có migration tương lai, tài khoản migration cần tối thiểu quyền phù hợp câu lệnh cụ thể như `CREATE`, `ALTER`, `INDEX`, `REFERENCES`; không cấp `DROP` nếu migration không yêu cầu.
