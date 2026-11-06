package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.ClassSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ClassSchedule s join fetch s.pilatesClass c join fetch c.classType join fetch s.trainer where s.id=:id")
    Optional<ClassSchedule> findByIdForUpdate(@Param("id") Long id);

    @Query("select s from ClassSchedule s join fetch s.pilatesClass c join fetch c.classType join fetch s.trainer where s.id=:id")
    Optional<ClassSchedule> findDetailedById(@Param("id") Long id);

    @Query("select s from ClassSchedule s join fetch s.pilatesClass c join fetch c.classType join fetch s.trainer " +
            "where s.startsAt >= :from order by s.startsAt")
    List<ClassSchedule> findUpcoming(@Param("from") Instant from);

    @Query("select count(s) from ClassSchedule s where s.trainer.id=:trainerId and s.status <> 'CANCELLED' " +
            "and s.startsAt < :endsAt and s.endsAt > :startsAt")
    long countTrainerConflicts(@Param("trainerId") Long trainerId, @Param("startsAt") Instant startsAt,
                               @Param("endsAt") Instant endsAt);

    @Query("select count(s) from ClassSchedule s where s.trainer.id=:trainerId and s.id<>:scheduleId and s.status <> 'CANCELLED' " +
            "and s.startsAt < :endsAt and s.endsAt > :startsAt")
    long countTrainerConflictsExcluding(@Param("trainerId") Long trainerId,@Param("scheduleId") Long scheduleId,
                                        @Param("startsAt") Instant startsAt,@Param("endsAt") Instant endsAt);

    @Query("select s from ClassSchedule s where s.startsAt>=:from and s.startsAt<:to and s.status<>'CANCELLED'")
    List<ClassSchedule> findForReport(@Param("from") Instant from,@Param("to") Instant to);
    Optional<ClassSchedule> findFirstByPilatesClassIdAndStartsAtAfterAndStatusNotOrderByStartsAt(
            Long classId,Instant startsAt,com.nhom8.pilates.enums.ScheduleStatus status);
}
