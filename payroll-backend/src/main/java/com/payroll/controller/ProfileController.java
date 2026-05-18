package com.payroll.controller;

import com.payroll.dto.response.EmployeeResponse;
import com.payroll.dto.response.SalaryResponse;
import com.payroll.service.EmployeeService;
import com.payroll.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final EmployeeService employeeService;
    private final SalaryService salaryService;

    public ProfileController(EmployeeService employeeService, SalaryService salaryService) {
        this.employeeService = employeeService;
        this.salaryService = salaryService;
    }

    @GetMapping
    public ResponseEntity<EmployeeResponse> getProfile(Authentication authentication) {
        EmployeeResponse response = employeeService.getEmployeeByEmail(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/salary")
    public ResponseEntity<SalaryResponse> getMySalary(Authentication authentication) {
        EmployeeResponse profile = employeeService.getEmployeeByEmail(authentication.getName());
        SalaryResponse salary = salaryService.getCurrentSalary(profile.getId());
        if (salary == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salary);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", employeeService.getActiveEmployeeCount()); //ignorei18n_start //ignorei18n_end
        return ResponseEntity.ok(stats);
    }
}
