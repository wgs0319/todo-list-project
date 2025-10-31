package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectMemberRequest {
    @NotNull(message = "프로젝트 id를 입력해 주세요.")
    private Long projectId;
    
    @NotBlank(message = "사용자 id를 입력해 주세요.")
    private String userId;

    private String role;    // ADMIN, MEMBER, VIEWER
}
