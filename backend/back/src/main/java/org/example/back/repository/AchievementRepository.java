package org.example.back.repository;

import org.example.back.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Long> {
    
    // 查询所有活跃成就
    List<Achievement> findByIsActiveTrue();
}
