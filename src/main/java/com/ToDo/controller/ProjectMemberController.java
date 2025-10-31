package com.ToDo.controller;

import com.ToDo.domain.Role;
import com.ToDo.dto.ProjectMemberRequest;
import com.ToDo.dto.ProjectMemberResponse;
import com.ToDo.dto.ProjectResponse;
import com.ToDo.dto.UserResponse;
import com.ToDo.service.ProjectMemberService;
import com.ToDo.service.ProjectService;
import com.ToDo.service.UserService;
import com.ToDo.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/project-members")
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;
    private final ProjectService projectService;
    private final UserService userService;

    // 프로젝트 멤버 목록 (권한 제한 없음)
    @GetMapping("/list")
    public String memberList(@RequestParam Long projectId, Model model) {
        System.out.println("=== 멤버 목록 조회 시작 ===");
        System.out.println("projectId: " + projectId);

        ProjectResponse project = projectService.getProjectById(projectId);
        System.out.println("프로젝트 조회 완료: " + project.getTitle());

        List<ProjectMemberResponse> members = projectMemberService.getProjectMembers(projectId);
        System.out.println("멤버 조회 완료: " + members.size() + "명");

        model.addAttribute("project", project);
        model.addAttribute("members", members);

        System.out.println("=== 멤버 목록 조회 완료 ===");

        return "project-member-list";
    }

    // 멤버 추가 폼 (VIEWER 제한)
    @GetMapping("/add")
    public String addForm(@RequestParam Long projectId,
                          HttpSession session,
                          Model model) {
        AuthUtil.chechNotViewer(session);

        ProjectResponse project = projectService.getProjectById(projectId);
        List<UserResponse> users = userService.getAllUsers();

        model.addAttribute("project", project);
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());

        return "project-member-add";
    }

    // 멤버 추가 처리 (VIEWER 제한)
    @PostMapping("/add")
    public String addMember(@ModelAttribute ProjectMemberRequest request,
                            HttpSession session,
                            Model model) {
        try {
            AuthUtil.chechNotViewer(session);
            projectMemberService.addMember(request);
            return "redirect:/project-members/list?projectId=" + request.getProjectId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", projectService.getProjectById(request.getProjectId()));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", Role.values());
            return "project-member-add";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", projectService.getProjectById(request.getProjectId()));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", Role.values());
            return "project-member-add";
        }
    }

    // 멤버 역할 변경 폼 (VIEWER 제한)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpSession session,
                           Model model) {
        AuthUtil.chechNotViewer(session);

        ProjectMemberResponse member = projectMemberService.getMemberById(id);

        model.addAttribute("member", member);
        model.addAttribute("roles", Role.values());

        return "project-member-edit";
    }

    // 멤버 역할 변경 처리 (VIEWER 제한)
    @PostMapping("/{id}/edit")
    public String updateMember(@PathVariable Long id,
                               @RequestParam String role,
                               HttpSession session,
                               Model model) {
        try {
            AuthUtil.chechNotViewer(session);
            ProjectMemberResponse member = projectMemberService.updateMemberRole(id, role);
            return "redirect:/project-members/list?projectId=" + member.getProjectId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("member", projectMemberService.getMemberById(id));
            model.addAttribute("roles", Role.values());
            return "project-member-edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("member", projectMemberService.getMemberById(id));
            model.addAttribute("roles", Role.values());
            return "project-member-edit";
        }
    }

    // 멤버 제거 (VIEWER 제한)
    @PostMapping("/{id}/remove")
    public String removeMember(@PathVariable Long id,
                               @RequestParam Long projectId,
                               HttpSession session) {
        AuthUtil.chechNotViewer(session);
        projectMemberService.removeMember(id);
        return "redirect:/project-members/list?projectId=" + projectId;
    }

}
