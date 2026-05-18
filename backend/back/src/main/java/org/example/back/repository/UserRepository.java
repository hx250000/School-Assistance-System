package org.example.back.repository;

import org.example.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameOrPhone(String username, String phone); // 登录用


    User findByPhone(String phone);
    boolean existsByUsernameOrPhone(String username, String phone);

    @Modifying
    @Query("UPDATE User u SET u.points = u.points - :amount WHERE u.id = :userId AND u.points >= :amount")
    int decreasePoints(@Param("userId") Long userId, @Param("amount")Integer amount);

    @Modifying
    @Query("UPDATE User u SET u.points = u.points + :amount WHERE u.id = :userId")
    int increasePoints(@Param("userId")Long userId, @Param("amount")Integer amount);
}