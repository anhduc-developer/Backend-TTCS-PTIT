package vn.hunter.job.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hunter.job.domain.Skill;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.JobRepository;
import vn.hunter.job.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public SkillService(SkillRepository skillRepository, JobRepository jobRepository) {
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public boolean isSkillExists(String name) {
        return this.skillRepository.findByName(name) != null ? true : false;
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill updateSkill(Skill updateSkill) {
        return this.skillRepository.save(updateSkill);
    }

    public Skill fetchSkillById(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        return skillOptional.isPresent() ? skillOptional.get() : null;
    }

    public ResultPaginationDTO getAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

    public void deleteSkill(Long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        Skill currentSkill = skill.get();

        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        currentSkill.getSubscribers().forEach(subs -> subs.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
    }

}
