package com.ToDo.repository;

import com.ToDo.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 제목으로 검색 (부분 일치)
    List<Project> findByTitleContaining(String title);
}
