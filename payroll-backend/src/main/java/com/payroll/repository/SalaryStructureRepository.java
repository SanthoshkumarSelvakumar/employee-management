package com.payroll.repository;

import com.payroll.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    @Query("SELECT s FROM SalaryStructure s WHERE s.employee.id = :employeeId " +
           "AND s.effectiveFrom <= :date ORDER BY s.effectiveFrom DESC LIMIT 1")
    Optional<SalaryStructure> findActiveStructure(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date);

    @Query("SELECT s FROM SalaryStructure s WHERE s.employee.id = :employeeId " +
           "ORDER BY s.effectiveFrom DESC")
    List<SalaryStructure> findAllByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT s FROM SalaryStructure s WHERE s.employee.id = :employeeId " +
           "AND s.effectiveFrom > :date ORDER BY s.effectiveFrom ASC")
    List<SalaryStructure> findPendingStructures(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date);

    boolean existsByEmployeeIdAndEffectiveFrom(Long employeeId, LocalDate effectiveFrom);
}
