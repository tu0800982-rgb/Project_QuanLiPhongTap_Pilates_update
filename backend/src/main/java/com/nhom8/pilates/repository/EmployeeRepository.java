package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.nhom8.pilates.enums.RecordStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserId(Long userId);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone,Long id);
    boolean existsByEmployeeCodeAndIdNot(String employeeCode,Long id);
    long countByStatus(RecordStatus status);
}
