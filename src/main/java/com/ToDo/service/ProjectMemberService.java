package com.ToDo.service;

import com.ToDo.domain.*;
import com.ToDo.dto.ProjectMemberRequest;
import com.ToDo.dto.ProjectMemberResponse;
import com.ToDo.repository.ProjectMemberRepository;
import com.ToDo.repository.ProjectRepository;
import com.ToDo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 프로젝트에 멤버 추가
    @Transactional
    public ProjectMemberResponse addMember(ProjectMemberRequest request) {
        // 프로젝트 조회
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다"));
        
        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 이미 멤버인지 확인
        if (projectMemberRepository.existsByProjectIdAndUserId(request.getProjectId(), request.getUserId())) {
            throw new IllegalArgumentException("이미 프로젝트 멤버입니다");
        }

        // ProjectMember 생성
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(request.getRole() != null ? Role.valueOf(request.getRole()) : Role.MEMBER)
                .status(ProjectMemberStatus.ACTIVE)
                .build();

        ProjectMember savedMember = projectMemberRepository.save(member);

        // 디버깅 로그
        System.out.println("멤버 저장 완료:");
        System.out.println("ID: " + savedMember.getId());
        System.out.println("User: " + savedMember.getUser().getName());
        System.out.println("Project: " + savedMember.getProject().getTitle());
        System.out.println("Role: " + savedMember.getRole());
        System.out.println("Status: " + savedMember.getStatus());
        System.out.println("JoinedAt: " + savedMember.getJoinedAt());

        return ProjectMemberResponse.from(savedMember);
    }

    // 프로젝트의 모든 멤버 조회
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        // 디버깅 로그
        System.out.println("프로젝트 " + projectId + "의 멤버 수: " + members.size());
        members.forEach(m -> {
            System.out.println("  - User ID: " + m.getUser().getId());
            System.out.println("  - User Name: " + m.getUser().getName());
            System.out.println("  - Project ID: " + m.getProject().getId());
            System.out.println("  - Project Title: " + m.getProject().getTitle());
        });

        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());
    }

    // 프로젝트의 활성 멤버만 조회
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getActiveProjectMemners(Long projectId) {
        return projectMemberRepository.findByProjectIdAndStatus(projectId, ProjectMemberStatus.ACTIVE)
                .stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());
    }

    // 사용자가 속한 프로젝트 목록
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getUserProjects(String userId) {
        return projectMemberRepository.findByUserId(userId).stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());
    }

    // 멤버 역할 변경
    @Transactional
    public ProjectMemberResponse updateMemberRole(Long memberId, String role) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다"));

        member.setRole(Role.valueOf(role));
        ProjectMember updatedMember = projectMemberRepository.save(member);
        return ProjectMemberResponse.from(updatedMember);
    }

    // 멤버 상태 변경 (비활성화)
    @Transactional
    public void deactivateMember(Long memberId) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다"));

        member.setStatus(ProjectMemberStatus.INACTIVE);
        projectMemberRepository.save(member);
    }

    // 멤버 제거
    @Transactional
    public void removeMember(Long memberId) {
        if (!projectMemberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("멤버를 찾을 수 없습니다");
        }
        projectMemberRepository.deleteById(memberId);
    }
    
    // 특정 멤버 정보 조회
    @Transactional(readOnly = true)
    public ProjectMemberResponse getMemberById(Long memberId) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다"));
        return ProjectMemberResponse.from(member);
    }
}
