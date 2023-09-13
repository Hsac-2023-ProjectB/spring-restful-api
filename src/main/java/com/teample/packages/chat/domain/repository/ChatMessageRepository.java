package com.teample.packages.chat.domain.repository;

import com.teample.packages.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // createdAt 필드 오름차순으로 정렬해서 반환
    List<ChatMessageEntity> findByRoomIdOrderByCreatedAt(String roomId);
    List<ChatMessageEntity> findByRoomIdAndCheckedOrderByCreatedAt(String roomId, Boolean checked);

    List<ChatMessageEntity> findByRoomIdOrderByCreatedAtDesc(String roomId);
    ChatMessageEntity findByMessageId(String messageId);
    void deleteAllByRoomId(String roomId);
}
