package org.example.back.repository;

import org.example.back.entity.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    Optional<LoginRecord> findByUserIdAndLoginDate(Long userId, LocalDate loginDate);
    List<LoginRecord> findByUserIdOrderByLoginDateDesc(Long userId);
}
