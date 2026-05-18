package com.payroll.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @Size(max = 100)
    private String designation;

    @NotNull(message = "Date of joining is required")
    private LocalDate dateOfJoining;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.01", message = "Basic salary must be positive")
    private BigDecimal basicSalary;

    @NotNull(message = "HRA is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal hra;

    @NotNull(message = "Allowances is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal allowances;

    @NotNull(message = "PF deduction is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal pfDeduction;

    @NotNull(message = "Tax deduction is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal taxDeduction;

    @NotNull(message = "Insurance deduction is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal insuranceDeduction;
}
