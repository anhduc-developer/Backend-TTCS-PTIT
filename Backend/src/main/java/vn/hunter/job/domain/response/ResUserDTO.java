package vn.hunter.job.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hunter.job.util.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private Long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private UserCompany company;
    private RoleUser role;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCompany {
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    public static class RoleUser {
        private Long id;
        private String name;
    }

}
