package vn.hunter.job.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hunter.job.util.constant.GenderEnum;

@Setter
@Getter
public class ResUpdateUserDTO {
    private Long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updateAt;
    private CompanyUser company;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyUser {
        private Long id;
        private String name;
    }

}
