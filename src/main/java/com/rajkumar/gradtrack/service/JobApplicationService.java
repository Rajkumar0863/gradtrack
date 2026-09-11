package com.rajkumar.gradtrack.service;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.model.Priority;
import com.rajkumar.gradtrack.repository.JobApplicationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

        this.jobApplicationRepository =
                jobApplicationRepository;
    }

    public List<JobApplication> getAllApplications() {

        return jobApplicationRepository.findAll();
    }

    public List<JobApplication> searchApplications(
            String search,
            ApplicationStatus status,
            Priority priority,
            String sort) {

        Specification<JobApplication> specification =
                Specification.unrestricted();

        if (
                search != null
                &&
                !search.isBlank()
        ) {

            String searchTerm =
                    "%"
                    +
                    search
                            .trim()
                            .toLowerCase()
                    +
                    "%";

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.or(
                                            criteriaBuilder.like(
                                                    criteriaBuilder.lower(
                                                            root.get("company")
                                                    ),
                                                    searchTerm
                                            ),
                                            criteriaBuilder.like(
                                                    criteriaBuilder.lower(
                                                            root.get("role")
                                                    ),
                                                    searchTerm
                                            )
                                    )
                    );
        }

        if (
                status != null
        ) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("status"),
                                            status
                                    )
                    );
        }

        if (
                priority != null
        ) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("priority"),
                                            priority
                                    )
                    );
        }

        Sort applicationSort =
                getSort(sort);

        return jobApplicationRepository.findAll(
                specification,
                applicationSort
        );
    }

    public Optional<JobApplication> getApplicationById(
            Long id) {

        return jobApplicationRepository.findById(
                id
        );
    }

    public JobApplication createApplication(
            JobApplication application) {

        validateDates(
                application.getApplicationDate(),
                application.getDeadline()
        );

        application.setId(
                null
        );

        return jobApplicationRepository.save(
                application
        );
    }

    public Optional<JobApplication> updateApplication(
            Long id,
            JobApplication updatedApplication) {

        Optional<JobApplication> existingOptional =
                jobApplicationRepository.findById(
                        id
                );

        if (
                existingOptional.isEmpty()
        ) {

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
                jobApplicationRepository.save(
                        existingApplication
                );

        return Optional.of(
                savedApplication
        );
    }

    public boolean deleteApplication(
            Long id) {

        if (
                !jobApplicationRepository.existsById(
                        id
                )
        ) {

            return false;
        }

        jobApplicationRepository.deleteById(
                id
        );

        return true;
    }

    private Sort getSort(
            String sort) {

        if (
                sort == null
                ||
                sort.isBlank()
        ) {

            return Sort.unsorted();
        }

        return switch (
                sort.trim().toLowerCase()
        ) {

            case "deadline" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "deadline"
                    );

            case "deadline_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "deadline"
                    );

            case "company" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "company"
                    );

            case "company_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "company"
                    );

            case "application_date" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "applicationDate"
                    );

            case "application_date_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "applicationDate"
                    );

            default ->
                    Sort.unsorted();
        };
    }

    private void validateDates(
            LocalDate applicationDate,
            LocalDate deadline) {

        if (
                applicationDate == null
                ||
                deadline == null
        ) {

            return;
        }

        if (
                deadline.isBefore(
                        applicationDate
                )
        ) {

            throw new IllegalArgumentException(
                    "Deadline cannot be before application date"
            );
        }
    }

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (
                currentStatus == null
                ||
                newStatus == null
        ) {

            return;
        }

        if (
                currentStatus == newStatus
        ) {

            return;
        }

        Set<ApplicationStatus> allowedStatuses =
                getAllowedNextStatuses(
                        currentStatus
                );

        if (
                !allowedStatuses.contains(
                        newStatus
                )
        ) {

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

        return switch (
                currentStatus
        ) {

            case SAVED ->
                    Set.of(
                            ApplicationStatus.APPLIED,
                            ApplicationStatus.WITHDRAWN
                    );

            case APPLIED ->
                    Set.of(
                            ApplicationStatus.ONLINE_ASSESSMENT,
                            ApplicationStatus.VIDEO_INTERVIEW,
                            ApplicationStatus.REJECTED,
                            ApplicationStatus.WITHDRAWN
                    );

            case ONLINE_ASSESSMENT ->
                    Set.of(
                            ApplicationStatus.VIDEO_INTERVIEW,
                            ApplicationStatus.ASSESSMENT_CENTRE,
                            ApplicationStatus.REJECTED,
                            ApplicationStatus.WITHDRAWN
                    );

            case VIDEO_INTERVIEW ->
                    Set.of(
                            ApplicationStatus.ASSESSMENT_CENTRE,
                            ApplicationStatus.FINAL_INTERVIEW,
                            ApplicationStatus.REJECTED,
                            ApplicationStatus.WITHDRAWN
                    );

            case ASSESSMENT_CENTRE ->
                    Set.of(
                            ApplicationStatus.FINAL_INTERVIEW,
                            ApplicationStatus.OFFER,
                            ApplicationStatus.REJECTED,
                            ApplicationStatus.WITHDRAWN
                    );

            case FINAL_INTERVIEW ->
                    Set.of(
                            ApplicationStatus.OFFER,
                            ApplicationStatus.REJECTED,
                            ApplicationStatus.WITHDRAWN
                    );

            case OFFER ->
                    Set.of(
                            ApplicationStatus.WITHDRAWN
                    );

            case REJECTED,
                 WITHDRAWN ->
                    Set.of();
        };
    }
}