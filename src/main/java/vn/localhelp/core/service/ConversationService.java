package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.ConversationMapper;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;
import vn.localhelp.core.repository.ConversationRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.error.NotFoundException;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final ConversationMapper conversationMapper;
  private final UserMapper userMapper;

  @Transactional
  public ConversationResponse getOrCreateConversation(Long targetUserId){
    String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> currentUserOpt = userRepository.findByFirebaseUid(firebaseUid);
    if (currentUserOpt.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    User currentUser = currentUserOpt.get();
    if (targetUserId.equals(currentUser.getId())) {
      throw new RuntimeException("You cannot text yourself");
    }

    Optional<Conversation> conversationOpt = conversationRepository.findConversationBetweenUsers(currentUser.getId(), targetUserId);

    if (conversationOpt.isPresent()) {
      return conversationMapper.toResponse(conversationOpt.get(), currentUser.getId(), userMapper);
    }

    User targetUser = userRepository.findById(targetUserId)
        .orElseThrow(() -> new NotFoundException("Target user not found"));

    Conversation conversation = Conversation.builder()
        .user1(currentUser)
        .user2(targetUser)
        .build();

    conversationRepository.save(conversation);
    return conversationMapper.toResponse(conversation, currentUser.getId(), userMapper);
  }

  public List<ConversationResponse> getMyConversations(){
    String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    List<Conversation> conversations = conversationRepository.getConversationsByUserId(currentUser.getId());

    return conversations.stream().map(
        conv -> conversationMapper.toResponse(conv, currentUser.getId(), userMapper))
        .toList();
  }

}
