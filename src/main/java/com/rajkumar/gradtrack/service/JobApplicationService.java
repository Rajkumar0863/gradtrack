package com.rajkumar.gradtrack.service;

import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository) {

        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    public Optional<JobApplication> getApplicationById(Long id) {
        return jobApplicationRepository.findById(id);
    }

    public JobApplication createApplication(JobApplication application) {
        application.setId(null);
        return jobApplicationRepository.save(application);
    }

    public Optional<JobApplication> updateApplication(
            Long id,
            JobApplication updatedApplication) {

        Optional<JobApplication> existingOptional =
                jobApplicationRepository.findById(id);

        if (existingOptional.isEmpty()) {
            return Optional.empty();
        }

        JobApplication existingApplication =
                existingOptional.get();

        existingApplication.setCompany(
                updatedApplication.getCompany());

        existingApplication.setRole(
                updatedApplication.getRole());

        existingApplication.setStatus(
                updatedApplication.getStatus());

        existingApplication.setPriority(
                updatedApplication.getPriority());

        existingApplication.setApplicationDate(
                updatedApplication.getApplicationDate());

        existingApplication.setDeadline(
                updatedApplication.getDeadline());

        JobApplication savedApplication =
                jobApplicationRepository.save(existingApplication);

        return Optional.of(savedApplication);
    }

    public boolean deleteApplication(Long id) {

        if (!jobApplicationRepository.existsById(id)) {
            return false;
        }

        jobApplicationRepository.deleteById(id);

        return true;
    }
}