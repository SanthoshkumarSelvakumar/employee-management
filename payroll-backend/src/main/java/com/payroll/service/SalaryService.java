package com.payroll.service;

import com.payroll.dto.request.SalaryRequest;
import com.payroll.dto.response.SalaryResponse;
import com.payroll.entity.Employee;
import com.payroll.entity.SalaryStructure;
import com.payroll.entity.User;
import com.payroll.exception.BusinessException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.SalaryStructureRepository;
import com.payroll.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public SalaryService(SalaryStructureRepository salaryStructureRepository,
                         EmployeeRepository employeeRepository,
                         UserRepository userRepository) {
        this.salaryStructureRepository = salaryStructureRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public List<SalaryResponse> getEmployeeSalaryHistory(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId)); //ignorei18n_start //ignorei18n_end

        LocalDate today = LocalDate.now();
        List<SalaryStructure> structures = salaryStructureRepository.findAllByEmployeeId(employeeId);

        return structures.stream()
                .map(s -> toResponse(s, !s.getEffectiveFrom().isAfter(today)))
                .collect(Collectors.toList());
    }

    public SalaryResponse getCurrentSalary(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId)); //ignorei18n_start //ignorei18n_end

        SalaryStructure current = salaryStructureRepository
                .findActiveStructure(employeeId, LocalDate.now())
                .orElse(null);

        if (current == null) {
            return null;
        }
        return toResponse(current, true);
    }

    @Transactional
    public SalaryResponse setSalaryForNextMonth(Long employeeId, SalaryRequest request, String employerEmail) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId)); //ignorei18n_start //ignorei18n_end

        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found")); //ignorei18n_start //ignorei18n_end

        LocalDate effectiveFrom = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        if (salaryStructureRepository.existsByEmployeeIdAndEffectiveFrom(employeeId, effectiveFrom)) {
            SalaryStructure existing = salaryStructureRepository
                    .findPendingStructures(employeeId, LocalDate.now())
                    .stream()
                    .filter(s -> s.getEffectiveFrom().equals(effectiveFrom))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Pending salary structure not found")); //ignorei18n_start //ignorei18n_end

            existing.setBasicSalary(request.getBasicSalary());
            existing.setHra(request.getHra());
            existing.setAllowances(request.getAllowances());
            existing.setPfDeduction(request.getPfDeduction());
            existing.setTaxDeduction(request.getTaxDeduction());
            existing.setInsuranceDeduction(request.getInsuranceDeduction());

            existing = salaryStructureRepository.save(existing);
            return toResponse(existing, false);
        }

        SalaryStructure salaryStructure = SalaryStructure.builder()
                .employee(employee)
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .allowances(request.getAllowances())
                .pfDeduction(request.getPfDeduction())
                .taxDeduction(request.getTaxDeduction())
                .insuranceDeduction(request.getInsuranceDeduction())
                .effectiveFrom(effectiveFrom)
                .createdBy(employer)
                .build();

        salaryStructure = salaryStructureRepository.save(salaryStructure);
        return toResponse(salaryStructure, false);
    }

    private SalaryResponse toResponse(SalaryStructure structure, boolean active) {
        BigDecimal totalEarnings = structure.getBasicSalary()
                .add(structure.getHra())
                .add(structure.getAllowances());
        BigDecimal totalDeductions = structure.getPfDeduction()
                .add(structure.getTaxDeduction())
                .add(structure.getInsuranceDeduction());
        BigDecimal netPay = totalEarnings.subtract(totalDeductions);

        return SalaryResponse.builder()
                .id(structure.getId())
                .employeeId(structure.getEmployee().getId())
                .basicSalary(structure.getBasicSalary())
                .hra(structure.getHra())
                .allowances(structure.getAllowances())
                .pfDeduction(structure.getPfDeduction())
                .taxDeduction(structure.getTaxDeduction())
                .insuranceDeduction(structure.getInsuranceDeduction())
                .totalEarnings(totalEarnings)
                .totalDeductions(totalDeductions)
                .netPay(netPay)
                .effectiveFrom(structure.getEffectiveFrom())
                .createdAt(structure.getCreatedAt())
                .active(active)
                .build();
    }
}
