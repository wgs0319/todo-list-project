package com.ToDo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


// 회원가입 요청 DTO
@Getter @Setter
public class UserCreateRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
