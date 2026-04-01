package org.example.back.repository;

import org.example.back.entity.PointsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsLogRepository extends JpaRepository<PointsLog, Long> {
}
