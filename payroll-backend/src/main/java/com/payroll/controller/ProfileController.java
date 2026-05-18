package com.payroll.controller;

import com.payroll.dto.response.EmployeeResponse;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayslipService;
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

    public ProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<EmployeeResponse> getProfile(Authentication authentication) {
        EmployeeResponse response = employeeService.getEmployeeByEmail(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", employeeService.getActiveEmployeeCount()); //ignorei18n_start //ignorei18n_end
        return ResponseEntity.ok(stats);
    }
}
