package com.ToDo.dto;

import com.ToDo.domain.Role;
import com.ToDo.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

// 회원가입 응답 DTO
@Getter @Setter
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 메서드
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
