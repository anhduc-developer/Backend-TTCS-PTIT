package vn.hunter.job.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hunter.job.domain.response.ResJobRecommendationDTO;
import vn.hunter.job.repository.JobRepository;
import vn.hunter.job.util.constant.JobStateEnum;

@Service
public class JobRecommendationService {
    private final JobRepository jobRepository;

    public JobRecommendationService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<ResJobRecommendationDTO.RecommendedJob> recommendJobsBySkills(List<String> candidateSkills, int limit) {
        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> normalizedSkills = candidateSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (normalizedSkills.isEmpty()) {
            return Collections.emptyList();
        }

        return jobRepository.findAll().stream()
                .filter(job -> job != null && job.isActive() && job.getStatus() == JobStateEnum.APPROVED)
                .map(job -> {
                    Set<String> jobSkillSet = job.getSkills() == null ? Collections.emptySet()
                            : job.getSkills().stream()
                                    .map(skill -> skill == null || skill.getName() == null ? ""
                                            : skill.getName().trim().toLowerCase())
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toSet());

                    Set<String> matched = jobSkillSet.stream()
                            .filter(normalizedSkills::contains)
                            .collect(Collectors.toSet());

                    ResJobRecommendationDTO.RecommendedJob dto = new ResJobRecommendationDTO.RecommendedJob();
                    dto.setId(job.getId());
                    dto.setName(job.getName());
                    dto.setLocation(job.getLocation());
                    dto.setCompanyName(job.getCompany() != null ? job.getCompany().getName() : null);
                    dto.setSalary(job.getSalary());
                    dto.setLevel(job.getLevel() != null ? job.getLevel().name() : null);
                    dto.setDescription(job.getDescription());
                    dto.setSkills(job.getSkills() == null ? Collections.emptyList()
                            : job.getSkills().stream().map(skill -> skill.getName()).collect(Collectors.toList()));
                    dto.setMatchedSkills(matched.stream().sorted().collect(Collectors.toList()));
                    dto.setMatchScore(matched.size());
                    dto.setHot(job.isHot());
                    dto.setStatus(job.getStatus() != null ? job.getStatus().name() : null);
                    return dto;
                })
                .filter(dto -> dto.getMatchScore() != null && dto.getMatchScore() > 0)
                .sorted((a, b) -> {
                    int compare = Integer.compare(b.getMatchScore(), a.getMatchScore());
                    if (compare != 0) {
                        return compare;
                    }
                    return Boolean.compare(b.isHot(), a.isHot());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}
