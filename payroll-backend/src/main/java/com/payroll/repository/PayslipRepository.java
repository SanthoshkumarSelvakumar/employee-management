package com.payroll.repository;

import com.payroll.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    Page<Payslip> findByEmployeeIdOrderByPayPeriodYearDescPayPeriodMonthDesc(
            Long employeeId, Pageable pageable);

    @Query("SELECT p FROM Payslip p WHERE p.employee.id = :employeeId " +
           "AND p.payPeriodYear = :year ORDER BY p.payPeriodMonth DESC")
    List<Payslip> findByEmployeeIdAndYear(
            @Param("employeeId") Long employeeId,
            @Param("year") Integer year);

    Optional<Payslip> findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
            Long employeeId, Integer month, Integer year);

    boolean existsByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
            Long employeeId, Integer month, Integer year);

    @Query("SELECT p FROM Payslip p JOIN FETCH p.employee e " +
           "WHERE p.payPeriodMonth = :month AND p.payPeriodYear = :year " +
           "ORDER BY e.lastName, e.firstName")
    List<Payslip> findAllByPeriod(
            @Param("month") Integer month,
            @Param("year") Integer year);

    Page<Payslip> findAllByOrderByPayPeriodYearDescPayPeriodMonthDesc(Pageable pageable);

    long countByPayPeriodMonthAndPayPeriodYear(Integer month, Integer year);
}
