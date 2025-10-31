package com.ToDo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Builder
public class DashboardData {
    private long totalUsers;
    private long totalProjects;
    private long totalTasks;

    private long todoTasks;
    private long inProgressTasks;
    private long doneTasks;

    private double completionRate;

    private List<TaskResponse> recentTasks;

    private List<TaskResponse> urgentTasks;
}
