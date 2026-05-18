package com.payroll.controller;

import com.payroll.dto.request.AttendanceRequest;
import com.payroll.dto.response.AttendanceResponse;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.service.AttendanceService;
import com.payroll.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createOrUpdateAttendance(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekOf) {
        EmployeeResponse profile = employeeService.getEmployeeByEmail(authentication.getName());
        List<AttendanceResponse> records = attendanceService.getWeeklyAttendance(profile.getId(), weekOf);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekOf) {
        List<AttendanceResponse> records = attendanceService.getWeeklyAttendance(employeeId, weekOf);
        return ResponseEntity.ok(records);
    }
}
