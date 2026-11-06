package com.nhom8.pilates.repository;

import com.nhom8.pilates.entity.PilatesPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import com.nhom8.pilates.enums.RecordStatus;

public interface PilatesPackageRepository extends JpaRepository<PilatesPackage, Long> {
    Optional<PilatesPackage> findByPackageCode(String packageCode);
    boolean existsByPackageCodeAndIdNot(String packageCode,Long id);
    @Query("select distinct p from PilatesPackage p left join fetch p.classTypes")
    List<PilatesPackage> findAllWithClassTypes();
    long countByStatus(RecordStatus status);
}
