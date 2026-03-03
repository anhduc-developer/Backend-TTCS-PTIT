package vn.hunter.job.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Job;
import vn.hunter.job.domain.response.ResCreateJobDTO;
import vn.hunter.job.domain.response.ResUpdateJobDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.service.JobService;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch Job By Id")
    public ResponseEntity<Job> fetchJobById(@PathVariable("id") Long id) throws IdInvalidException {
        Job job = this.jobService.fetchJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(job);
    }

    @GetMapping("/jobs")
    @ApiMessage("Get All Jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.getAllJobs(spec, pageable));
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new Job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(job));
    }

    @PutMapping("/jobs/{id}")
    @ApiMessage("Update a Job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job newJob, @PathVariable("id") Long id)
            throws Exception {
        Job job = this.jobService.fetchJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.updateJob(newJob, job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a Job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job với id = " + id + " không tồn tại");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.ok().body(null);
    }
}
