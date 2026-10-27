package com.hirehub.service;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobApplication;
import com.hirehub.entity.User;
import com.hirehub.exception.ResourceNotFoundException;
import com.hirehub.repository.JobApplicationRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import com.hirehub.dto.JobApplicationResponse;
import com.hirehub.exception.DuplicateApplicationException;


@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobApplicationServiceImpl(
            JobApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JobApplicationResponse applyForJob(Long candidateId, Long jobId) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate not found with ID: " + candidateId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with ID: " + jobId));

        if (applicationRepository
                .existsByCandidateIdAndJobId(candidateId, jobId)) {
            throw new DuplicateApplicationException(
                    "You have already applied for this job");
        }

        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJob(job);

        JobApplication saved = applicationRepository.save(application);

        return new JobApplicationResponse(
                saved.getId(),
                candidate.getId(),
                candidate.getName(),
                job.getId(),
                job.getTitle(),
                saved.getAppliedAt(),
                saved.getStatus()
        );
    }

    @Override
    public List<JobApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(application -> new JobApplicationResponse(
                        application.getId(),
                        application.getCandidate().getId(),
                        application.getCandidate().getName(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getAppliedAt(),
                        application.getStatus()
                ))
                .toList();
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(

            Long applicationId, String status) {

        if (!"PENDING".equals(status)
                && !"ACCEPTED".equals(status)
                && !"REJECTED".equals(status)) {

            throw new IllegalArgumentException(
                    "Invalid status. Use PENDING, ACCEPTED, or REJECTED");
        }

        JobApplication application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with ID: " + applicationId));

        application.setStatus(status);

        JobApplication updated = applicationRepository.save(application);

        return new JobApplicationResponse(
                updated.getId(),
                updated.getCandidate().getId(),
                updated.getCandidate().getName(),
                updated.getJob().getId(),
                updated.getJob().getTitle(),
                updated.getAppliedAt(),
                updated.getStatus()
        );
    }

    @Override
    public List<JobApplicationResponse> getApplicationsByCandidateId(
            Long candidateId) {

        return applicationRepository.findByCandidateId(candidateId)
                .stream()
                .map(application -> new JobApplicationResponse(
                        application.getId(),
                        application.getCandidate().getId(),
                        application.getCandidate().getName(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getAppliedAt(),
                        application.getStatus()
                ))
                .toList();
    }



}
