package com.rajkumar.gradtrack.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate applicationDate;
    private LocalDate deadline;

    public JobApplication() {
    }

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