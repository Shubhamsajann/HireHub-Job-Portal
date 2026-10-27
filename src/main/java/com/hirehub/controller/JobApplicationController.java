
package com.hirehub.controller;

import com.hirehub.dto.JobApplicationResponse;
import com.hirehub.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;


import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService applicationService;

    private final UserRepository userRepository;

    public JobApplicationController(
            JobApplicationService applicationService,
            UserRepository userRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }



    @PostMapping("/candidate/{candidateId}/job/{jobId}")
    public ResponseEntity<JobApplicationResponse> applyForJob(
            @PathVariable Long candidateId,
            @PathVariable Long jobId,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if (!"CANDIDATE".equals(loggedInUser.getRole().name())
                || !loggedInUser.getId().equals(candidateId)) {
            throw new AccessDeniedException(
                    "You can only apply using your own candidate ID"
            );
        }

        JobApplicationResponse application =
                applicationService.applyForJob(candidateId, jobId);

        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }


    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(
                applicationService.getAllApplications());
    }


    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if (!"RECRUITER".equals(loggedInUser.getRole().name())) {
            throw new AccessDeniedException(
                    "Only recruiters can update application status"
            );
        }

        JobApplicationResponse response =
                applicationService.updateApplicationStatus(
                        applicationId, status);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<JobApplicationResponse>>
    getApplicationsByCandidateId(
            @PathVariable Long candidateId,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if ("CANDIDATE".equals(loggedInUser.getRole().name())
                && !loggedInUser.getId().equals(candidateId)) {
            throw new AccessDeniedException(
                    "Candidates can only view their own applications"
            );
        }

        return ResponseEntity.ok(
                applicationService.getApplicationsByCandidateId(candidateId)
        );
    }


    private User getLoggedInUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        ));
    }
}
