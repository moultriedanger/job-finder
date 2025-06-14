package com.moultriedanger.mljobfinder.job;
import java.util.ArrayList;
import java.util.List;

import com.moultriedanger.mljobfinder.company.Company;
import com.moultriedanger.mljobfinder.company.CompanyRepository;
import com.moultriedanger.mljobfinder.job.dto.JobRequest;
import com.moultriedanger.mljobfinder.job.dto.JobResponse;
import com.moultriedanger.mljobfinder.job.mapper.JobResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*")
@RestController
public class JobController {

    private JobRepository jobRepository;
    private CompanyRepository companyRepository;
    private JobResponseMapper jobResponseMapper;

    JobController(JobRepository jobRepository, CompanyRepository companyRepository, JobResponseMapper jobResponseMapper) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobResponseMapper = jobResponseMapper;

    }

    @GetMapping("/jobs")
    public List<JobResponse> all() {

        List<Job> jRepo = jobRepository.findAll();

        List<JobResponse> jobs = new ArrayList<>();

        for (Job j: jRepo) {
            JobResponse jobDto = jobResponseMapper.toResponseDto(j);

            jobs.add(jobDto);
        }

        return jobs;
    }

    @GetMapping("/jobs/{id}")
    public JobResponse getJobById(@PathVariable Long id){

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));

        JobResponse jobDTO = jobResponseMapper.toResponseDto(job);

        return jobDTO;
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> addJob(@Valid @RequestBody JobRequest jobDTO){

        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found with id: " + jobDTO.getCompanyId()));

        Job job = new Job();
        job.setJobTitle(jobDTO.getJobTitle());
        job.setJobDescription(jobDTO.getJobDescription());
        job.setSeniorityLevel(jobDTO.getSeniorityLevel());
        job.setMaxSalary(jobDTO.getMaxSalary());
        job.setLocation(jobDTO.getLocation());
        job.setPostingUrl(jobDTO.getPostingUrl());
        job.setCompany(company);

        jobRepository.save(job);
        return new ResponseEntity<>(job, HttpStatus.CREATED);
    }

    //PUT Method that allows you to update an existing job and company that it belongs to
    //will not work because job constructor changed!
    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@Valid @PathVariable Long id, @RequestBody JobRequest jobDTO){

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));

        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found with id: " + id));

        Company previousCompany = job.getCompany();

        job.setJobTitle(jobDTO.getJobTitle());
        job.setJobDescription(jobDTO.getJobDescription());
        job.setSeniorityLevel(jobDTO.getSeniorityLevel());
        job.setMaxSalary(jobDTO.getMaxSalary());
        job.setLocation(jobDTO.getLocation());
        job.setPostingUrl(jobDTO.getPostingUrl());
        job.setCompany(company);

        //need to delete the previous job from the company provided
        List<Job> previousCompanyJobs = previousCompany.getJobs();


        for (Job j: previousCompanyJobs){
            if (j.getJobId().equals(job.getJobId())){
                previousCompanyJobs.remove(j);
                break;
            }
        }

        return new ResponseEntity<Job>(job, HttpStatus.OK);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> deleteJobById(@Valid @PathVariable Long id){

        Job job = jobRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found with id: " + id));

        JobResponse jobDTO = jobResponseMapper.toResponseDto(job);

        jobRepository.delete(job);

        Company company = job.getCompany();

        List<Job> jobList = company.getJobs();

        for (Job j: jobList){
            if (j.getJobId().equals(job.getJobId())) {
                jobList.remove(j);
                break;
            }
        }

        return new ResponseEntity<JobResponse>(jobDTO, HttpStatus.OK);
    }
}
