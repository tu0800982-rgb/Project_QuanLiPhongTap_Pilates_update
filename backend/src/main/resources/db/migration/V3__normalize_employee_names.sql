UPDATE users
SET full_name = 'Vũ Thành Đạt'
WHERE LOWER(email) = 'admin@pilates.com'
  AND TRIM(full_name) = 'Quản trị viên';

UPDATE users
SET full_name = 'Phạm Thu Hà'
WHERE LOWER(email) = 'staff@pilates.com'
  AND TRIM(full_name) = 'Nhân viên Studio';

UPDATE users
SET full_name = 'Nguyễn Minh Khang'
WHERE LOWER(email) = 'trainer@pilates.com'
  AND TRIM(full_name) = 'Nguyễn Minh Trainer';

UPDATE users
SET full_name = 'Đỗ Gia Huy'
WHERE id IN (
    SELECT user_id
    FROM employees
    WHERE TRIM(full_name) = 'CODEX'
);

UPDATE employees
SET full_name = CASE TRIM(full_name)
    WHEN 'Quản trị viên' THEN 'Vũ Thành Đạt'
    WHEN 'Nhân viên Studio' THEN 'Phạm Thu Hà'
    WHEN 'Nguyễn Minh Trainer' THEN 'Nguyễn Minh Khang'
    WHEN 'CODEX' THEN 'Đỗ Gia Huy'
    ELSE full_name
END
WHERE TRIM(full_name) IN ('Quản trị viên', 'Nhân viên Studio', 'Nguyễn Minh Trainer', 'CODEX');
