package com.imigishalink.messages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
           "(m.sender.id = :otherUserId AND m.receiver.id = :userId) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(
            @Param("userId") Long userId,
            @Param("otherUserId") Long otherUserId,
            Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.community.id = :communityId ORDER BY m.createdAt DESC")
    Page<Message> findByCommunityId(@Param("communityId") Long communityId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    Page<Message> findUnreadMessages(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT m.sender.id FROM Message m WHERE m.receiver.id = :userId " +
           "UNION SELECT DISTINCT m.receiver.id FROM Message m WHERE m.sender.id = :userId")
    List<Long> findContactIds(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.parentMessage.id = :parentId")
    Page<Message> findReplies(@Param("parentId") Long parentId, Pageable pageable);
}