package vn.hunter.job.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hunter.job.util.constant.LevelEnum;

@Setter
@Getter
public class ResUpdateJobDTO {
    private Long id;
    private String name;
    private double salary;
    private int quantity;
    private String location;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant updatedAt;
    private String updatedBy;
    private List<String> skills;
}
