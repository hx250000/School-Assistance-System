package org.example.back.repository;

import org.example.back.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUserId(Long userId);
    
    // 根据用户ID和成就ID查询用户成就
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);
    
    // 计算用户已解锁的成就数量
    long countByUserIdAndIsUnlockedTrue(Long userId);
}
