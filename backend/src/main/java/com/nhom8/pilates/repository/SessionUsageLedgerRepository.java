package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.SessionUsageLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionUsageLedgerRepository extends JpaRepository<SessionUsageLedger, Long> {
    boolean existsByRegistrationIdAndReason(Long registrationId, String reason);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(l.delta),0) from SessionUsageLedger l where l.attendance.id=:attendanceId")
    int sumDeltaByAttendanceId(@org.springframework.data.repository.query.Param("attendanceId") Long attendanceId);
}
