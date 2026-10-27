
package com.hirehub.service;

import com.hirehub.entity.Job;
import com.hirehub.entity.User;

import java.util.List;
import java.util.Optional;

public interface JobService {

    Job createJob(Job job);

    List<Job> getAllJobs();

    Optional<Job> getJobById(Long id);

    Job updateJob(Long id, Job updatedJob, User recruiter);

    void deleteJob(Long id, User recruiter);
}
