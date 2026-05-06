package org.example.back.repository;

import org.example.back.entity.PointsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsLogRepository extends JpaRepository<PointsLog, Long> {

    // ================= 积分历史 =================
    List<PointsLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    // ================= 幂等防刷（关键修复） =================
    boolean existsByUserIdAndTitleAndChangeAmount(
            Long userId,
            String title,
            Integer changeAmount
    );
}