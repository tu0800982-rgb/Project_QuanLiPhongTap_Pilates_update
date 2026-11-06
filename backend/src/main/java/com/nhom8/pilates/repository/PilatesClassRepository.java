package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.PilatesClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.nhom8.pilates.enums.RecordStatus;

public interface PilatesClassRepository extends JpaRepository<PilatesClass, Long> {
    Optional<PilatesClass> findByClassCode(String classCode);
    long countByStatus(RecordStatus status);
}
