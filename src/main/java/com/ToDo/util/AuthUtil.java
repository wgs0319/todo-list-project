package com.ToDo.util;

import com.ToDo.domain.Role;
import com.ToDo.dto.UserResponse;
import jakarta.servlet.http.HttpSession;

public class AuthUtil {
    // 사용자 역할이 viewer가 아닌지 확인 (생성/수정/삭제 가능)
    public static void chechNotViewer(HttpSession session) {
        UserResponse loginUser = (UserResponse)  session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        if (loginUser.getRole() == Role.VIEWER) {
            throw new IllegalArgumentException("조회 권한만 있습니다. 생성/수정/삭제 권한이 없습니다.");
        }
    }

    // 관리자인지 확인
    public static void checkAdmin(HttpSession session) {
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        if (loginUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("관리자만 접근할 수 있습니다");
        }
    }

    // 현재 로그인한 사용자 가져오기
    public static UserResponse getLoginUser(HttpSession session) {
        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }
        return loginUser;
    }
}
