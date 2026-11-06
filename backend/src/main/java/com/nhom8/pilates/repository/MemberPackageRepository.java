package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.MemberPackage;
import com.nhom8.pilates.enums.MemberPackageStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberPackageRepository extends JpaRepository<MemberPackage, Long> {
    @EntityGraph(attributePaths = {"pilatesPackage", "member"})
    List<MemberPackage> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct mp from MemberPackage mp join fetch mp.member join fetch mp.pilatesPackage p " +
            "left join fetch p.classTypes where mp.id=:id")
    Optional<MemberPackage> findEligibleByIdForUpdate(@Param("id") Long id);
    @EntityGraph(attributePaths = {"pilatesPackage", "member"})
    List<MemberPackage> findByMemberIdAndStatusOrderByEndDateDesc(Long memberId,MemberPackageStatus status);
}
