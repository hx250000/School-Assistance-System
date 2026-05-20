package org.example.back.repository;

import org.example.back.entity.TaskParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskParticipantRepository extends JpaRepository<TaskParticipant, Long> {
    List<TaskParticipant> findByTaskId(Long taskId);
    List<TaskParticipant> findByUserId(Long userId);
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
    Optional<TaskParticipant> findByTaskIdAndUserId(Long taskId, Long userId);
    long countByUserIdAndStatus(Long userId, String status);

    @Modifying
    @Query("DELETE FROM TaskParticipant p WHERE p.taskId = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);


}
