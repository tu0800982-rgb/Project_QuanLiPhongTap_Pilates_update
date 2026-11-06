package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.ClassRegistration;
import com.nhom8.pilates.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ClassRegistrationRepository extends JpaRepository<ClassRegistration, Long> {
    long countByScheduleIdAndStatus(Long scheduleId, RegistrationStatus status);
    boolean existsByScheduleIdAndMemberIdAndStatus(Long scheduleId, Long memberId, RegistrationStatus status);

    @Query("select count(r) from ClassRegistration r where r.member.id=:memberId and r.status='REGISTERED' " +
            "and r.schedule.startsAt < :endsAt and r.schedule.endsAt > :startsAt")
    long countMemberConflicts(@Param("memberId") Long memberId, @Param("startsAt") Instant startsAt,
                              @Param("endsAt") Instant endsAt);

    @EntityGraph(attributePaths = {"schedule", "schedule.pilatesClass", "schedule.trainer", "member", "memberPackage"})
    List<ClassRegistration> findByMemberIdOrderByScheduleStartsAtDesc(Long memberId);

    @EntityGraph(attributePaths = {"schedule", "schedule.pilatesClass", "schedule.trainer", "member", "memberPackage"})
    List<ClassRegistration> findAllByOrderByRegisteredAtDesc();

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ClassRegistration r join fetch r.schedule s join fetch s.pilatesClass " +
            "join fetch r.member join fetch r.memberPackage where r.id=:id")
    Optional<ClassRegistration> findByIdForUpdate(@Param("id") Long id);

    @Query("select count(r) from ClassRegistration r where r.memberPackage.id=:memberPackageId " +
            "and r.status='REGISTERED' and r.schedule.startsAt > :now")
    long countReservedSessions(@Param("memberPackageId") Long memberPackageId, @Param("now") Instant now);

    @Query("select r from ClassRegistration r join fetch r.member join fetch r.memberPackage " +
            "where r.schedule.id=:scheduleId and r.status='REGISTERED' order by r.member.fullName")
    List<ClassRegistration> findAttendanceRoster(@Param("scheduleId") Long scheduleId);
    long countByRegisteredAtGreaterThanEqualAndRegisteredAtLessThan(Instant from,Instant to);
}
