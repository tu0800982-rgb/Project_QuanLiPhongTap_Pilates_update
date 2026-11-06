package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RefreshToken t join fetch t.user u join fetch u.role where t.tokenHash=:hash")
    Optional<RefreshToken> findByHashForUpdate(@Param("hash") String hash);
}
