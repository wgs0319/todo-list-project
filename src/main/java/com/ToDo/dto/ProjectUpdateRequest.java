package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectUpdateRequest {
    @NotBlank(message = "프로젝트 제목은 필수입니다.")
    private String title;
    private String description;
}
