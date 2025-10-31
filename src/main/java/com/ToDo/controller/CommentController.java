package com.ToDo.controller;

import com.ToDo.dto.CommentCreateRequest;
import com.ToDo.dto.CommentResponse;
import com.ToDo.dto.CommentUpdateRequest;
import com.ToDo.service.CommentService;
import com.ToDo.service.TaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final TaskService taskService;

    // 댓글 작성 (task-detail 페이지에서 처리)
    @PostMapping("/create")
    public String createComment(@ModelAttribute CommentCreateRequest request,
                                HttpSession session) {
        // 현재 로그인한 사용자 ID 가져오기
        com.ToDo.dto.UserResponse loginUser =
                (com.ToDo.dto.UserResponse) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        commentService.createComment(request, loginUser.getId());
        return "redirect:/tasks/" + request.getTaskId();
    }

    // 댓글 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpSession session,
                           Model model) {
        com.ToDo.dto.UserResponse loginUser =
                (com.ToDo.dto.UserResponse) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        CommentResponse comment = commentService.getCommentById(id);
        
        // 작성자 본인 확인
        if (!comment.getAuthorId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다");
        }

        model.addAttribute("comment", comment);
        return "comment-edit";
    }

    // 댓글 수정 처리
    @PostMapping("/{id}/edit")
    public String updateComment(@PathVariable Long id,
                                @ModelAttribute CommentUpdateRequest request,
                                HttpSession session,
                                Model model) {
        com.ToDo.dto.UserResponse loginUser =
                (com.ToDo.dto.UserResponse) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        try {
            CommentResponse comment = commentService.updateComment(id, request, loginUser.getId());
            return "redirect:/tasks/" + comment.getTaskId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("comment", commentService.getCommentById(id));
            return "comment-edit";
        }
    }

    // 댓글 삭제
    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id,
                                @RequestParam Long taskId,
                                HttpSession session) {
        com.ToDo.dto.UserResponse loginUser =
                (com.ToDo.dto.UserResponse) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        commentService.deleteComment(id, loginUser.getId());
        return "redirect:/tasks/" + taskId;
    }
}
