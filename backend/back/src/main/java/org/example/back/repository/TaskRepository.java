package org.example.back.repository;

import org.example.back.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Query("""
        UPDATE Task t
        SET t.currentPeople = t.currentPeople + 1
        WHERE t.id = :taskId
          AND t.currentPeople < t.needPeople
    """)
    int incrementIfNotFull(@Param("taskId")Long taskId);

    @Modifying
    @Query("""
        UPDATE Task t 
        SET t.currentPeople = t.currentPeople - 1 
        WHERE t.id = :taskId 
          AND t.currentPeople > 0""")
    int decrementIfNotEmpty(@Param("taskId")Long taskId);

    @Modifying
    @Query("""
    UPDATE Task t
    SET t.status = 'IN_PROGRESS'
    WHERE t.id = :taskId
      AND t.currentPeople >= t.needPeople
    """)
    int updateStatusIfFull(@Param("taskId") Long taskId);

    List<Task> findByStatus(String status);

    List<Task> findByPublisherId(Long publisherId);

    List<Task> findByTitleLike(String keyword);

}
