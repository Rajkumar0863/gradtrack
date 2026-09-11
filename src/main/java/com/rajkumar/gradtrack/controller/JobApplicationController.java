package com.rajkumar.gradtrack.controller;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.model.Priority;
import com.rajkumar.gradtrack.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(
            JobApplicationService jobApplicationService) {

        this.jobApplicationService =
                jobApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>>
    getAllApplications(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            ApplicationStatus status,

            @RequestParam(required = false)
            Priority priority,

            @RequestParam(required = false)
            String sort) {

        boolean hasQueryParameters =
                (
                        search != null
                        &&
                        !search.isBlank()
                )
                ||
                status != null
                ||
                priority != null
                ||
                (
                        sort != null
                        &&
                        !sort.isBlank()
                );

        if (
                !hasQueryParameters
        ) {

            return ResponseEntity.ok(
                    jobApplicationService
                            .getAllApplications()
            );
        }

        return ResponseEntity.ok(
                jobApplicationService
                        .searchApplications(
                                search,
                                status,
                                priority,
                                sort
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication>
    getApplicationById(
            @PathVariable Long id) {

        Optional<JobApplication> application =
                jobApplicationService
                        .getApplicationById(
                                id
                        );

        if (
                application.isEmpty()
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                application.get()
        );
    }

    @PostMapping
    public ResponseEntity<JobApplication>
    createApplication(
            @Valid
            @RequestBody
            JobApplication application) {

        JobApplication createdApplication =
                jobApplicationService
                        .createApplication(
                                application
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        createdApplication
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication>
    updateApplication(
            @PathVariable Long id,

            @Valid
            @RequestBody
            JobApplication application) {

        Optional<JobApplication> updatedApplication =
                jobApplicationService
                        .updateApplication(
                                id,
                                application
                        );

        if (
                updatedApplication.isEmpty()
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                updatedApplication.get()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteApplication(
            @PathVariable Long id) {

        boolean deleted =
                jobApplicationService
                        .deleteApplication(
                                id
                        );

        if (
                !deleted
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .noContent()
                .build();
    }
}