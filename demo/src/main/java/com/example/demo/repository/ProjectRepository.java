package com.example.demo.repository;

import com.example.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByAdminId(Long adminId);
    Optional<Project> findById(Long id);
        // Trouver les projets où l'utilisateur est membre via ProjectMember
    @Query("SELECT p FROM Project p JOIN p.projectMembers pm WHERE pm.user.id = :userId")
    List<Project> findByUserIsMember(@Param("userId") Long userId);

}
