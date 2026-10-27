
package com.hirehub.service;

import com.hirehub.entity.Job;
import com.hirehub.entity.User;
import com.hirehub.exception.ResourceNotFoundException;
import com.hirehub.repository.JobRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    @Override
    public Job updateJob(Long id, Job updatedJob, User recruiter) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with ID: " + id));

        checkOwnership(existingJob, recruiter);

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setSalary(updatedJob.getSalary());
        existingJob.setEmploymentType(updatedJob.getEmploymentType());
        existingJob.setCompanyName(updatedJob.getCompanyName());

        return jobRepository.save(existingJob);
    }

    @Override
    public void deleteJob(Long id, User recruiter) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with ID: " + id));

        checkOwnership(existingJob, recruiter);

        jobRepository.delete(existingJob);
    }

    private void checkOwnership(Job job, User recruiter) {
        if (job.getRecruiter() == null
                || recruiter == null
                || !job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException(
                    "You cannot modify a job owned by another recruiter");
        }
    }
}
