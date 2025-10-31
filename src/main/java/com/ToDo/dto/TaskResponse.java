package com.ToDo.dto;

import com.ToDo.domain.Task;
import com.ToDo.domain.TaskPriority;
import com.ToDo.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;

    // 프로젝트 정보
    private Long projectId;
    private String projectTitle;

    // 담당자 정보
    private String assigneeId;
    private String assigneeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long commentCount;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getProject() != null ? task.getProject().getTitle() : null,
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                0   // 기본값 0
        );
    }

    // 댓글 개수 포함하는 생성자
    public static TaskResponse fromWithCommentCount(Task task, long commentCount) {
        TaskResponse response = from(task);
        response.setCommentCount(commentCount);
        return response;
    }
}
