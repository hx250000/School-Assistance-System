package org.example.back.repository;

import org.example.back.entity.TaskParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskParticipantRepository extends JpaRepository<TaskParticipant, Long> {
    List<TaskParticipant> findByTaskId(Long taskId);
    List<TaskParticipant> findByUserId(Long userId);
}
