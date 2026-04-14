package vn.localhelp.core.domain.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.localhelp.core.util.constant.GenderEnum;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^[0-9+\\-\\s]{0,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private GenderEnum gender;

    @Size(max = 500, message = "Giới thiệu không được vượt quá 500 ký tự")
    private String bio;

    private String avatarUrl;
}
