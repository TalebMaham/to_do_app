package com.example.demo.dto;


// Classe DTO pour recevoir les données de création de projet
public class ProjectRequest {
    private String name;
    private String description;
    private String startDate;
    private Long adminId;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public Long getAdminId() {
        return adminId;
    }
}
