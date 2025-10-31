package com.ToDo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    // 비밀번호는 선택 사항(변경하지 않을 수도 있기 때문)
    private String password;
}
