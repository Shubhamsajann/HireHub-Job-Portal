package com.hirehub.service;

import com.hirehub.dto.JobApplicationResponse;
import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse applyForJob(Long candidateId, Long jobId);

    List<JobApplicationResponse> getAllApplications();

    JobApplicationResponse updateApplicationStatus(
            Long applicationId, String status);
    List<JobApplicationResponse> getApplicationsByCandidateId(
            Long candidateId);

}