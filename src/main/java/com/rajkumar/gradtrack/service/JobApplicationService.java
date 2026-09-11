package com.rajkumar.gradtrack.service;

import com.rajkumar.gradtrack.model.JobApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobApplicationService {

    private final List<JobApplication> applications = new ArrayList<>();
    private Long nextId = 1L;

    public List<JobApplication> getAllApplications() {
        return applications;
    }

    public JobApplication getApplicationById(Long id) {

        for (JobApplication application : applications) {
            if (application.getId().equals(id)) {
                return application;
            }
        }

        return null;
    }

    public JobApplication createApplication(JobApplication application) {
        application.setId(nextId++);
        applications.add(application);
        return application;
    }

    public JobApplication updateApplication(
            Long id,
            JobApplication updatedApplication) {

        for (JobApplication application : applications) {

            if (application.getId().equals(id)) {

                application.setCompany(updatedApplication.getCompany());
                application.setRole(updatedApplication.getRole());
                application.setStatus(updatedApplication.getStatus());
                application.setPriority(updatedApplication.getPriority());
                application.setApplicationDate(updatedApplication.getApplicationDate());
                application.setDeadline(updatedApplication.getDeadline());

                return application;
            }
        }

        return null;
    }

    public boolean deleteApplication(Long id) {
        return applications.removeIf(
                application -> application.getId().equals(id)
        );
    }
}