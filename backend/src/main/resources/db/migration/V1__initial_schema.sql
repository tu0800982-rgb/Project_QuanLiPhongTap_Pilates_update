CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    INDEX idx_users_status (status)
);

CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL UNIQUE,
    member_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    date_of_birth DATE NULL,
    gender VARCHAR(20) NULL,
    address VARCHAR(255) NULL,
    emergency_contact VARCHAR(150) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_members_name (full_name),
    INDEX idx_members_status (status)
);

CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    employee_type VARCHAR(30) NOT NULL,
    specialization VARCHAR(255) NULL,
    biography TEXT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_employees_type_status (employee_type, status)
);

CREATE TABLE class_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE packages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    duration_days INT NOT NULL,
    session_count INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_packages_duration CHECK (duration_days > 0),
    CONSTRAINT chk_packages_sessions CHECK (session_count > 0),
    CONSTRAINT chk_packages_price CHECK (price >= 0),
    INDEX idx_packages_status (status)
);

CREATE TABLE package_class_types (
    package_id BIGINT NOT NULL,
    class_type_id BIGINT NOT NULL,
    PRIMARY KEY (package_id, class_type_id),
    CONSTRAINT fk_pct_package FOREIGN KEY (package_id) REFERENCES packages(id),
    CONSTRAINT fk_pct_class_type FOREIGN KEY (class_type_id) REFERENCES class_types(id)
);

CREATE TABLE member_packages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    package_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_sessions INT NOT NULL,
    remaining_sessions INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    renewed_from_id BIGINT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_mp_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_mp_package FOREIGN KEY (package_id) REFERENCES packages(id),
    CONSTRAINT fk_mp_renewed FOREIGN KEY (renewed_from_id) REFERENCES member_packages(id),
    CONSTRAINT fk_mp_creator FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT chk_mp_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_mp_remaining CHECK (remaining_sessions >= 0 AND remaining_sessions <= total_sessions),
    INDEX idx_mp_member_status (member_id, status),
    INDEX idx_mp_expiry (end_date, status)
);

CREATE TABLE classes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    class_type_id BIGINT NOT NULL,
    location VARCHAR(150) NOT NULL,
    default_capacity INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_classes_type FOREIGN KEY (class_type_id) REFERENCES class_types(id),
    CONSTRAINT chk_classes_capacity CHECK (default_capacity > 0),
    INDEX idx_classes_status (status)
);

CREATE TABLE class_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    starts_at TIMESTAMP(6) NOT NULL,
    ends_at TIMESTAMP(6) NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_schedules_class FOREIGN KEY (class_id) REFERENCES classes(id),
    CONSTRAINT fk_schedules_trainer FOREIGN KEY (trainer_id) REFERENCES employees(id),
    CONSTRAINT chk_schedules_time CHECK (ends_at > starts_at),
    CONSTRAINT chk_schedules_capacity CHECK (capacity > 0),
    INDEX idx_schedules_time_status (starts_at, status),
    INDEX idx_schedules_trainer_time (trainer_id, starts_at, ends_at)
);

CREATE TABLE class_registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    member_package_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'REGISTERED',
    registered_by BIGINT NOT NULL,
    registered_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    cancelled_by BIGINT NULL,
    cancelled_at TIMESTAMP(6) NULL,
    cancellation_reason VARCHAR(500) NULL,
    late_cancellation BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_cr_schedule FOREIGN KEY (schedule_id) REFERENCES class_schedules(id),
    CONSTRAINT fk_cr_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_cr_member_package FOREIGN KEY (member_package_id) REFERENCES member_packages(id),
    CONSTRAINT fk_cr_registered_by FOREIGN KEY (registered_by) REFERENCES users(id),
    CONSTRAINT fk_cr_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id),
    INDEX idx_cr_schedule_status (schedule_id, status),
    INDEX idx_cr_member_status (member_id, status)
);

CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    marked_by BIGINT NOT NULL,
    marked_at TIMESTAMP(6) NOT NULL,
    updated_by BIGINT NULL,
    updated_at TIMESTAMP(6) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_attendance_schedule_member UNIQUE (schedule_id, member_id),
    CONSTRAINT fk_att_schedule FOREIGN KEY (schedule_id) REFERENCES class_schedules(id),
    CONSTRAINT fk_att_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_att_registration FOREIGN KEY (registration_id) REFERENCES class_registrations(id),
    CONSTRAINT fk_att_marker FOREIGN KEY (marked_by) REFERENCES users(id),
    CONSTRAINT fk_att_updater FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_att_member (member_id),
    INDEX idx_att_status (status)
);

CREATE TABLE session_usage_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_package_id BIGINT NOT NULL,
    attendance_id BIGINT NULL,
    registration_id BIGINT NULL,
    delta INT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_sul_mp FOREIGN KEY (member_package_id) REFERENCES member_packages(id),
    CONSTRAINT fk_sul_att FOREIGN KEY (attendance_id) REFERENCES attendance(id),
    CONSTRAINT fk_sul_reg FOREIGN KEY (registration_id) REFERENCES class_registrations(id),
    CONSTRAINT fk_sul_creator FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_sul_mp_time (member_package_id, created_at),
    INDEX idx_sul_attendance (attendance_id)
);

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code VARCHAR(40) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    member_package_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(15,2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP(6) NULL,
    refunded_at TIMESTAMP(6) NULL,
    performed_by BIGINT NULL,
    refund_reason VARCHAR(500) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_pay_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_pay_mp FOREIGN KEY (member_package_id) REFERENCES member_packages(id),
    CONSTRAINT fk_pay_performer FOREIGN KEY (performed_by) REFERENCES users(id),
    CONSTRAINT chk_pay_amount CHECK (amount >= 0),
    INDEX idx_pay_member_status (member_id, status),
    INDEX idx_pay_created (created_at)
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    read_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_notifications_user_read (user_id, read_at)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NULL,
    before_json JSON NULL,
    after_json JSON NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_actor_time (actor_id, created_at)
);

CREATE TABLE revoked_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token_id VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_revoked_expiry (expires_at)
);
