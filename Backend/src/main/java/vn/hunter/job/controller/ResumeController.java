package vn.hunter.job.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.Job;
import vn.hunter.job.domain.Resume;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.response.ResCreateResumeDTO;
import vn.hunter.job.domain.response.ResFetchResumeDTO;
import vn.hunter.job.domain.response.ResUpdateResumeDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.service.ResumeService;
import vn.hunter.job.service.UserService;
import vn.hunter.job.util.SecurityUtil;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService,
            UserService userService,
            FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        boolean isIdExists = this.resumeService.checkResumeExistsByUserAndJob(resume);
        if (!isIdExists) {
            throw new IdInvalidException("User id / Job id không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a Resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume)
            throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(resume.getId());
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume with id = " + resume.getId() + " không tồn tại");
        }
        Resume newResume = resumeOptional.get();
        newResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(newResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a Resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume with id = " + id + " không tồn tại");
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if (resumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume with id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.handleGetUserByUsername(email);

        // Nếu là ADMIN -> xem tất cả resumes
        if (currentUser != null && currentUser.getRole() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {

            return ResponseEntity.ok().body(
                    this.resumeService.fetchAllResume(spec, pageable));
        }

        // Nếu không phải admin -> chỉ xem resume của company
        List<Long> arrJobIds = null;

        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();

            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();

                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs
                            .stream()
                            .map(Job::getId)
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(
                this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
