package vn.hunter.job.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hunter.job.util.constant.LevelEnum;

@Setter
@Getter
public class ResCreateJobDTO {
    private Long id;
    private String name;
    private double salary;
    private int quantity;
    private String location;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private String createdBy;
    private List<String> skills;
}
