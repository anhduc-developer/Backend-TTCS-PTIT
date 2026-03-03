package vn.hunter.job.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.Job;
import vn.hunter.job.domain.Skill;
import vn.hunter.job.domain.response.ResCreateJobDTO;
import vn.hunter.job.domain.response.ResUpdateJobDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.CompanyRepository;
import vn.hunter.job.repository.JobRepository;
import vn.hunter.job.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public boolean isJobExists(Long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.isPresent() ? true : false;
    }

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;

    }

    public ResCreateJobDTO createJob(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        if (job.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if (companyOptional.isPresent()) {
                job.setCompany(companyOptional.get());
            }
        }
        Job currentJob = this.jobRepository.save(job);
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public ResUpdateJobDTO updateJob(Job job, Job jobInDB) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }
        if (job.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());

            if (companyOptional.isPresent()) {
                jobInDB.setCompany(companyOptional.get());
            }
        }
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());
        Job currentJob = this.jobRepository.save(jobInDB);
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getCreatedAt());
        dto.setUpdatedBy(currentJob.getCreatedBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public Job fetchJobById(Long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.isPresent() ? jobOptional.get() : null;
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageSize() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());
        res.setMeta(mt);
        res.setResult(pageJob.getContent());
        return res;
    }

    public void deleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }

}
