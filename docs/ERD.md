# ERD thực tế – Pilates Studio

Tài liệu này phản ánh schema MySQL `pilates_studio` đã audit ngày 2026-11-06. Database có 19 bảng InnoDB và được quản lý bởi Flyway V1–V2. Không dùng tên cũ `memberships`, `registrations` hoặc `package_allowed_classes`.

## Quan hệ chính

```text
roles 1 ── n users
users 1 ── 0..1 members
users 1 ── 0..1 employees

packages n ── n class_types          qua package_class_types
members 1 ── n member_packages
packages 1 ── n member_packages
users 1 ── n member_packages         created_by

classes n ── 1 class_types
classes 1 ── n class_schedules
employees 1 ── n class_schedules     trainer_id

class_schedules 1 ── n class_registrations
members 1 ── n class_registrations
member_packages 1 ── n class_registrations

class_registrations 1 ── 0..1 attendance
class_schedules 1 ── n attendance
members 1 ── n attendance

member_packages 1 ── 1 payments
members 1 ── n payments
users 1 ── n payments                performed_by

member_packages 1 ── n session_usage_ledger
users 1 ── n refresh_tokens
users 1 ── n notifications
users 1 ── n audit_logs              actor_id
```

## Bảng và ràng buộc

| Bảng | Khóa/cột nghiệp vụ chính |
|---|---|
| `roles` | PK `id`; unique `code`; `name` |
| `users` | PK `id`; FK `role_id`; unique `email`; `password_hash`, `full_name`, `status`, timestamps, `version` |
| `members` | PK `id`; unique nullable FK `user_id`; unique `member_code`, `phone`; hồ sơ và trạng thái |
| `employees` | PK `id`; unique FK `user_id`; unique `employee_code`, `phone`; `employee_type`, chuyên môn, trạng thái |
| `class_types` | PK `id`; unique `code`; `name`, `status` |
| `packages` | PK `id`; unique `package_code`; `duration_days`, `session_count`, `price DECIMAL(15,2)`, trạng thái |
| `package_class_types` | PK ghép `(package_id, class_type_id)` và hai FK tương ứng |
| `member_packages` | PK `id`; FK member/package/creator/renewed_from; ngày hiệu lực, tổng/còn lại số buổi, trạng thái |
| `classes` | PK `id`; unique `class_code`; FK `class_type_id`; location, default capacity, trạng thái |
| `class_schedules` | PK `id`; FK class/trainer; `starts_at`, `ends_at`, capacity, trạng thái, version |
| `class_registrations` | PK `id`; FK schedule/member/member_package/actors; trạng thái và thông tin hủy |
| `attendance` | PK `id`; unique `registration_id`; unique `(schedule_id, member_id)`; actor và timestamps |
| `payments` | PK `id`; unique `transaction_code`, `member_package_id`; FK member/performer; amount, method, status |
| `session_usage_ledger` | PK `id`; FK member_package/attendance/registration/creator; `delta`, `reason` |
| `audit_logs` | PK `id`; nullable FK actor; action, entity, JSON trước/sau, IP và thời điểm |
| `notifications` | PK `id`; FK user; title, content, read/created timestamps |
| `refresh_tokens` | PK `id`; unique `token_hash`; FK user; expiry/revocation/replacement |
| `revoked_tokens` | PK `id`; unique `token_id`; expiry và revoked timestamp |
| `flyway_schema_history` | Lịch sử migration; PK `installed_rank` |

Tất cả foreign key hiện dùng `UPDATE/DELETE NO ACTION`. Dữ liệu lịch sử được bảo toàn bằng chuyển trạng thái thay vì cascade delete.

## Quy tắc transaction

- Mua gói tạo `member_packages(PENDING_PAYMENT)` và `payments(PENDING)` trong cùng transaction.
- Chỉ khi payment chuyển `PAID`, member package mới chuyển `ACTIVE`.
- Đăng ký lớp khóa lịch và gói, kiểm tra capacity/trùng lịch/quyền lợi rồi trừ một buổi và ghi ledger `REGISTRATION_RESERVED`.
- Hủy đúng hạn hoàn một buổi đúng một lần bằng ledger `REGISTRATION_REFUND`; hủy muộn không hoàn.
- Điểm danh không thay đổi số buổi vì buổi đã được giữ khi đăng ký.
