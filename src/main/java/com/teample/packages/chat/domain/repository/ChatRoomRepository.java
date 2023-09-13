package com.teample.packages.chat.domain.repository;

import com.teample.packages.chat.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    List<ChatRoomEntity> findBySubmembersContaining(String keyword);

    ChatRoomEntity findByRoomId(String roomId);
}
