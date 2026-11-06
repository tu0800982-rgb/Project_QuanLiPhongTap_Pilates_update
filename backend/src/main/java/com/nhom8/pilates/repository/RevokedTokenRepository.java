package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken,Long> {
    boolean existsByTokenId(String tokenId);
}
