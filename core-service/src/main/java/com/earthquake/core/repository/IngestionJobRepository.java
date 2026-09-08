package com.earthquake.core.repository;

import com.earthquake.core.domain.IngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionJobRepository extends JpaRepository<IngestionJob, Long> {
}
