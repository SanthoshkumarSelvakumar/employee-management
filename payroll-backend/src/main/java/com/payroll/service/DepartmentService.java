package com.payroll.service;

import com.payroll.dto.request.DepartmentRequest;
import com.payroll.dto.response.DepartmentResponse;
import com.payroll.entity.Department;
import com.payroll.exception.BusinessException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id)); //ignorei18n_start //ignorei18n_end
        return toResponse(department);
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new BusinessException("Department with name '" + request.getName() + "' already exists"); //ignorei18n_start //ignorei18n_end
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        department = departmentRepository.save(department);
        return toResponse(department);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id)); //ignorei18n_start //ignorei18n_end

        departmentRepository.findByName(request.getName())
                .filter(d -> !d.getId().equals(id))
                .ifPresent(d -> {
                    throw new BusinessException("Department with name '" + request.getName() + "' already exists"); //ignorei18n_start //ignorei18n_end
                });

        department.setName(request.getName());
        department.setDescription(request.getDescription());

        department = departmentRepository.save(department);
        return toResponse(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id)); //ignorei18n_start //ignorei18n_end

        long employeeCount = employeeRepository.countByDepartmentId(id);
        if (employeeCount > 0) {
            throw new BusinessException("Cannot delete department with " + employeeCount + " assigned employees"); //ignorei18n_start //ignorei18n_end
        }

        departmentRepository.delete(department);
    }

    private DepartmentResponse toResponse(Department department) {
        long employeeCount = employeeRepository.countByDepartmentId(department.getId());
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .employeeCount(employeeCount)
                .createdAt(department.getCreatedAt())
                .build();
    }
}
