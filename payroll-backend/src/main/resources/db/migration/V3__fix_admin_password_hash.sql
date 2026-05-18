-- V3__fix_admin_password_hash.sql
-- Fix BCrypt hash for default employer (admin@payroll.com / changeme)
UPDATE users
SET password_hash = '$2a$10$m2cADVpghpjBen7yeRc7s.V/MTnOahl1PLCpxp/cBNkA03Fq2DqV.'
WHERE email = 'admin@payroll.com';
