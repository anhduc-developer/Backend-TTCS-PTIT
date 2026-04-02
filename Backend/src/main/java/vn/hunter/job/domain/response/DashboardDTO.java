package vn.hunter.job.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private long totalUsers;
    private long totalRoles;
    private long totalJobs;
    private long totalCompanies;
    private long totalResumes;
    private long totalSkills;
    private long totalPermissions;
    private long totalSubscribers;
}