package com.hirehub.dto;
import java.time.LocalDateTime;


public class JobApplicationResponse {


    private Long id;
    private Long candidateId;
    private String candidateName;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime appliedAt;
    private String status;



    public JobApplicationResponse() {
    }

    public JobApplicationResponse(Long id, Long candidateId, String candidateName, Long jobId, String jobTitle, LocalDateTime appliedAt, String status) {
        this.id = id;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.appliedAt = appliedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public String getStatus() {
        return status;
    }
}
