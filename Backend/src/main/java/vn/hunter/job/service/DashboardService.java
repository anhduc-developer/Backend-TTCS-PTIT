package vn.hunter.job.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hunter.job.domain.Job;
import vn.hunter.job.domain.Resume;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.response.DashboardDTO;
import vn.hunter.job.repository.CompanyRepository;
import vn.hunter.job.repository.JobRepository;
import vn.hunter.job.repository.PermissionRepository;
import vn.hunter.job.repository.ResumeRepository;
import vn.hunter.job.repository.RoleRepository;
import vn.hunter.job.repository.SkillRepository;
import vn.hunter.job.repository.SubscriberRepository;
import vn.hunter.job.repository.UserRepository;
import vn.hunter.job.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final PermissionRepository permissionRepository;
    private final SubscriberRepository subscriberRepository;

    public DashboardDTO getDashboard() {
        String email = SecurityUtil.getCurrentUserLogin().get();
        User currentUser = this.userRepository.findByEmail(email);
        DashboardDTO dto = new DashboardDTO();
        if (currentUser.getRole().getName().equals("ADMIN")) {
            dto.setTotalUsers(userRepository.count());
            dto.setTotalRoles(roleRepository.count());
            dto.setTotalJobs(jobRepository.count());
            dto.setTotalCompanies(companyRepository.count());
            dto.setTotalResumes(resumeRepository.count());
            dto.setTotalSkills(this.skillRepository.count());
            dto.setTotalPermissions(this.permissionRepository.count());
            dto.setTotalSubscribers(this.subscriberRepository.count());
        } else {
            List<Job> jobs = this.jobRepository.findAll();
            long totalJob = 0;
            for (Job job : jobs) {
                if (job.getCompany().getName().equals(currentUser.getCompany().getName())) {
                    ++totalJob;
                }
            }
            List<Resume> resumes = this.resumeRepository.findAll();
            long totalResume = 0;
            for (Resume resume : resumes) {
                if (resume.getJob().getCompany().getName().equals(currentUser.getCompany().getName())) {
                    ++totalResume;
                }
            }
            dto.setTotalResumes(totalResume);
            dto.setTotalJobs(totalJob);
            dto.setTotalSkills(this.skillRepository.count());
        }
        return dto;
    }
}
