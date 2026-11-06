# Pilates Studio Management System – Nhóm 8

Ứng dụng quản lý Pilates hoàn chỉnh gồm frontend Vanilla HTML/CSS/JavaScript, backend Java 17/Spring Boot 3 và MySQL 8. Frontend giữ nguyên giao diện luxury và gọi REST API thật; LocalStorage chỉ lưu JWT/session, không lưu dữ liệu nghiệp vụ.

## Yêu cầu

- Java/Javac 17
- Maven 3.9+
- MySQL 8 với database `pilates_studio` ở Flyway V2
- Python 3 để chạy static server frontend

## Cấu hình backend

Không ghi secret vào repository. Đặt trong cùng terminal chạy backend:

```powershell
$env:DB_URL="jdbc:mysql://127.0.0.1:3306/pilates_studio?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="pilates_app"
$env:DB_PASSWORD="<your-password>"
$env:JWT_SECRET="<base64-secret-at-least-32-bytes>"
$env:FRONTEND_ORIGIN="http://localhost:5173,http://127.0.0.1:5173"
$env:DEMO_DATA_ENABLED="false"
```

Tài khoản ứng dụng cần `SELECT, INSERT, UPDATE, DELETE` trên `pilates_studio.*`. Schema hiện không cần DDL mới. Flyway chỉ validate V1–V2 đã áp dụng.

## Chạy backend

```powershell
cd backend
mvn clean test
mvn spring-boot:run
```

Kiểm tra: `http://127.0.0.1:8080/actuator/health`.

## Chạy frontend

Tại thư mục gốc:

```powershell
python -m http.server 5173 --directory frontend
```

Mở `http://127.0.0.1:5173`. API base được suy ra theo hostname hiện tại và cổng backend 8080.

## Tài khoản demo hiện có

| Role | Email |
|---|---|
| ADMIN | `admin@pilates.com` |
| STAFF | `staff@pilates.com` |
| TRAINER | `trainer@pilates.com` |
| MEMBER | `member@pilates.com` |

Mật khẩu demo chỉ dành cho môi trường trình diễn. Seeder mặc định bị tắt để không ghi đè hoặc tự thêm dữ liệu vào database hiện hữu.

## Kiểm thử và build

```powershell
cd backend
mvn clean test
mvn package

cd ..
Get-ChildItem frontend/js -Filter *.js | ForEach-Object { node --check $_.FullName }
```

Tài liệu: [ERD](docs/ERD.md), [API](docs/API_DOCUMENTATION.md), [Frontend analysis](docs/FRONTEND_ANALYSIS.md), [Implementation status](docs/IMPLEMENTATION_PLAN.md).
