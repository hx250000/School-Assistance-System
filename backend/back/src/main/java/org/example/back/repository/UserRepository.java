package org.example.back.repository;

import org.example.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameOrPhone(String username, String phone); // 登录用


    User findByPhone(String phone);
    boolean existsByUsernameOrPhone(String username, String phone);
}