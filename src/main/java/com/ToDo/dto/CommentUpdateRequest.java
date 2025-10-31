package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentUpdateRequest {
    @NotBlank(message = "댓글 내용을 입력해 주세요.")
    private String content;
}
