package vn.localhelp.core.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;
import vn.localhelp.core.service.ConversationService;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController {
  private final ConversationService conversationService;

  @ApiMessage("Start a conversation with a user successfully")
  @PostMapping("/start")
  public ResponseEntity<ConversationResponse> startConversation(@RequestParam Long targetUserId) {
    return ResponseEntity.ok(this.conversationService.getOrCreateConversation(targetUserId));
  }

  @ApiMessage("Fetch all my conversations successfully")
  @GetMapping
  public ResponseEntity<List<ConversationResponse>> getAllConversations() {
    return ResponseEntity.ok(this.conversationService.getMyConversations());
  }
}
