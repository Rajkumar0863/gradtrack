package com.rajkumar.gradtrack.model;

import java.time.LocalDate;

public class JobApplication {

    private Long id;
    private String company;
    private String role;
    private ApplicationStatus status;
    private Priority priority;
    private LocalDate applicationDate;
    private LocalDate deadline;

    // Empty constructor
    public JobApplication() {
    }

    // Constructor with all fields
    public JobApplication(
            Long id,
            String company,
            String role,
            ApplicationStatus status,
            Priority priority,
            LocalDate applicationDate,
            LocalDate deadline) {

        this.id = id;
        this.company = company;
        this.role = role;
        this.status = status;
        this.priority = priority;
        this.applicationDate = applicationDate;
        this.deadline = deadline;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}