package com.ToDo.dto;

import com.ToDo.domain.ProjectMember;
import com.ToDo.domain.ProjectMemberStatus;
import com.ToDo.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class ProjectMemberResponse {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private String userId;
    private String userName;
    private String userEmail;
    private Role role;
    private ProjectMemberStatus status;
    private LocalDateTime joinedAt;

    public static ProjectMemberResponse from(ProjectMember member) {
        if (member == null) {
            throw new IllegalArgumentException("ProjectMember가 null입니다");
        }

        // 디버깅 로그
        System.out.println("ProjectMemberResponse 변환 중:");
        System.out.println("  Project: " + (member.getProject() != null ? member.getProject().getTitle() : "null"));
        System.out.println("  User: " + (member.getUser() != null ? member.getUser().getName() : "null"));

        return new ProjectMemberResponse(
                member.getId(),
                member.getProject() != null ? member.getProject().getId() : null,
                member.getProject() != null ? member.getProject().getTitle() : "알 수 없음",
                member.getUser() != null ? member.getUser().getId() : null,
                member.getUser() != null ? member.getUser().getName() : "알 수 없음",
                member.getUser() != null ? member.getUser().getEmail() : "알 수 없음",
                member.getRole(),
                member.getStatus(),
                member.getJoinedAt()
        );
    }
}
