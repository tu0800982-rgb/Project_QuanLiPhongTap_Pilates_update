package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.nhom8.pilates.enums.RecordStatus;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);
    boolean existsByMemberCode(String memberCode);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone,Long id);
    Page<Member> findByFullNameContainingIgnoreCaseOrMemberCodeContainingIgnoreCaseOrPhoneContaining(
            String fullName, String memberCode, String phone, Pageable pageable);
    long countByStatus(RecordStatus status);
}
