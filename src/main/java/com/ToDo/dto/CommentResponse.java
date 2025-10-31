package com.ToDo.dto;

import com.ToDo.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;

    // Task 정보
    private Long taskId;
    private String TaskTitle;

    // 작성자 정보
    private String authorId;
    private String authorName;

    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTask() != null ? comment.getTask().getId() : null,
                comment.getTask() != null ? comment.getTask().getTitle() : null,
                comment.getAuthor() != null ? comment.getAuthor().getId() : null,
                comment.getAuthor() != null ? comment.getAuthor().getName() : null,
                comment.getCreatedAt()
        );
    }
}
