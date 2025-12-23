package com.imigishalink.messages;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.EmailService;
import com.imigishalink.common.PageResponse;
import com.imigishalink.communities.Community;
import com.imigishalink.communities.CommunityRepository;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final EmailService emailService;
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<Message>>> getConversation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findConversation(currentUser.getId(), userId, pageable);
        
        PageResponse<Message> response = new PageResponse<>(messages);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/community/{communityId}")
    @PreAuthorize("@communitySecurity.isAdmin(#communityId, authentication) or @communitySecurity.isMember(#communityId, authentication)")
    public ResponseEntity<ApiResponse<PageResponse<Message>>> getCommunityMessages(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findByCommunityId(communityId, pageable);
        
        PageResponse<Message> response = new PageResponse<>(messages);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<PageResponse<Message>>> getUnreadMessages(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findUnreadMessages(currentUser.getId(), pageable);
        
        PageResponse<Message> response = new PageResponse<>(messages);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(
            @AuthenticationPrincipal User currentUser) {
        
        long unreadCount = messageRepository.countUnreadMessages(currentUser.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Message message) {
        
        message.setSender(currentUser);
        
        if (message.getReceiver() != null && message.getReceiver().getId() != null) {
            Long receiverId = Objects.requireNonNull(message.getReceiver().getId(), "Receiver ID cannot be null");
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));
            message.setReceiver(receiver);
            
            // Send via WebSocket
            String receiverEmail = Objects.requireNonNull(receiver.getEmail(), "Receiver email cannot be null");
            messagingTemplate.convertAndSendToUser(
                    receiverEmail,
                    "/queue/messages",
                    message
            );
        } else if (message.getCommunity() != null && message.getCommunity().getId() != null) {
            Long communityId = Objects.requireNonNull(message.getCommunity().getId(), "Community ID cannot be null");
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Community not found"));
            message.setCommunity(community);
            
            // Send to community topic
            messagingTemplate.convertAndSend(
                    "/topic/community/" + communityId,
                    message
            );
        } else {
            throw new RuntimeException("Either receiver or community must be specified");
        }
        
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", savedMessage));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id) {
        
        Long messageId = Objects.requireNonNull(id, "Message ID cannot be null");
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to mark this message as read");
        }
        
        message.setRead(true);
        messageRepository.save(message);
        
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }
    
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<?>> getContacts(@AuthenticationPrincipal User currentUser) {
        var contactIds = Objects.requireNonNull(messageRepository.findContactIds(currentUser.getId()), "Contact IDs cannot be null");
        var contacts = userRepository.findAllById(contactIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("contacts", contacts);
        response.put("totalContacts", contacts.size());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // Public contact endpoint - sends email to admin
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendContactMessage(
            @RequestBody Map<String, String> contactForm) {
        
        String name = contactForm.get("name");
        String email = contactForm.get("email");
        String topic = contactForm.get("topic");
        String phone = contactForm.get("phone");
        String message = contactForm.get("message");
        
        if (name == null || email == null || message == null) {
            throw new RuntimeException("Name, email, and message are required");
        }
        
        // Find admin user
        User admin = userRepository.findByEmail("admin@imigishalink.rw")
                .orElse(null);
        
        String adminEmail = admin != null ? admin.getEmail() : "admin@imigishalink.rw";
        
        // Create email body
        String emailBody = String.format(
            "New Contact Form Submission\n\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Topic: %s\n" +
            "Phone: %s\n\n" +
            "Message:\n%s",
            name,
            email,
            topic != null ? topic : "Not specified",
            phone != null ? phone : "Not provided",
            message
        );
        
        // Send email to admin
        try {
            emailService.sendEmail(adminEmail, "Contact Form: " + (topic != null ? topic : "General Support"), emailBody);
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to send contact email: " + e.getMessage());
        }
        
        // Also save as message if user is logged in (optional - for future use)
        // For now, just send email
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Your message has been sent successfully. We'll get back to you soon!");
        
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", response));
    }
    
    // WebSocket endpoint
    @MessageMapping("/chat.send")
    public void sendMessageViaWebSocket(@Payload Message message) {
        // Save message
        Message savedMessage = Objects.requireNonNull(message, "Message cannot be null");
        messageRepository.save(savedMessage);
        
        // Route message based on type
        if (savedMessage.isPrivateMessage()) {
            String receiverEmail = Objects.requireNonNull(savedMessage.getReceiver().getEmail(), "Receiver email cannot be null");
            messagingTemplate.convertAndSendToUser(
                    receiverEmail,
                    "/queue/messages",
                    savedMessage
            );
        } else if (savedMessage.isCommunityMessage()) {
            messagingTemplate.convertAndSend(
                    "/topic/community/" + savedMessage.getCommunity().getId(),
                    savedMessage
            );
        }
    }
}