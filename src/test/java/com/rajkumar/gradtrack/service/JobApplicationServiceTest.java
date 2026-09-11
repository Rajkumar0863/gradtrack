package com.rajkumar.gradtrack.service;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.model.Priority;
import com.rajkumar.gradtrack.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private JobApplication validApplication;

    @BeforeEach
    void setUp() {

        validApplication =
                new JobApplication(
                        1L,
                        "Accenture",
                        "Software Engineering Graduate Programme",
                        ApplicationStatus.APPLIED,
                        Priority.HIGH,
                        LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 10, 20)
                );
    }

    @Test
    void shouldCreateApplicationWhenDataIsValid() {

        JobApplication savedApplication =
                new JobApplication(
                        1L,
                        "Accenture",
                        "Software Engineering Graduate Programme",
                        ApplicationStatus.APPLIED,
                        Priority.HIGH,
                        LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 10, 20)
                );

        when(
                jobApplicationRepository.save(
                        any(JobApplication.class)
                )
        ).thenReturn(
                savedApplication
        );

        JobApplication result =
                jobApplicationService.createApplication(
                        validApplication
                );

        assertNotNull(result);

        assertEquals(
                "Accenture",
                result.getCompany()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                result.getStatus()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).save(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldRejectApplicationWhenDeadlineIsBeforeApplicationDate() {

        JobApplication invalidApplication =
                new JobApplication(
                        null,
                        "Google",
                        "Software Engineer Graduate",
                        ApplicationStatus.APPLIED,
                        Priority.HIGH,
                        LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 8, 1)
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                jobApplicationService
                                        .createApplication(
                                                invalidApplication
                                        )
                );

        assertEquals(
                "Deadline cannot be before application date",
                exception.getMessage()
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldReturnApplicationWhenIdExists() {

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        validApplication
                )
        );

        Optional<JobApplication> result =
                jobApplicationService
                        .getApplicationById(
                                1L
                        );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "Accenture",
                result.get().getCompany()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).findById(
                1L
        );
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {

        when(
                jobApplicationRepository.findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        Optional<JobApplication> result =
                jobApplicationService
                        .getApplicationById(
                                999L
                        );

        assertTrue(
                result.isEmpty()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).findById(
                999L
        );
    }

    @Test
    void shouldAllowAppliedToOnlineAssessment() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        JobApplication updatedApplication =
                createApplicationWithStatus(
                        ApplicationStatus.ONLINE_ASSESSMENT
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        when(
                jobApplicationRepository.save(
                        any(JobApplication.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Optional<JobApplication> result =
                jobApplicationService
                        .updateApplication(
                                1L,
                                updatedApplication
                        );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                ApplicationStatus.ONLINE_ASSESSMENT,
                result.get().getStatus()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).save(
                existingApplication
        );
    }

    @Test
    void shouldAllowFinalInterviewToOffer() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.FINAL_INTERVIEW
                );

        JobApplication updatedApplication =
                createApplicationWithStatus(
                        ApplicationStatus.OFFER
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        when(
                jobApplicationRepository.save(
                        any(JobApplication.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Optional<JobApplication> result =
                jobApplicationService
                        .updateApplication(
                                1L,
                                updatedApplication
                        );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                ApplicationStatus.OFFER,
                result.get().getStatus()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).save(
                existingApplication
        );
    }

    @Test
    void shouldRejectSavedToOffer() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.SAVED
                );

        JobApplication invalidUpdate =
                createApplicationWithStatus(
                        ApplicationStatus.OFFER
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                jobApplicationService
                                        .updateApplication(
                                                1L,
                                                invalidUpdate
                                        )
                );

        assertEquals(
                "Invalid status transition from SAVED to OFFER",
                exception.getMessage()
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldRejectRejectedToFinalInterview() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.REJECTED
                );

        JobApplication invalidUpdate =
                createApplicationWithStatus(
                        ApplicationStatus.FINAL_INTERVIEW
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                jobApplicationService
                                        .updateApplication(
                                                1L,
                                                invalidUpdate
                                        )
                );

        assertEquals(
                "Invalid status transition from REJECTED to FINAL_INTERVIEW",
                exception.getMessage()
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldRejectWithdrawnToApplied() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.WITHDRAWN
                );

        JobApplication invalidUpdate =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                jobApplicationService
                                        .updateApplication(
                                                1L,
                                                invalidUpdate
                                        )
                );

        assertEquals(
                "Invalid status transition from WITHDRAWN to APPLIED",
                exception.getMessage()
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldAllowApplicationToRemainInSameStatus() {

        JobApplication existingApplication =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        JobApplication updatedApplication =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationRepository.findById(1L)
        ).thenReturn(
                Optional.of(
                        existingApplication
                )
        );

        when(
                jobApplicationRepository.save(
                        any(JobApplication.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Optional<JobApplication> result =
                jobApplicationService
                        .updateApplication(
                                1L,
                                updatedApplication
                        );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                result.get().getStatus()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).save(
                existingApplication
        );
    }

    @Test
    void shouldSearchApplicationsBySearchTerm() {

        JobApplication microsoftApplication =
                new JobApplication(
                        2L,
                        "Microsoft",
                        "Software Developer",
                        ApplicationStatus.APPLIED,
                        Priority.HIGH,
                        LocalDate.of(2026, 9, 11),
                        LocalDate.of(2026, 9, 30)
                );

        when(
                jobApplicationRepository.findAll(
                        any(Specification.class),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(
                        microsoftApplication
                )
        );

        List<JobApplication> result =
                jobApplicationService
                        .searchApplications(
                                "Microsoft",
                                null,
                                null,
                                null
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Microsoft",
                result.getFirst().getCompany()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).findAll(
                any(Specification.class),
                any(Sort.class)
        );
    }

    @Test
    void shouldFilterApplicationsByStatus() {

        JobApplication application =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationRepository.findAll(
                        any(Specification.class),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(
                        application
                )
        );

        List<JobApplication> result =
                jobApplicationService
                        .searchApplications(
                                null,
                                ApplicationStatus.APPLIED,
                                null,
                                null
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                result.getFirst().getStatus()
        );
    }

    @Test
    void shouldFilterApplicationsByPriority() {

        JobApplication application =
                createApplicationWithStatus(
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationRepository.findAll(
                        any(Specification.class),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(
                        application
                )
        );

        List<JobApplication> result =
                jobApplicationService
                        .searchApplications(
                                null,
                                null,
                                Priority.HIGH,
                                null
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                Priority.HIGH,
                result.getFirst().getPriority()
        );
    }

    @Test
    void shouldApplyDeadlineAscendingSort() {

        when(
                jobApplicationRepository.findAll(
                        any(Specification.class),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(
                        validApplication
                )
        );

        jobApplicationService
                .searchApplications(
                        null,
                        null,
                        null,
                        "deadline"
                );

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(
                        Sort.class
                );

        verify(
                jobApplicationRepository
        ).findAll(
                any(Specification.class),
                sortCaptor.capture()
        );

        Sort sort =
                sortCaptor.getValue();

        Sort.Order deadlineOrder =
                sort.getOrderFor(
                        "deadline"
                );

        assertNotNull(
                deadlineOrder
        );

        assertTrue(
                deadlineOrder.isAscending()
        );
    }

    @Test
    void shouldApplyCombinedSearchStatusPriorityAndSort() {

        when(
                jobApplicationRepository.findAll(
                        any(Specification.class),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(
                        validApplication
                )
        );

        List<JobApplication> result =
                jobApplicationService
                        .searchApplications(
                                "Accenture",
                                ApplicationStatus.APPLIED,
                                Priority.HIGH,
                                "company"
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Accenture",
                result.getFirst().getCompany()
        );

        verify(
                jobApplicationRepository,
                times(1)
        ).findAll(
                any(Specification.class),
                any(Sort.class)
        );
    }

    private JobApplication createApplicationWithStatus(
            ApplicationStatus status) {

        return new JobApplication(
                1L,
                "Accenture",
                "Software Engineering Graduate Programme",
                status,
                Priority.HIGH,
                LocalDate.of(2026, 9, 11),
                LocalDate.of(2026, 10, 20)
        );
    }
}