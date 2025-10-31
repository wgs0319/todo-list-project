package com.ToDo.repository;

import com.ToDo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Task의 모든 댓글 조회(최신순)
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +
            "JOIN FETCH c.task " +
            "WHERE c.task.id = :taskId " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    // 사용자가 작성한 모든 댓글
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +
            "JOIN FETCH c.task " +
            "WHERE c.author.id = :authorId " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findByAuthorId(@Param("authorId") String authorId);

    // Task의 댓글 개수
    long countByTaskId(Long taskId);
}
