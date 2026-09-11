package com.rajkumar.gradtrack.repository;

import com.rajkumar.gradtrack.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {
}