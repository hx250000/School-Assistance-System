package org.example.back.repository;

import org.example.back.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ================= 抢任务 =================
    @Modifying
    @Query("""
        UPDATE Task t
        SET t.currentPeople = t.currentPeople + 1
        WHERE t.id = :taskId
          AND t.currentPeople < t.needPeople
    """)
    int incrementIfNotFull(@Param("taskId") Long taskId);

    @Modifying
    @Query("""
        UPDATE Task t 
        SET t.currentPeople = t.currentPeople - 1 
        WHERE t.id = :taskId 
          AND t.currentPeople > 0
    """)
    int decrementIfNotEmpty(@Param("taskId") Long taskId);

    @Modifying
    @Query("""
        UPDATE Task t
        SET t.status = 'IN_PROGRESS'
        WHERE t.id = :taskId
          AND t.currentPeople >= t.needPeople
    """)
    int updateStatusIfFull(@Param("taskId") Long taskId);

    // ================= ✅ 防重复完成（你缺的） =================
    @Modifying
    @Query("""
        UPDATE Task t
        SET t.status = 'FINISHED'
        WHERE t.id = :taskId
          AND t.status = 'IN_PROGRESS'
    """)
    int updateStatusToFinishedIfInProgress(@Param("taskId") Long taskId);

    // ================= 查询 =================

    List<Task> findByStatus(String status);

    List<Task> findByPublisherId(Long publisherId);

    // ❌ 原来的（有风险，建议删除）
    // List<Task> findByTitleLike(String keyword);

    // ✅ 推荐：安全版本（自动参数绑定）
    List<Task> findByTitleContaining(String keyword);

    // 分页
    Page<Task> findAllByStatus(String status, Pageable pageable);
}