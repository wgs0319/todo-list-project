package com.ToDo.repository;

import com.ToDo.domain.ProjectMember;
import com.ToDo.domain.ProjectMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // Fetch Join으로 User, Project 같이 조회
    @Query("SELECT pm FROM ProjectMember pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "WHERE pm.project.id = :projectId")
    // 프로젝트의 모든 멤버 조회
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "WHERE pm.project.id = :projectId AND pm.status = :status")
    // 프로젝트의 활성 멤버만 조회
    List<ProjectMember> findByProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") ProjectMemberStatus status
    );

    @Query("SELECT pm FROM ProjectMember pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    // 특정 사용자가 특정 프로젝트의 멤버인지 확인
    Optional<ProjectMember> findByProjectIdAndUserId(
            @Param("projectId") Long projectId,
            @Param("userId") String userId
    );

    @Query("SELECT pm FROM ProjectMember pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "WHERE pm.user.id = :userId")
    // 사용자가 속한 모든 프로젝트 조회
    List<ProjectMember> findByUserId(@Param("userId") String userId);

    // 프로젝트에 이미 멤버로 있는지 확인
    boolean existsByProjectIdAndUserId(Long projectId, String userId);
}
