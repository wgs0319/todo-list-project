package com.ToDo.controller;

import com.ToDo.domain.Role;
import com.ToDo.dto.UserCreateRequest;
import com.ToDo.dto.UserResponse;
import com.ToDo.dto.UserUpdateRequest;
import com.ToDo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 폼 페이지
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";  // templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String createUser(@ModelAttribute UserCreateRequest request, Model model) {
        try {
            userService.createUser(request);
            return "redirect:/users/list";  // 성공 시 목록으로
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";  // 실패 시 다시 폼으로
        }
    }

    // 사용자 목록 페이지
    @GetMapping("/list")
    public String userList(Model model) {
        List<UserResponse> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user-list";
    }

    // 특정 사용자 상세 페이지
    @GetMapping("/{id}")
    public String userDetail(@PathVariable("id") String userId, Model model) {
        UserResponse user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "user-detail";
    }

    // 사용자 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        UserResponse user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-edit";
    }

    // 사용자 수정 처리
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable String id,
                             @ModelAttribute UserUpdateRequest request,
                             Model model) {
        try {
            userService.updateUser(id, request);
            return "redirect:/users/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userService.getUserById(id));
            return "user-edit";
        }
    }

    // 사용자 삭제
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/users/list";
    }

    // 역할 변경 (관리자 기능)
    @PostMapping("/{id}/change-role")
    public String changeUserRole(@PathVariable String id,
                                 @RequestParam String role,
                                 HttpSession session) {
        // 로그인 확인
        UserResponse loginUser = (UserResponse)  session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 관리자 권한 확인
        if (loginUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("관리자만 역할을 변경할 수 있습니다");
        }

        userService.updateUserRole(id, role);
        return "redirect:/users/list";
    }
}