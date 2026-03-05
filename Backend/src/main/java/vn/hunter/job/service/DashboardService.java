package vn.hunter.job.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hunter.job.domain.response.DashboardDTO;
import vn.hunter.job.repository.CompanyRepository;
import vn.hunter.job.repository.JobRepository;
import vn.hunter.job.repository.ResumeRepository;
import vn.hunter.job.repository.RoleRepository;
import vn.hunter.job.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ResumeRepository resumeRepository;

    public DashboardDTO getDashboard() {
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalUsers(userRepository.count());
        dto.setTotalRoles(roleRepository.count());
        dto.setTotalJobs(jobRepository.count());
        dto.setTotalCompanies(companyRepository.count());
        dto.setTotalResumes(resumeRepository.count());

        return dto;
    }
}
