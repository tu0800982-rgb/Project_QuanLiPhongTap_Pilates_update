package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
    Optional<ClassType> findByCode(String code);
}
