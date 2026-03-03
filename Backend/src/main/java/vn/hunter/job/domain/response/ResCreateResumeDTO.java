package vn.hunter.job.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResCreateResumeDTO {
    private Long id;
    private Instant createdAt;
    private String createdBy;
}
