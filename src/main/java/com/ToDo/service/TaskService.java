package com.ToDo.service;

import com.ToDo.domain.*;
import com.ToDo.dto.TaskCreateRequest;
import com.ToDo.dto.TaskResponse;
import com.ToDo.dto.TaskStatistics;
import com.ToDo.dto.TaskUpdateRequest;
import com.ToDo.repository.CommentRepository;
import com.ToDo.repository.ProjectRepository;
import com.ToDo.repository.TaskRepository;
import com.ToDo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 작업 생성
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        // 프로젝트 조회
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 담당자 조회(있는 경우)
        User assignee = null;
        if (request.getAssigneeId() != null && !request.getAssigneeId().isEmpty()) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다."));
        }

        // Task 생성
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .assignee(assignee)
                .status(request.getStatus() != null ? TaskStatus.valueOf(request.getStatus()) : TaskStatus.ToDO)
                .priority(request.getPriority() != null ? TaskPriority.valueOf(request.getPriority()) : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        return  TaskResponse.from(savedTask);
    }

    // 모든 작업 조회 (댓글 개수 포함)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> {
                    long commentCount = commentRepository.countByTaskId(task.getId());
                    return TaskResponse.fromWithCommentCount(task, commentCount);
                })
                .collect(Collectors.toList());
    }

    // 프로젝트 별 작업 조회 (댓글 개수 포함)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(task -> {
                    long commentCount = commentRepository.countByTaskId(task.getId());
                    return TaskResponse.fromWithCommentCount(task, commentCount);
                })
                .collect(Collectors.toList());
    }
    
    // 작업 상세 조회
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));
        return TaskResponse.from(task);
    }

    // 복합 검색 (댓글 개수 포함)
    public List<TaskResponse> searchTasks(Long projectId, TaskStatus status,
                                          TaskPriority priority, String assigneeId,
                                          String keyword) {
        return taskRepository.searchTasks(projectId, status, priority, assigneeId, keyword)
                .stream()
                .map(task -> {
                    long commentCount = commentRepository.countByTaskId(task.getId());
                    return TaskResponse.fromWithCommentCount(task, commentCount);
                })
                .collect(Collectors.toList());
    }

    // 프로젝트별 작업 통계
    public TaskStatistics getProjectStatistics(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        long total = tasks.size();
        long todo = tasks.stream().filter(t -> t.getStatus() == TaskStatus.ToDO).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        double completionRate = total > 0 ? (done * 100.0 / total) : 0;

        return new TaskStatistics(total, todo, inProgress, done, completionRate);
    }

    // 마감 임박 작업 조회 (N일 이내-현재 7일, 미완료 작업만)
    public List<TaskResponse> getUrgentTasks(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(daysAhead);

        return taskRepository.findAll().stream()
                .filter(task -> task.getDueDate() != null)  // 마감일이 설정된 작업만
                .filter(task -> {
                    LocalDate dueDate = task.getDueDate();
                    // 오늘 이후 이면서 deadline 이전인 작업
                    return !dueDate.isBefore(today) && !dueDate.isAfter(deadline);
                })
                .filter(task -> task.getStatus() != TaskStatus.DONE)    // 완료된 작업 제외
                .sorted(Comparator.comparing(Task::getDueDate))     // 마감일 순 정렬
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }
    
    // 작업 수정
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        // 담당자 변경
        if (request.getAssigneeId() != null && !request.getAssigneeId().isEmpty()) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다."));
            task.setAssignee(assignee);
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? TaskStatus.valueOf(request.getStatus()) : task.getStatus());
        task.setPriority(request.getPriority() != null ? TaskPriority.valueOf(request.getPriority()) : task.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.from(updatedTask);
    }
    
    // 작업 삭제
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("작업을 찾을 수 없습니다.");
        }
        taskRepository.deleteById(id);
    }

    // 작업 상태 토글 (완료 <-> 진행중)
    @Transactional
    public TaskResponse toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));

        // 완료 상태면 -> 진행중, 아니면 -> 완료로
        if (task.getStatus() == TaskStatus.DONE) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            task.setStatus(TaskStatus.DONE);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.from(updatedTask);
    }
}
