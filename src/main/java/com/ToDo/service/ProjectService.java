package com.ToDo.service;

import com.ToDo.domain.Project;
import com.ToDo.dto.ProjectCreateRequest;
import com.ToDo.dto.ProjectResponse;
import com.ToDo.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    // 프로젝트 생성
    public ProjectResponse createProject(ProjectCreateRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        Project savedProject = projectRepository.save(project);
        return ProjectResponse.from(savedProject);
    }

    // 프로젝트 목록 조회
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    // 프로젝트 검색 (제목)
    public List<ProjectResponse> searchProjects(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllProjects();
        }
        return projectRepository.findByTitleContaining(keyword).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    // 프로젝트 상세 조회
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id));
        return ProjectResponse.from(project);
    }

    // 프로젝트 정보 수정
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectCreateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);
        return ProjectResponse.from(updatedProject);
    }

    // 프로젝트 삭제
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id);
        }
        projectRepository.deleteById(id);
    }
}
