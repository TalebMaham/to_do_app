package com.example.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "task_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false)
    private String fieldChanged;

    @Column(nullable = false, length = 1000)
    private String oldValue;

    @Column(nullable = false, length = 1000)
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modified_by", nullable = false)
    private User modifiedBy;

    public TaskHistory() {}

    public TaskHistory(Task task, String fieldChanged, String oldValue, String newValue, User modifiedBy) {
        this.task = task;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.modifiedBy = modifiedBy;
    }

    @PrePersist
    protected void onCreate() {
        this.modifiedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    // Setters (utiles pour les tests ou le mapping manuel)
    public void setTask(Task task) {
        this.task = task;
    }

    public void setFieldChanged(String fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
