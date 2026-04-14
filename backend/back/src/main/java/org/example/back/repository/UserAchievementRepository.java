package org.example.back.repository;

import org.example.back.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Integer> {

    List<UserAchievement> findByUserId(Long userId);
}
