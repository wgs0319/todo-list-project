package com.ToDo.controller;

import com.ToDo.dto.DashboardData;
import com.ToDo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final DashboardService dashboardService;

    @GetMapping("/")
    public String home(Model model) {
        DashboardData dashboardData = dashboardService.getDashboardData();
        model.addAttribute("dashboard", dashboardData);
        return "dashboard";
    }
}
