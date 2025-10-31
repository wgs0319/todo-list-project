package com.ToDo.controller;

import com.ToDo.domain.Role;
import com.ToDo.domain.TaskPriority;
import com.ToDo.domain.TaskStatus;
import com.ToDo.dto.*;
import com.ToDo.service.CommentService;
import com.ToDo.service.ProjectService;
import com.ToDo.service.TaskService;
import com.ToDo.service.UserService;
import com.ToDo.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;
    private final CommentService commentService;

    // 작업 목록 (검색/필터링 포함)
    @GetMapping("/list")
    public String taskList(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<TaskResponse> tasks;

        // 검색 조건이 하나라도 있는지 확인 (빈 문자열 체크 추가)
        boolean hasSearchCondition =
                (status != null && !status.isEmpty()) ||
                        (priority != null && !priority.isEmpty()) ||
                        (assigneeId != null && !assigneeId.isEmpty()) ||
                        (keyword != null && !keyword.isEmpty());

        if (hasSearchCondition) {
            // 빈 문자열을 null로 변환
            TaskStatus taskStatus = (status != null && !status.isEmpty()) ? TaskStatus.valueOf(status) : null;
            TaskPriority taskPriority = (priority != null && !priority.isEmpty()) ? TaskPriority.valueOf(priority) : null;
            String finalAssigneeId = (assigneeId != null && !assigneeId.isEmpty()) ? assigneeId : null;
            String finalKeyword = (keyword != null && !keyword.isEmpty()) ? keyword : null;

            tasks = taskService.searchTasks(projectId, taskStatus, taskPriority, finalAssigneeId, finalKeyword);
        } else if (projectId != null) {
            tasks = taskService.getTasksByProjectId(projectId);
        } else {
            tasks = taskService.getAllTasks();
        }

        // 프로젝트 정보 (필터링용)
        if (projectId != null) {
            ProjectResponse project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);

            // 프로젝트 통계
            TaskStatistics statistics = taskService.getProjectStatistics(projectId);
            model.addAttribute("statistics", statistics);
        }

        // 필터링 옵션
        model.addAttribute("tasks", tasks);
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());

        // 현재 선택된 필터 값
        model.addAttribute("selectedProjectId", projectId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedAssigneeId", assigneeId);
        model.addAttribute("selectedKeyword", keyword);

        return "task-list";
    }

    // 작업 생성 폼 (VIEWER 제한, 관리자 및 담당자만 가능)
    @GetMapping("/create")
    public String createForm(@RequestParam(required = false) Long projectId,
                             HttpSession session,
                             Model model) {
        // VIEWER는 사용 불가
        AuthUtil.chechNotViewer(session);

        List<ProjectResponse> projects = projectService.getAllProjects();
        List<UserResponse> users = userService.getAllUsers();

        model.addAttribute("projects", projects);
        model.addAttribute("users", users);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());

        if (projectId != null) {
            model.addAttribute("selectedProjectId", projectId);
        }
        return "task-create";
    }

    // 작업 생성 처리 (VIEWER 제한)
    @PostMapping("/create")
    public String createTask(@ModelAttribute TaskCreateRequest request,
                             HttpSession session,
                             Model model) {
        try {
            AuthUtil.chechNotViewer(session);
            TaskResponse task = taskService.createTask(request);
            return "redirect:/tasks/list?projectId=" + request.getProjectId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("projects", projectService.getAllProjects());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            return "task-create";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("projects", projectService.getAllProjects());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            return "task-create";
        }
    }

    // 작업 상세 (댓글 포함)
    @GetMapping("/{id}")
    public String taskDetail(@PathVariable Long id, Model model) {
        TaskResponse task = taskService.getTaskById(id);
        List<CommentResponse> comments = commentService.getTaskComments(id);

        model.addAttribute("task", task);
        model.addAttribute("comments", comments);
        return "task-detail";
    }

    // 작업 수정 폼 (VIEWER 제한, 관리자 및 담당자만 가능)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpSession session,
                           Model model) {
        UserResponse loginUser = AuthUtil.getLoginUser(session);
        
        // VIEWER는 사용 불가
        AuthUtil.chechNotViewer(session);

        TaskResponse task = taskService.getTaskById(id);
        
        // 관리자 및 담당자만 사용 가능
        if (loginUser.getRole() != Role.ADMIN &&
            !loginUser.getId().equals(task.getAssigneeId())) {
            throw new IllegalArgumentException("담당자 또는 관리자만 수정할 수 있습니다");
        }
        
        List<UserResponse> users = userService.getAllUsers();

        model.addAttribute("task", task);
        model.addAttribute("users", users);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());

        return "task-edit";
    }
    
    // 작업 수정 처리 (VIEWER 제한, 관리자 및 담당자만 가능)
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute TaskUpdateRequest request,
                             HttpSession session,
                             Model model) {
        try {
            UserResponse loginUser = AuthUtil.getLoginUser(session);

            // VIEWER는 사용 불가
            AuthUtil.chechNotViewer(session);

            TaskResponse task = taskService.updateTask(id, request);

            // 관리자 및 담당자만 사용 가능
            if (loginUser.getRole() != Role.ADMIN &&
                    !loginUser.getId().equals(task.getAssigneeId())) {
                throw new IllegalArgumentException("담당자 또는 관리자만 수정할 수 있습니다");
            }

            taskService.updateTask(id, request);
            return "redirect:/tasks/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("task", taskService.getTaskById(id));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            return "task-edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("task", taskService.getTaskById(id));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            return "task-edit";
        }
    }
    
    // 작업 삭제 (VIEWER 제한, 관리자 및 담당자만 가능)
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        UserResponse loginUser = AuthUtil.getLoginUser(session);

        // VIEWER는 사용 불가
        AuthUtil.chechNotViewer(session);

        TaskResponse task = taskService.getTaskById(id);

        // 관리자 및 담당자만 사용 가능
        if (loginUser.getRole() != Role.ADMIN &&
                !loginUser.getId().equals(task.getAssigneeId())) {
            throw new IllegalArgumentException("담당자 또는 관리자만 삭제할 수 있습니다");
        }

        Long projectId = task.getProjectId();
        taskService.deleteTask(id);
        return "redirect:/tasks/list?projectId=" + projectId;
    }

    // 작업 완료 토글 (VIEWER 제한, 관리자 및 담당자만 가능)
    @PostMapping("/{id}/toggle")
    @ResponseBody
    public String toggleTaskCompletion(@PathVariable Long id, HttpSession session) {
        try {
            UserResponse loginUser = AuthUtil.getLoginUser(session);

            // VIEWER는 사용 불가
            if (loginUser.getRole() == Role.VIEWER) {
                return "error: 조회 권한만 있습니다";
            }

            // 작업 정보 조회
            TaskResponse task = taskService.getTaskById(id);

            // 관리자 및 담당자만 사용 가능
            if (loginUser.getRole() != Role.ADMIN &&
                !loginUser.getId().equals(task.getAssigneeId())) {
                return "error: 담당자 또는 관리자만 상태를 변경할 수 있습니다";
            }

            taskService.toggleTaskCompletion(id);
            return "success";
        } catch (Exception e) {
            return "error" + e.getMessage();
        }
    }
}
