package com.teample.packages.chat.dto;

import com.teample.packages.chat.domain.entity.ChatMessageEntity;
import com.teample.packages.chat.domain.entity.ChatRoomEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private String roomId;
    private String roomName;
    private String submembers;

    public static ChatRoomEntity toEntity(ChatRoomDTO dto){
        return ChatRoomEntity.builder()
                .roomId(dto.getRoomId())
                .roomName(dto.getRoomName())
                .submembers(dto.getSubmembers())
                .build();
    }
}