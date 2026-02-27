package vn.localhelp.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.request.user.UpdateUserRequest;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponse getMyProfile(String firebaseUid) {
    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    return this.userMapper.toResponse(user);
  }

  public UserResponse updateProfile(String firebaseUid, UpdateUserRequest request) {
    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (request.getFullName() != null) {
      user.setFullName(request.getFullName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
    if (request.getAvatarUrl() != null) {
      user.setAvatarUrl(request.getAvatarUrl());
    }
    if (request.getGender() != null) {
      user.setGender(request.getGender());
    }

    return userMapper.toResponse(userRepository.save(user));
  }
}
