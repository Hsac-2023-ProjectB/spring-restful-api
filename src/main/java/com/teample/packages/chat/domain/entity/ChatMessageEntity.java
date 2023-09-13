package com.teample.packages.chat.domain.entity;

import com.teample.packages.chat.dto.ChatMessageDTO;
import com.teample.packages.chat.dto.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="ChatMessage")
public class ChatMessageEntity {

    @Id
    @Column(name="messageId", length = 36, nullable = false)
    // 메시지 ID
    private String messageId;

    @Column(name="roomId", length = 50, nullable = false)
    // 채팅방 ID
    private String roomId;

    @Column(name="senderName", length = 10, nullable = false)
    // 송신자 이름
    private String senderName;

    @Column(name="senderId", length = 20, nullable = false)
    // 송신자 id
    private Long senderId;

    @Column(name="message", length = 200, columnDefinition = "TEXT", nullable = false)
    //내용
    private String message;

    @Column(name="type", length = 10, nullable = true)
    //메시지 타입
    private MessageType type;

    @Column(name="checked", length = 10, nullable = true)
    // 읽음 확인
    private Boolean checked;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageEntity(String messageId, String roomId, String senderName, Long senderId, String message, MessageType type, boolean checked){
        this.messageId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
        this.checked = checked;

    }

    public static ChatMessageDTO toDTO(ChatMessageEntity entity){
        return ChatMessageDTO.builder()
                .messageId(entity.getMessageId())
                .roomId(entity.getRoomId())
                .senderName(entity.getSenderName())
                .senderId(entity.getSenderId())
                .message(entity.getMessage())
                .type(entity.getType())
                .checked(entity.getChecked())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateChecked(Boolean checked) {
        this.checked = checked;
    }
    public void deleteMessage(String message) {
        this.message = message;
        this.type = MessageType.DELETED;
    }
}
