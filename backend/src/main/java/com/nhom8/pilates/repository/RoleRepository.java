package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.Role;
import com.nhom8.pilates.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
}
