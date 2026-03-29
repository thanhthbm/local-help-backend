package vn.localhelp.core.domain.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMapper {

  @Mapping(target = "partner", expression = "java(resolvePartner(conversation, currentUserId, userMapper))")
  ConversationResponse toResponse(
      Conversation conversation,
      @Context Long currentUserId,
      @Context UserMapper userMapper
  );

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
