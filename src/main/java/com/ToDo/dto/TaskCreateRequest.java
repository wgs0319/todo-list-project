package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class TaskCreateRequest {
    @NotBlank(message = "작업 제목을 입력해 주세요.")
    private String title;
    private String description;
    @NotNull(message = "프로젝트 id를 입력해 주세요.")
    private Long projectId;
    private String assigneeId;  // 담당자
    private String status;      // TODO, IN_PROGRESS, DONE
    private String priority;    // LOW, MEDIUM, HIGH
    private LocalDate dueDate;    // 마감일
}
