package com.teample.packages.chat.service;


import com.teample.packages.chat.domain.entity.ChatMessageEntity;
import com.teample.packages.chat.domain.repository.ChatMessageRepository;
import com.teample.packages.chat.dto.ChatMessageDTO;
import com.teample.packages.chat.dto.ChatRoomDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    /**
     * 메시지를 db에 저장
     */
    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageDTO chatMessageDTO) {
        return chatMessageRepository.save(ChatMessageDTO.toEntity(chatMessageDTO));
    }

    /**
     * 채팅방 id로 메시지 객체들을 조회
     */
    @Transactional
    public List<ChatMessageDTO> getMessagesByRoomId(String roomId) {
        // 리스트로 하나씩 꺼내서 바꿔야함.
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findByRoomIdOrderByCreatedAt(roomId);
        List<ChatMessageDTO> chatMessageDTOS = new ArrayList<>();

        if(chatMessageEntities.isEmpty()) return chatMessageDTOS;

        for(ChatMessageEntity chatMessageEntity : chatMessageEntities) {
            chatMessageDTOS.add(ChatMessageEntity.toDTO(chatMessageEntity));
        }

        return chatMessageDTOS;
    }

    /**
     * 채팅방 입장 시 내가 읽지 않은 메시지들을 읽음 처리한다.
     */
    @Transactional
    public int turnCheckedTrue(ChatMessageDTO chatMessageDTO) {
        int count = 0;

        // 내가 참여 중인 채팅방에서 읽지 않은 메시지들만 select
        List<ChatMessageEntity> messages =
                chatMessageRepository.findByRoomIdAndCheckedOrderByCreatedAt(chatMessageDTO.getRoomId(), false);

        // 해당하는 데이터가 없으면 0 반환
        if(messages == null) return count;

        for(ChatMessageEntity c : messages) {
            // 상대방이 보낸 메시지가 읽지 않은 상태일 때 읽음으로 전환
            if(!c.getSenderId().equals(chatMessageDTO.getSenderId())) {
                // checked 필드 업데이트
                c.updateChecked(true);
                chatMessageRepository.save(c);
                count++;
            }
        }

        return count;
    }

    /**
     * 채팅방 별로 내가 보낸 메시지가 아닌 것 중 읽지 않은 메시지의 수를 반환한다.
     * Map<채팅방Id, 읽지 않은 메시지 수> 형태로 반환한다.
     */
    public Map<String, String> getUnchecked(List<ChatRoomDTO> chatRoomDTOList, Long senderId) {

        Map<String, String> result = new HashMap<>();
        for(ChatRoomDTO room : chatRoomDTOList) {
            int cnt = 0;
            List<ChatMessageDTO> msgList = getMessagesByRoomId(room.getRoomId());
            if(msgList == null) continue;       // 내가 참여중인 채팅방에서 작성된 메시지가 없는 경우 다음 채팅방으로 넘어간다.

            for(ChatMessageDTO msg : msgList) {
                // 내가 참여중인 채팅방에서, 상대방이 보낸 메시지 중, 확인 안한것
                if(msg.getRoomId().contains(senderId.toString())
                        && !(msg.getSenderId() == senderId) && msg.getChecked() == false) {
                    cnt++;
                }
            }
            result.put(room.getRoomId(), Integer.toString(cnt));
        }

        return result;
    }

    /**
     * 내가 읽지 않은 상대방의 채팅이 있는지 판별
     */
    public Boolean notChecked(ChatMessageDTO message) {
        Boolean result = false;
        Boolean temp = false;

        // 채팅방의 채팅을 최신 순서로 받아와서 처리
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(message.getRoomId());
        for(ChatMessageEntity c : chatMessageEntities) {
            // 내가 보낸 채팅인 경우 temp는 true
            if(temp == false && c.getSenderId() == message.getSenderId()) {
                temp = true;
                continue;
            }

            // 내가 보낸 채팅 바로 이전 채팅이 상대방의 채팅인 경우 true 리턴
            if(temp == true && c.getSenderId() != message.getSenderId()) {
                result = true;
                return result;
            }
            else temp = false;
        }

        return result;
    }

    /**
     * 채팅 상대 id 반환
     */
    public String getTargetId(ChatMessageDTO message) {
        String[] temp = message.getRoomId().split("-");
        String targetId = temp[0].equals(message.getSenderId().toString()) ? temp[1] : temp[0];

        return targetId;
    }

    /**
     * 메시지 삭제
     */
    @Transactional
    public ChatMessageDTO deleteMessage(String messageId) {
        ChatMessageEntity msg = chatMessageRepository.findByMessageId(messageId);
        msg.deleteMessage("삭제된 메시지입니다.");
        chatMessageRepository.save(msg);
        return ChatMessageEntity.toDTO(msg);
    }

    /**
     * 한 채팅방의 모든 메시지 삭제
     */
    @Transactional
    public void deleteAllMessage(String roomId) {
        chatMessageRepository.deleteAllByRoomId(roomId);
    }
}
