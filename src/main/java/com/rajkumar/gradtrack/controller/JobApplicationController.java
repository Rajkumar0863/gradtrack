package com.rajkumar.gradtrack.controller;

import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.service.JobApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public List<JobApplication> getAllApplications() {
        return jobApplicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public JobApplication getApplicationById(@PathVariable Long id) {
        return jobApplicationService.getApplicationById(id);
    }

    @PostMapping
    public JobApplication createApplication(
            @RequestBody JobApplication application) {

        return jobApplicationService.createApplication(application);
    }

    @PutMapping("/{id}")
    public JobApplication updateApplication(
            @PathVariable Long id,
            @RequestBody JobApplication application) {

        return jobApplicationService.updateApplication(id, application);
    }

    @DeleteMapping("/{id}")
    public boolean deleteApplication(@PathVariable Long id) {
        return jobApplicationService.deleteApplication(id);
    }
}