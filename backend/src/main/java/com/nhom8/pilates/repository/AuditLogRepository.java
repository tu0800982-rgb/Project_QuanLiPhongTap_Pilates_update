package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.AuditLog;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    @EntityGraph(attributePaths = "actor")
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
