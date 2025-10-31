package com.ToDo.controller;

import com.ToDo.dto.ProjectCreateRequest;
import com.ToDo.dto.ProjectResponse;
import com.ToDo.service.ProjectService;
import com.ToDo.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    // 프로젝트 목록(검색 포함, 권한 제한 없음)
    @GetMapping("/list")
    public String projectList(@RequestParam(required = false) String keyword, Model model) {
        List<ProjectResponse> projects;

        if(keyword != null && !keyword.isEmpty()) {
            projects = projectService.searchProjects(keyword);
        } else {
            projects = projectService.getAllProjects();
        }

        model.addAttribute("projects", projects);
        model.addAttribute("selectedKeyword", keyword);
        return "project-list";
    }

    // 프로잭트 생성 폼 (VIEWER 제한)
    @GetMapping("/create")
    public String createForm(HttpSession session) {
        AuthUtil.chechNotViewer(session);
        return "project-create";
    }

    // 프로젝트 생성 처리 (VIEWER 제한)
    @PostMapping("/create")
    public String createProject(@ModelAttribute ProjectCreateRequest request,
                                HttpSession session,
                                Model model) {
        try {
            AuthUtil.chechNotViewer(session);
            projectService.createProject(request);
            return "redirect:/projects/list";  // 성공 시 목록으로
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "project-create";  // 실패 시 다시 폼으로
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "project-create";  // 실패 시 다시 폼으로
        }
    }

    // 프로젝트 상세 (조회만 - 권한 제한 없음)
    @GetMapping("/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        ProjectResponse project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "project-detail";
    }

    // 프로젝트 수정 폼 (VIEWER 제한)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpSession session,
                           Model model) {
        AuthUtil.chechNotViewer(session);
        ProjectResponse project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "project-edit";
    }

    // 프로젝트 수정 처리 (VIEWER 제한)
    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable Long id, 
                                @ModelAttribute ProjectCreateRequest request,
                                HttpSession session,
                                Model model) {
        try {
            AuthUtil.chechNotViewer(session);
            projectService.updateProject(id, request);
            return "redirect:/projects/list";  // 성공 시 프로젝트 목록으로
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", projectService.getProjectById(id));
            return "project-edit";  // 실패 시 다시 폼으로
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", projectService.getProjectById(id));
            return "project-edit";  // 실패 시 다시 폼으로
        }
    }

    // 프로젝트 삭제 (VIEWER 제한)
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, HttpSession session) {
        AuthUtil.chechNotViewer(session);
        projectService.deleteProject(id);
        return "redirect:/projects/list";
    }
}
