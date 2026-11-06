package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.Attendance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Attendance a join fetch a.registration r join fetch r.memberPackage " +
            "where a.schedule.id=:scheduleId and a.member.id=:memberId")
    Optional<Attendance> findForUpdate(@Param("scheduleId") Long scheduleId, @Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"schedule", "schedule.pilatesClass", "member", "registration"})
    List<Attendance> findByMemberIdOrderByScheduleStartsAtDesc(Long memberId);
    List<Attendance> findByScheduleId(Long scheduleId);
    long countByMarkedAtGreaterThanEqualAndMarkedAtLessThan(Instant from,Instant to);
}
