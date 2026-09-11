package com.rajkumar.gradtrack.repository;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long>,
        JpaSpecificationExecutor<JobApplication> {

    long countByStatus(
            ApplicationStatus status
    );

    long countByPriority(
            Priority priority
    );
}