package com.imigishalink.messages;

import com.imigishalink.common.BaseEntity;
import com.imigishalink.communities.Community;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {
    
    @Column(name = "content", nullable = false, length = 2000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private MessageType type = MessageType.TEXT;
    
    @Column(name = "attachment_url")
    private String attachmentUrl;
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;
    
    // Many-to-one with User (Sender)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    // Many-to-one with User (Receiver) - For private messages
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;
    
    // Many-to-one with Community - For community messages
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    
    // Self-referencing: Parent message for replies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;
    
    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.Set<Message> replies = new java.util.HashSet<>();
    
    public boolean isPrivateMessage() {
        return receiver != null && community == null;
    }
    
    public boolean isCommunityMessage() {
        return community != null && receiver == null;
    }
}

enum MessageType {
    TEXT,
    IMAGE,
    FILE,
    SYSTEM
}