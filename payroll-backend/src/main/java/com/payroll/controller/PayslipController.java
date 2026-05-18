package com.payroll.controller;

import com.payroll.dto.response.PayslipResponse;
import com.payroll.entity.Employee;
import com.payroll.entity.Payslip;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.PayslipService;
import com.payroll.service.PdfGenerationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payslips")
public class PayslipController {

    private final PayslipService payslipService;
    private final PdfGenerationService pdfGenerationService;
    private final EmployeeRepository employeeRepository;

    public PayslipController(PayslipService payslipService,
                             PdfGenerationService pdfGenerationService,
                             EmployeeRepository employeeRepository) {
        this.payslipService = payslipService;
        this.pdfGenerationService = pdfGenerationService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<Page<PayslipResponse>> getPayslips(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYER"))) { //ignorei18n_start //ignorei18n_end
            return ResponseEntity.ok(payslipService.getAllPayslips(pageable));
        }

        Employee employee = employeeRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found")); //ignorei18n_start //ignorei18n_end
        return ResponseEntity.ok(payslipService.getEmployeePayslips(employee.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayslipResponse> getPayslipById(@PathVariable Long id,
                                                          Authentication authentication) {
        PayslipResponse payslip = payslipService.getPayslipById(id);
        verifyPayslipAccess(payslip, authentication);
        return ResponseEntity.ok(payslip);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByYear(
            @PathVariable Integer year,
            Authentication authentication) {

        Employee employee = employeeRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found")); //ignorei18n_start //ignorei18n_end
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeAndYear(employee.getId(), year));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id,
                                                  Authentication authentication) throws IOException {
        PayslipResponse payslipResponse = payslipService.getPayslipById(id);
        verifyPayslipAccess(payslipResponse, authentication);

        Payslip payslip = payslipService.getPayslipEntity(id);
        byte[] pdfBytes = pdfGenerationService.generatePayslipPdf(payslip);

        String filename = "payslip_" + payslip.getEmployee().getEmployeeCode() //ignorei18n_start //ignorei18n_end
                + "_" + payslip.getPayPeriodMonth() + "_" + payslip.getPayPeriodYear() + ".pdf"; //ignorei18n_start //ignorei18n_end

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") //ignorei18n_start //ignorei18n_end
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/generate")
    public ResponseEntity<java.util.Map<String, Integer>> triggerPayslipGeneration(
            @RequestParam int month,
            @RequestParam int year) {
        java.util.Map<String, Integer> result = payslipService.generateMonthlyPayslips(month, year);
        return ResponseEntity.ok(result);
    }

    private void verifyPayslipAccess(PayslipResponse payslip, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYER"))) { //ignorei18n_start //ignorei18n_end
            return;
        }
        Employee employee = employeeRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found")); //ignorei18n_start //ignorei18n_end
        if (!payslip.getEmployeeId().equals(employee.getId())) {
            throw new ResourceNotFoundException("Payslip not found"); //ignorei18n_start //ignorei18n_end
        }
    }
}
