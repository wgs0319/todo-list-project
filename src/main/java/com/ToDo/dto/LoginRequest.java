package com.ToDo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String id;
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}