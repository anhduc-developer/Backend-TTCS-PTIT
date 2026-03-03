package vn.hunter.job.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Skill;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.service.SkillService;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;
import org.springframework.web.bind.annotation.GetMapping;

import com.turkraft.springfilter.boot.Filter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isSkillExists = this.skillService.isSkillExists(skill.getName());
        if (isSkillExists && skill.getName() != null) {
            throw new IdInvalidException("Skill đã tổn tại");
        }
        Skill newSkill = this.skillService.createSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills/{id}")
    @ApiMessage("Update a Skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill newSkill)
            throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(newSkill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + newSkill.getId() + " không tồn tại");
        }
        boolean isSkillExists = this.skillService.isSkillExists(newSkill.getName());
        if (isSkillExists && newSkill.getName() != null) {
            throw new IdInvalidException("Skill " + newSkill.getName() + " đã tổn tại");
        }
        currentSkill.setName(newSkill.getName());
        Skill skill = this.skillService.updateSkill(currentSkill);
        return ResponseEntity.ok().body(skill);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("fetch skill by id")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") Long id) throws IdInvalidException {
        Skill skill = this.skillService.fetchSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Skill với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.getAllSkills(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a Skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") Long id) {
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
