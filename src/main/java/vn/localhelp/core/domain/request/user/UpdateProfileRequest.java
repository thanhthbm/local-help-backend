package vn.localhelp.core.domain.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.localhelp.core.util.constant.GenderEnum;

@Data
public class UpdateProfileRequest {

    /** Họ tên hiển thị trên hồ sơ người dùng. */
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    /** Số điện thoại liên hệ, cho phép số, khoảng trắng, dấu + và dấu -. */
    @Pattern(regexp = "^[0-9+\\-\\s]{0,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    /** Giới tính người dùng, dùng enum để đồng bộ với app. */
    private GenderEnum gender;

    /** Mô tả ngắn về bản thân hiển thị trong thẻ hồ sơ. */
    @Size(max = 500, message = "Giới thiệu không được vượt quá 500 ký tự")
    private String bio;

    /** URL ảnh đại diện sau khi client upload lên Cloudinary. */
    private String avatarUrl;
}
