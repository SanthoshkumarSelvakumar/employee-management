package com.payroll.service;

import com.payroll.dto.response.PayslipResponse;
import com.payroll.entity.Employee;
import com.payroll.entity.Payslip;
import com.payroll.entity.SalaryStructure;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayslipRepository;
import com.payroll.repository.SalaryStructureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PayslipService {

    private static final Logger LOGGER = Logger.getLogger(PayslipService.class.getName());

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    public PayslipService(PayslipRepository payslipRepository,
                          EmployeeRepository employeeRepository,
                          SalaryStructureRepository salaryStructureRepository) {
        this.payslipRepository = payslipRepository;
        this.employeeRepository = employeeRepository;
        this.salaryStructureRepository = salaryStructureRepository;
    }

    public Page<PayslipResponse> getEmployeePayslips(Long employeeId, Pageable pageable) {
        return payslipRepository
                .findByEmployeeIdOrderByPayPeriodYearDescPayPeriodMonthDesc(employeeId, pageable)
                .map(this::toResponse);
    }

    public Page<PayslipResponse> getAllPayslips(Pageable pageable) {
        return payslipRepository
                .findAllByOrderByPayPeriodYearDescPayPeriodMonthDesc(pageable)
                .map(this::toResponse);
    }

    public PayslipResponse getPayslipById(Long id) {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id: " + id)); //ignorei18n_start //ignorei18n_end
        return toResponse(payslip);
    }

    public Payslip getPayslipEntity(Long id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id: " + id)); //ignorei18n_start //ignorei18n_end
    }

    public List<PayslipResponse> getPayslipsByEmployeeAndYear(Long employeeId, Integer year) {
        return payslipRepository.findByEmployeeIdAndYear(employeeId, year)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void generateMonthlyPayslips(int month, int year) {
        LocalDate payPeriod = LocalDate.of(year, month, 1);
        List<Employee> activeEmployees = employeeRepository
                .findByStatus(Employee.Status.ACTIVE, Pageable.unpaged())
                .getContent();

        int generated = 0;
        int skipped = 0;

        for (Employee employee : activeEmployees) {
            if (payslipRepository.existsByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
                    employee.getId(), month, year)) {
                skipped++;
                continue;
            }

            SalaryStructure activeSalary = salaryStructureRepository
                    .findActiveStructure(employee.getId(), payPeriod)
                    .orElse(null);

            if (activeSalary == null) {
                LOGGER.warning("No salary structure found for employee: " //ignorei18n_start //ignorei18n_end
                        + employee.getEmployeeCode() + " for period " + month + "/" + year); //ignorei18n_start //ignorei18n_end
                skipped++;
                continue;
            }

            BigDecimal totalEarnings = activeSalary.getBasicSalary()
                    .add(activeSalary.getHra())
                    .add(activeSalary.getAllowances());
            BigDecimal totalDeductions = activeSalary.getPfDeduction()
                    .add(activeSalary.getTaxDeduction())
                    .add(activeSalary.getInsuranceDeduction());
            BigDecimal netPay = totalEarnings.subtract(totalDeductions);

            Payslip payslip = Payslip.builder()
                    .employee(employee)
                    .salaryStructure(activeSalary)
                    .payPeriodMonth(month)
                    .payPeriodYear(year)
                    .basicSalary(activeSalary.getBasicSalary())
                    .hra(activeSalary.getHra())
                    .allowances(activeSalary.getAllowances())
                    .totalEarnings(totalEarnings)
                    .pfDeduction(activeSalary.getPfDeduction())
                    .taxDeduction(activeSalary.getTaxDeduction())
                    .insuranceDeduction(activeSalary.getInsuranceDeduction())
                    .totalDeductions(totalDeductions)
                    .netPay(netPay)
                    .status(Payslip.Status.GENERATED)
                    .build();

            payslipRepository.save(payslip);
            generated++;
        }

        LOGGER.info("Payslip generation complete for " + month + "/" + year //ignorei18n_start //ignorei18n_end
                + ": generated=" + generated + ", skipped=" + skipped); //ignorei18n_start //ignorei18n_end
    }

    private PayslipResponse toResponse(Payslip payslip) {
        Employee employee = payslip.getEmployee();
        return PayslipResponse.builder()
                .id(payslip.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName()) //ignorei18n_start //ignorei18n_end
                .employeeCode(employee.getEmployeeCode())
                .department(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .designation(employee.getDesignation())
                .payPeriodMonth(payslip.getPayPeriodMonth())
                .payPeriodYear(payslip.getPayPeriodYear())
                .basicSalary(payslip.getBasicSalary())
                .hra(payslip.getHra())
                .allowances(payslip.getAllowances())
                .totalEarnings(payslip.getTotalEarnings())
                .pfDeduction(payslip.getPfDeduction())
                .taxDeduction(payslip.getTaxDeduction())
                .insuranceDeduction(payslip.getInsuranceDeduction())
                .totalDeductions(payslip.getTotalDeductions())
                .netPay(payslip.getNetPay())
                .generatedAt(payslip.getGeneratedAt())
                .status(payslip.getStatus().name())
                .build();
    }
}
