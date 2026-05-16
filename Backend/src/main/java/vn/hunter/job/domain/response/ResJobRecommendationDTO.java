package vn.hunter.job.domain.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResJobRecommendationDTO {
    private Long cvId;
    private String fileName;
    private List<String> skills;
    private List<RecommendedJob> jobs;

    @Getter
    @Setter
    public static class RecommendedJob {
        private Long id;
        private String name;
        private String location;
        private String companyName;
        private double salary;
        private String level;
        private String description;
        private List<String> skills;
        private List<String> matchedSkills;
        private Integer matchScore;
        private boolean hot;
        private String status;
    }
}
