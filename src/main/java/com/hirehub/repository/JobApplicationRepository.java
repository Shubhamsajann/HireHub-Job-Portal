package com.hirehub.repository;

import com.hirehub.dto.JobApplicationResponse;
import com.hirehub.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByCandidateId(Long candidateId);
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);



}
