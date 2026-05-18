package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String hoursWorked;
}
