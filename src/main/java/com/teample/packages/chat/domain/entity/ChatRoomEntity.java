package com.teample.packages.chat.domain.entity;

import com.teample.packages.chat.dto.ChatRoomDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="ChatRoom")
public class ChatRoomEntity {

    @Id
    @Column(name="roomId", length = 50, nullable = false)
    private String roomId;

    @Column(name="roomName", length = 20, nullable = false)
    private String roomName;

    @Column(name="submembers", length = 50, nullable = false)
    private String submembers;

    @Builder
    public ChatRoomEntity(String roomId, String roomName, String submembers){
        this.roomId=roomId;
        this.roomName=roomName;
        this.submembers=submembers;
    }

    public static ChatRoomDTO toDTO(ChatRoomEntity entity) {
        return ChatRoomDTO.builder()
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .submembers(entity.getSubmembers())
                .build();
    }

    public void updateRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void updateSubmembers(String submembers) {this.submembers = submembers; }
}
