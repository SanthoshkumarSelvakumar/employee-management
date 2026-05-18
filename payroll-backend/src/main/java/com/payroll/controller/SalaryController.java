package com.payroll.controller;

import com.payroll.dto.request.SalaryRequest;
import com.payroll.dto.response.SalaryResponse;
import com.payroll.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees/{employeeId}/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public ResponseEntity<List<SalaryResponse>> getSalaryHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getEmployeeSalaryHistory(employeeId));
    }

    @GetMapping("/current")
    public ResponseEntity<SalaryResponse> getCurrentSalary(@PathVariable Long employeeId) {
        SalaryResponse response = salaryService.getCurrentSalary(employeeId);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<SalaryResponse> setSalaryForNextMonth(
            @PathVariable Long employeeId,
            @Valid @RequestBody SalaryRequest request,
            Authentication authentication) {
        SalaryResponse response = salaryService.setSalaryForNextMonth(
                employeeId, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
