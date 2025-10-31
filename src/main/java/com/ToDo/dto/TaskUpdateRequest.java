package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class TaskUpdateRequest {
    @NotBlank(message = "작업 제목을 입력해 주세요.")
    private String title;
    private String description;
    private String assigneeId;  // 담당자
    private String status;      // TODO, IN_PROGRESS, DONE
    private String priority;    // LOW, MEDIUM, HIGH
    private LocalDate dueDate;     // 마감일
}
