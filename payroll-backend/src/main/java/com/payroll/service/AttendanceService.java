package com.payroll.service;

import com.payroll.dto.request.AttendanceRequest;
import com.payroll.dto.response.AttendanceResponse;
import com.payroll.entity.Attendance;
import com.payroll.entity.Employee;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public AttendanceResponse createOrUpdateAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate())
                .orElse(Attendance.builder()
                        .employee(employee)
                        .date(request.getDate())
                        .build());

        attendance.setCheckIn(request.getCheckIn());
        attendance.setCheckOut(request.getCheckOut());

        attendance = attendanceRepository.save(attendance);
        return toResponse(attendance);
    }

    public List<AttendanceResponse> getWeeklyAttendance(Long employeeId, LocalDate weekOf) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        LocalDate startOfWeek = weekOf.minusDays(weekOf.getDayOfWeek().getValue() - 1); // Monday
        LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday

        List<Attendance> records = attendanceRepository
                .findByEmployeeIdAndDateBetweenOrderByDateAsc(employeeId, startOfWeek, endOfWeek);

        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AttendanceResponse toResponse(Attendance attendance) {
        String hoursWorked = null;
        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            Duration duration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            hoursWorked = hours + "h " + minutes + "m";
        }

        Employee employee = attendance.getEmployee();
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeCode(employee.getEmployeeCode())
                .date(attendance.getDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .hoursWorked(hoursWorked)
                .build();
    }
}
