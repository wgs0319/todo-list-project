package com.ToDo.repository;

import com.ToDo.domain.Task;
import com.ToDo.domain.TaskPriority;
import com.ToDo.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 프로젝트 별 작업 조회
    List<Task> findByProjectId(Long projectId);
    // 담당자 별 작업 조회
    List<Task> findByAssigneeId(String assigneeId);
    // 상태 별 작업 조회
    List<Task> findByStatus(TaskStatus status);
    // 프로젝트 및 상태 별 작업 조회
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    // 제목으로 검색 (부분 일치)
    List<Task> findByTitleContaining(String title);
    // 우선순위별 조회
    List<Task> findByPriority(TaskPriority priority);
    
    // 복합 검색 (프로젝트, 상태, 우선순위, 담당자)
    @Query("SELECT t FROM Task t WHERE " +
            "(:projectId IS NULL OR t.project.id = :projectId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
            "(:keyword IS NULL OR t.title LIKE %:keyword%)")
    List<Task> searchTasks(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("assigneeId") String assigneeId,
            @Param("keyword") String keyword
    );
}
