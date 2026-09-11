package com.rajkumar.gradtrack.controller;

import com.rajkumar.gradtrack.model.ApplicationStatus;
import com.rajkumar.gradtrack.model.JobApplication;
import com.rajkumar.gradtrack.model.Priority;
import com.rajkumar.gradtrack.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobApplicationController.class)
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @Test
    void shouldReturnAllApplications() throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Accenture",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService.getAllApplications()
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content()
                                .contentTypeCompatibleWith(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].company")
                                .value("Accenture")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("APPLIED")
                );

        verify(
                jobApplicationService
        ).getAllApplications();
    }

    @Test
    void shouldReturnApplicationWhenIdExists()
            throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Microsoft",
                        ApplicationStatus.SAVED
                );

        when(
                jobApplicationService
                        .getApplicationById(1L)
        ).thenReturn(
                Optional.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.company")
                                .value("Microsoft")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SAVED")
                );
    }

    @Test
    void shouldReturn404WhenApplicationDoesNotExist()
            throws Exception {

        when(
                jobApplicationService
                        .getApplicationById(999L)
        ).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(
                        get("/api/applications/999")
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldCreateApplicationAndReturn201()
            throws Exception {

        JobApplication savedApplication =
                createApplication(
                        10L,
                        "Google",
                        ApplicationStatus.SAVED
                );

        when(
                jobApplicationService
                        .createApplication(
                                any(JobApplication.class)
                        )
        ).thenReturn(
                savedApplication
        );

        String requestJson = """
                {
                  "company": "Google",
                  "role": "Software Engineering Graduate Programme",
                  "status": "SAVED",
                  "priority": "HIGH",
                  "applicationDate": "2026-09-11",
                  "deadline": "2026-10-20"
                }
                """;

        mockMvc.perform(
                        post("/api/applications")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.company")
                                .value("Google")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SAVED")
                );
    }

    @Test
    void shouldReturn400WhenCompanyIsBlank()
            throws Exception {

        String requestJson = """
                {
                  "company": "",
                  "role": "Software Engineer",
                  "status": "SAVED",
                  "priority": "HIGH",
                  "applicationDate": "2026-09-11",
                  "deadline": "2026-10-20"
                }
                """;

        mockMvc.perform(
                        post("/api/applications")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.company")
                                .value(
                                        "Company name is required"
                                )
                );

        verify(
                jobApplicationService,
                never()
        ).createApplication(
                any(JobApplication.class)
        );
    }

    @Test
    void shouldUpdateApplicationAndReturn200()
            throws Exception {

        JobApplication updatedApplication =
                createApplication(
                        1L,
                        "Microsoft",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .updateApplication(
                                eq(1L),
                                any(JobApplication.class)
                        )
        ).thenReturn(
                Optional.of(
                        updatedApplication
                )
        );

        String requestJson = """
                {
                  "company": "Microsoft",
                  "role": "Software Engineering Graduate Programme",
                  "status": "APPLIED",
                  "priority": "HIGH",
                  "applicationDate": "2026-09-11",
                  "deadline": "2026-10-20"
                }
                """;

        mockMvc.perform(
                        put("/api/applications/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.company")
                                .value("Microsoft")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("APPLIED")
                );
    }

    @Test
    void shouldReturn404WhenUpdatingMissingApplication()
            throws Exception {

        when(
                jobApplicationService
                        .updateApplication(
                                eq(999L),
                                any(JobApplication.class)
                        )
        ).thenReturn(
                Optional.empty()
        );

        String requestJson = """
                {
                  "company": "Microsoft",
                  "role": "Software Engineering Graduate Programme",
                  "status": "APPLIED",
                  "priority": "HIGH",
                  "applicationDate": "2026-09-11",
                  "deadline": "2026-10-20"
                }
                """;

        mockMvc.perform(
                        put("/api/applications/999")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldDeleteApplicationAndReturn204()
            throws Exception {

        when(
                jobApplicationService
                        .deleteApplication(1L)
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        delete(
                                "/api/applications/1"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(
                jobApplicationService
        ).deleteApplication(
                1L
        );
    }

    @Test
    void shouldReturn404WhenDeletingMissingApplication()
            throws Exception {

        when(
                jobApplicationService
                        .deleteApplication(999L)
        ).thenReturn(
                false
        );

        mockMvc.perform(
                        delete(
                                "/api/applications/999"
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldSearchApplicationsByCompanyOrRole()
            throws Exception {

        JobApplication application =
                createApplication(
                        2L,
                        "Microsoft",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .searchApplications(
                                eq("Microsoft"),
                                isNull(),
                                isNull(),
                                isNull()
                        )
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                                .param(
                                        "search",
                                        "Microsoft"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].company")
                                .value("Microsoft")
                );

        verify(
                jobApplicationService
        ).searchApplications(
                "Microsoft",
                null,
                null,
                null
        );
    }

    @Test
    void shouldFilterApplicationsByStatus()
            throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Accenture",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .searchApplications(
                                isNull(),
                                eq(ApplicationStatus.APPLIED),
                                isNull(),
                                isNull()
                        )
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                                .param(
                                        "status",
                                        "APPLIED"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("APPLIED")
                );
    }

    @Test
    void shouldFilterApplicationsByPriority()
            throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Accenture",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .searchApplications(
                                isNull(),
                                isNull(),
                                eq(Priority.HIGH),
                                isNull()
                        )
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                                .param(
                                        "priority",
                                        "HIGH"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].priority")
                                .value("HIGH")
                );
    }

    @Test
    void shouldSortApplicationsByDeadline()
            throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Accenture",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .searchApplications(
                                isNull(),
                                isNull(),
                                isNull(),
                                eq("deadline")
                        )
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                                .param(
                                        "sort",
                                        "deadline"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].company")
                                .value("Accenture")
                );
    }

    @Test
    void shouldSupportCombinedSearchStatusPriorityAndSort()
            throws Exception {

        JobApplication application =
                createApplication(
                        1L,
                        "Accenture",
                        ApplicationStatus.APPLIED
                );

        when(
                jobApplicationService
                        .searchApplications(
                                eq("Accenture"),
                                eq(ApplicationStatus.APPLIED),
                                eq(Priority.HIGH),
                                eq("deadline")
                        )
        ).thenReturn(
                List.of(
                        application
                )
        );

        mockMvc.perform(
                        get("/api/applications")
                                .param(
                                        "search",
                                        "Accenture"
                                )
                                .param(
                                        "status",
                                        "APPLIED"
                                )
                                .param(
                                        "priority",
                                        "HIGH"
                                )
                                .param(
                                        "sort",
                                        "deadline"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].company")
                                .value("Accenture")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("APPLIED")
                )
                .andExpect(
                        jsonPath("$[0].priority")
                                .value("HIGH")
                );
    }

    private JobApplication createApplication(
            Long id,
            String company,
            ApplicationStatus status) {

        return new JobApplication(
                id,
                company,
                "Software Engineering Graduate Programme",
                status,
                Priority.HIGH,
                LocalDate.of(2026, 9, 11),
                LocalDate.of(2026, 10, 20)
        );
    }
}