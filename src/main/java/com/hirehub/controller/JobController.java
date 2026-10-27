
package com.hirehub.controller;

import com.hirehub.entity.Job;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    public JobController(
            JobService jobService,
            UserRepository userRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    // Create a job for the logged-in recruiter
    @PostMapping
    public ResponseEntity<Job> createJob(
            @Valid @RequestBody Job job,
            Authentication authentication) {

        User recruiter = getLoggedInRecruiter(authentication);
        job.setRecruiter(recruiter);

        Job savedJob = jobService.createJob(job);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedJob);
    }

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // Get a job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update a job only if the recruiter owns it
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody Job job,
            Authentication authentication) {

        User recruiter = getLoggedInRecruiter(authentication);

        return ResponseEntity.ok(
                jobService.updateJob(id, job, recruiter)
        );
    }

    // Delete a job only if the recruiter owns it
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        User recruiter = getLoggedInRecruiter(authentication);
        jobService.deleteJob(id, recruiter);

        return ResponseEntity.noContent().build();
    }

    // Identify the authenticated recruiter
    private User getLoggedInRecruiter(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new AccessDeniedException(
                    "Authentication required");
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));

        if (user.getRole() == null
                || !"RECRUITER".equals(user.getRole().name())) {
            throw new AccessDeniedException(
                    "Only recruiters can perform this action");
        }

        return user;
    }
}
