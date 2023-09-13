package com.teample.packages.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teample.packages.chat.domain.entity.ChatMessageEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
public class ChatMessageDTO {

        // 메시지 ID
        private String messageId;

        // 채팅방 ID
        private String roomId;

        // 송신자 이름
        private String senderName;

        // 송신자 id
        private Long senderId;

        //내용
        private String message;

        //메시지 타입
        private MessageType type;

        // 읽음 확인
        private Boolean checked;

        // 생성 시각
        private LocalDateTime createdAt;

        public static ChatMessageEntity toEntity(ChatMessageDTO dto){
                return ChatMessageEntity.builder()
                        .messageId(dto.getMessageId())
                        .roomId(dto.getRoomId())
                        .senderName(dto.getSenderName())
                        .senderId(dto.getSenderId())
                        .message(dto.getMessage())
                        .type(dto.getType())
                        .checked(dto.getChecked())
                        .build();
        }
}
