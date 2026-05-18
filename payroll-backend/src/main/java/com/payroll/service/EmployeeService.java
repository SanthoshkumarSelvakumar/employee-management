package com.payroll.service;

import com.payroll.dto.request.EmployeeRequest;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.entity.Department;
import com.payroll.entity.Employee;
import com.payroll.entity.SalaryStructure;
import com.payroll.entity.User;
import com.payroll.exception.BusinessException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.SalaryStructureRepository;
import com.payroll.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.security.SecureRandom;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EmployeeService(EmployeeRepository employeeRepository,
                           UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           SalaryStructureRepository salaryStructureRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.salaryStructureRepository = salaryStructureRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<EmployeeResponse> getAllEmployees(String search, Pageable pageable) {
        Page<Employee> employees;
        if (search != null && !search.isBlank()) {
            employees = employeeRepository.searchEmployees(search, pageable);
        } else {
            employees = employeeRepository.findByStatus(Employee.Status.ACTIVE, pageable);
        }
        return employees.map(this::toResponse);
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id)); //ignorei18n_start //ignorei18n_end
        return toResponse(employee);
    }

    public EmployeeResponse getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for email: " + email)); //ignorei18n_start //ignorei18n_end
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User with email '" + request.getEmail() + "' already exists"); //ignorei18n_start //ignorei18n_end
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId())); //ignorei18n_start //ignorei18n_end

        String generatedPassword = generatePassword();

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(generatedPassword))
                .role(User.Role.ROLE_EMPLOYEE)
                .isActive(true)
                .build();
        user = userRepository.save(user);

        String employeeCode = generateEmployeeCode();

        Employee employee = Employee.builder()
                .user(user)
                .employeeCode(employeeCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .department(department)
                .designation(request.getDesignation())
                .dateOfJoining(request.getDateOfJoining())
                .status(Employee.Status.ACTIVE)
                .build();
        employee = employeeRepository.save(employee);

        // Create initial salary structure effective from current month
        SalaryStructure salary = SalaryStructure.builder()
                .employee(employee)
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .allowances(request.getAllowances())
                .pfDeduction(request.getPfDeduction())
                .taxDeduction(request.getTaxDeduction())
                .insuranceDeduction(request.getInsuranceDeduction())
                .effectiveFrom(LocalDate.now().withDayOfMonth(1))
                .createdBy(user)
                .build();
        salaryStructureRepository.save(salary);

        EmployeeResponse response = toResponse(employee);
        response.setTemporaryPassword(generatedPassword);
        return response;
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id)); //ignorei18n_start //ignorei18n_end

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId())); //ignorei18n_start //ignorei18n_end

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDepartment(department);
        employee.setDesignation(request.getDesignation());
        employee.setDateOfJoining(request.getDateOfJoining());

        employee = employeeRepository.save(employee);
        return toResponse(employee);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id)); //ignorei18n_start //ignorei18n_end

        employee.setStatus(Employee.Status.INACTIVE);
        employee.getUser().setIsActive(false);
        employeeRepository.save(employee);
    }

    public long getActiveEmployeeCount() {
        return employeeRepository.countByStatus(Employee.Status.ACTIVE);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getUser().getEmail())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .designation(employee.getDesignation())
                .dateOfJoining(employee.getDateOfJoining())
                .status(employee.getStatus().name())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    private String generateEmployeeCode() {
        String code;
        do {
            code = "EMP" + String.format("%05d", SECURE_RANDOM.nextInt(100000)); //ignorei18n_start //ignorei18n_end
        } while (employeeRepository.existsByEmployeeCode(code));
        return code;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }
}
