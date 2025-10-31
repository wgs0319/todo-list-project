package com.ToDo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentCreateRequest {
    @NotNull(message = "작업 ID를 입력해 주세요.")
    private Long taskId;

    @NotNull(message = "댓글 내용을 입력해 주세요.")
    private String content;
}
