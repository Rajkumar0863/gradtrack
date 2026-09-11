package com.rajkumar.gradtrack.service;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        validateDates(
                application.getApplicationDate(),
                application.getDeadline()
        );

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

        validateDates(
                updatedApplication.getApplicationDate(),
                updatedApplication.getDeadline()
        );

        validateStatusTransition(
                existingApplication.getStatus(),
                updatedApplication.getStatus()
        );

        existingApplication.setCompany(
                updatedApplication.getCompany()
        );

        existingApplication.setRole(
                updatedApplication.getRole()
        );

        existingApplication.setStatus(
                updatedApplication.getStatus()
        );

        existingApplication.setPriority(
                updatedApplication.getPriority()
        );

        existingApplication.setApplicationDate(
                updatedApplication.getApplicationDate()
        );

        existingApplication.setDeadline(
                updatedApplication.getDeadline()
        );

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

    private void validateDates(
            LocalDate applicationDate,
            LocalDate deadline) {

        if (applicationDate == null || deadline == null) {
            return;
        }

        if (deadline.isBefore(applicationDate)) {
            throw new IllegalArgumentException(
                    "Deadline cannot be before application date"
            );
        }
    }

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (currentStatus == null || newStatus == null) {
            return;
        }

        if (currentStatus == newStatus) {
            return;
        }

        Set<ApplicationStatus> allowedStatuses =
                getAllowedNextStatuses(currentStatus);

        if (!allowedStatuses.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    private Set<ApplicationStatus> getAllowedNextStatuses(
            ApplicationStatus currentStatus) {

        return switch (currentStatus) {

            case SAVED -> Set.of(
                    ApplicationStatus.APPLIED,
                    ApplicationStatus.WITHDRAWN
            );

            case APPLIED -> Set.of(
                    ApplicationStatus.ONLINE_ASSESSMENT,
                    ApplicationStatus.VIDEO_INTERVIEW,
                    ApplicationStatus.REJECTED,
                    ApplicationStatus.WITHDRAWN
            );

            case ONLINE_ASSESSMENT -> Set.of(
                    ApplicationStatus.VIDEO_INTERVIEW,
                    ApplicationStatus.ASSESSMENT_CENTRE,
                    ApplicationStatus.REJECTED,
                    ApplicationStatus.WITHDRAWN
            );

            case VIDEO_INTERVIEW -> Set.of(
                    ApplicationStatus.ASSESSMENT_CENTRE,
                    ApplicationStatus.FINAL_INTERVIEW,
                    ApplicationStatus.REJECTED,
                    ApplicationStatus.WITHDRAWN
            );

            case ASSESSMENT_CENTRE -> Set.of(
                    ApplicationStatus.FINAL_INTERVIEW,
                    ApplicationStatus.OFFER,
                    ApplicationStatus.REJECTED,
                    ApplicationStatus.WITHDRAWN
            );

            case FINAL_INTERVIEW -> Set.of(
                    ApplicationStatus.OFFER,
                    ApplicationStatus.REJECTED,
                    ApplicationStatus.WITHDRAWN
            );

            case OFFER -> Set.of(
                    ApplicationStatus.WITHDRAWN
            );

            case REJECTED,
                 WITHDRAWN -> Set.of();
        };
    }
}