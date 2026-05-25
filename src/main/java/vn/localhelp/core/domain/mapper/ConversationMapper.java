package vn.localhelp.core.domain.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;
/**
 * MapStruct Mapper chuyển đổi Conversation entity sang ConversationResponse DTO.
 *
 * <p>Điểm quan trọng: thay vì expose raw user1/user2, mapper xác định 'partner'
 * là người còn lại (không phải currentUser) để frontend dùng trực tiếp.</p>
 *
 * <p>Sử dụng @Context để truyền currentUserId và UserMapper vào default method,
 * vì MapStruct không tự inject được giá trị runtime vào biểu thức java(...).</p>
 *
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMapper {

  @Mapping(target = "partner", expression = "java(resolvePartner(conversation, currentUserId, userMapper))")
  ConversationResponse toResponse(
      Conversation conversation,
      @Context Long currentUserId,
      @Context UserMapper userMapper
  );
  /**
   * Xác định người dùng 'đối phương' (partner) trong conversation.
   *
   * <p>Logic: Nếu user1.id == currentUserId thì partner là user2, ngược lại partner là user1.</p>
   *
   * <p>Được gọi thông qua biểu thức @Mapping expression = "java(resolvePartner(...))".
   * Annotated @Named để MapStruct nhận diện đây là custom mapping method.</p>
   *
   * @param conversation   Entity cần map
   * @param currentUserId  ID của user hiện tại (để xác định đối phương)
   * @param userMapper     UserMapper để chuyển User entity → UserSummary
   * @return               UserSummary của người đối diện
   */
  @Named("resolvePartner")
  default vn.localhelp.core.domain.response.user.UserSummary resolvePartner(
      Conversation conversation,
      Long currentUserId,
      UserMapper userMapper) {

    if (conversation.getUser1().getId().equals(currentUserId)) {
      return userMapper.toSummary(conversation.getUser2());
    }
    else {
      return userMapper.toSummary(conversation.getUser1());
    }
  }
}
