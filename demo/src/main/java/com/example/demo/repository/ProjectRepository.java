package com.example.demo.repository;

import com.example.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByAdminId(Long adminId);
    Optional<Project> findById(Long id);
}
