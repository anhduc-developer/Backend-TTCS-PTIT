package vn.hunter.job.domain.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiResumeAnalysisDTO {
    private String fileName;
    private String text;
    private List<String> skills;
    private String summary;
}
