package com.payroll.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRequest {

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
