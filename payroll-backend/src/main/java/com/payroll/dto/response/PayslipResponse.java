package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String department;
    private String designation;
    private Integer payPeriodMonth;
    private Integer payPeriodYear;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal totalEarnings;
    private BigDecimal pfDeduction;
    private BigDecimal taxDeduction;
    private BigDecimal insuranceDeduction;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private LocalDateTime generatedAt;
    private String status;
}
