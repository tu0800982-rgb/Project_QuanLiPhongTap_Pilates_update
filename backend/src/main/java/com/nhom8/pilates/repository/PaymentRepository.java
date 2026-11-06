package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import java.math.BigDecimal;
import com.nhom8.pilates.enums.PaymentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"member", "memberPackage", "memberPackage.pilatesPackage"})
    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Override
    @EntityGraph(attributePaths = {"member", "memberPackage", "memberPackage.pilatesPackage"})
    Optional<Payment> findById(Long id);
    @EntityGraph(attributePaths = {"member", "memberPackage", "memberPackage.pilatesPackage"})
    List<Payment> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant from,Instant to);
    @Query("select coalesce(sum(p.amount),0) from Payment p where p.status=:status and p.paidAt>=:from and p.paidAt<:to")
    BigDecimal sumAmountByStatusAndPaidAt(@Param("status") PaymentStatus status,
                                          @Param("from") Instant from,@Param("to") Instant to);
    @EntityGraph(attributePaths = {"member", "memberPackage", "memberPackage.pilatesPackage"})
    List<Payment> findTop5ByOrderByCreatedAtDesc();
    Optional<Payment> findByMemberPackageId(Long memberPackageId);
}
