package com.ToDo.controller;

import com.ToDo.dto.LoginRequest;
import com.ToDo.dto.UserResponse;
import com.ToDo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpSession session,
                        Model model) {
        try {
            UserResponse user = userService.login(request.getId(), request.getPassword());

            // 세션에 사용자 정보 저장
            session.setAttribute("loginUser", user);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 세션 무효화
        return "redirect:/login";
    }
}
