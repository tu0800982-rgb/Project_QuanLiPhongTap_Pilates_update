UPDATE users
SET full_name = 'Đỗ Gia Huy'
WHERE id IN (
    SELECT user_id
    FROM employees
    WHERE TRIM(full_name) = 'CODEX Trainer Updated 230339'
);

UPDATE employees
SET full_name = 'Đỗ Gia Huy'
WHERE TRIM(full_name) = 'CODEX Trainer Updated 230339';
