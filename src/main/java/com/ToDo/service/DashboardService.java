package com.ToDo.service;

import com.ToDo.domain.TaskStatus;
import com.ToDo.dto.DashboardData;
import com.ToDo.dto.TaskResponse;
import com.ToDo.repository.ProjectRepository;
import com.ToDo.repository.TaskRepository;
import com.ToDo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    public DashboardData getDashboardData() {
        // 전체 통계
        long totalUsers = userRepository.count();
        long totalProjects = projectRepository.count();
        long totalTasks = taskRepository.count();

        // 작업 상태별 통계
        long todoTasks = taskRepository.findByStatus(TaskStatus.ToDO).size();
        long inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS).size();
        long doneTasks = taskRepository.findByStatus(TaskStatus.DONE).size();

        // 완료율 계산
        double completionRate = totalTasks > 0 ? (doneTasks * 100.0 / totalTasks) : 0;

        // 최근 작업 5개
        List<TaskResponse> recentTasks = taskRepository.findAll().stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(5)
                .map(TaskResponse::from)
                .collect(Collectors.toList());

        // 마감기한이 7일 이내인 작업
        List<TaskResponse> urgentTasks = taskService.getUrgentTasks(7)
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        return DashboardData.builder()
                .totalUsers(totalUsers)
                .totalProjects(totalProjects)
                .totalTasks(totalTasks)
                .todoTasks(todoTasks)
                .inProgressTasks(inProgressTasks)
                .doneTasks(doneTasks)
                .completionRate(completionRate)
                .recentTasks(recentTasks)
                .urgentTasks(urgentTasks)
                .build();
    }
}
