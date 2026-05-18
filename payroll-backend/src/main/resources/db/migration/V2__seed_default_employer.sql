-- V2__seed_default_employer.sql
-- Default employer account: admin@payroll.com / changeme
-- Password hash is BCrypt of 'changeme'
INSERT INTO users (email, password_hash, role, is_active)
VALUES (
    'admin@payroll.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ROLE_EMPLOYER',
    TRUE
);
