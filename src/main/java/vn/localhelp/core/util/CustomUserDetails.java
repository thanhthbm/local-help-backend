package vn.localhelp.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.localhelp.core.domain.entity.User;

/**
 * Principal đặt trong SecurityContext sau khi FirebaseAuthFilter xác thực token.
 *
 * firebaseUid dùng để đối chiếu với Firebase, userEntity là bản ghi User nội bộ.
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails {
    private String firebaseUid;
    private User userEntity;

    @Override
    public String toString(){
        return firebaseUid;
    }
}
