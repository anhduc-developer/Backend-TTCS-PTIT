package vn.hunter.job.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hunter.job.util.constant.GenderEnum;

@Setter
@Getter
public class ResCreateUserDTO {
    private Long id;
    private String email;
    private String name;
    private int age;

    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    public static class CompanyUser {
        private Long id;
        private String name;
    }
}
