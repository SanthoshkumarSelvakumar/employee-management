-- V4__create_attendance_table.sql
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    date DATE NOT NULL,
    check_in TIME NOT NULL,
    check_out TIME,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_employee_date UNIQUE (employee_id, date)
);

CREATE INDEX idx_attendance_employee ON attendance(employee_id);
CREATE INDEX idx_attendance_date ON attendance(employee_id, date);
