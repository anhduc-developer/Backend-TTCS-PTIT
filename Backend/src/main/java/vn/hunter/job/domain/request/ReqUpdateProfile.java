package vn.hunter.job.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hunter.job.util.constant.GenderEnum;

@Getter
@Setter
public class ReqUpdateProfile {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 1, message = "Tuổi phải lớn hơn 1")
    @Max(value = 120, message = "Tuổi không hợp lệ")
    private Integer age;

    @NotNull(message = "Giới tính không được để trống")
    private GenderEnum gender;

    private String address;
}