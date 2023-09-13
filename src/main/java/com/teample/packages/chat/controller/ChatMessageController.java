package com.teample.packages.chat.controller;

import com.teample.packages.chat.domain.entity.ChatMessageEntity;
import com.teample.packages.chat.dto.ChatRoomDTO;
import com.teample.packages.chat.dto.MessageType;
import com.teample.packages.chat.service.ChatMessageService;
import com.teample.packages.chat.dto.ChatMessageDTO;
import com.teample.packages.chat.service.ChatRoomService;
import com.teample.packages.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class ChatMessageController {

    private final SimpMessageSendingOperations sendingOperations; //특정 Broker로 메세지를 전달. SimpMessagingTemplate 와 같음.
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    /**
     * 채팅방에서 메시지를 전송하면 이곳으로 매핑된다.
     * Config에서 설정한 applicationDestinationPrefixes, @MessageMapping 경로가 병합
     * /pub/chat/message로 메시지를 보내면 이쪽으로 온다.
     */
    @MessageMapping("/chat/message")
    public void send(ChatMessageDTO message) {
        // 메시지 저장
        ChatMessageDTO saved = ChatMessageEntity.toDTO(chatMessageService.saveMessage(message));

        // 채팅방으로 메시지 다시 publish
        // /sub/chat/room/ 구독자에게 보낸다.
        // 생성 시각이 필요하기 때문에 Entity를 받아 DTO로 변형하여 전송한다.
        sendingOperations.convertAndSend("/sub/chat/room/" + saved.getRoomId(), saved);

        // 상대방에게 내가 채팅을 보냈음을 알리는 메시지를 보낸다.
        // 이 메시지로 인해 상대의 읽지 않은 메시지 수가 하나 더해진다.
        String targetId = chatMessageService.getTargetId(message);
        sendingOperations.convertAndSend("/sub/notification/" + targetId, saved);

        // 메시지를 읽음 처리
        // 안읽은 메시지가 있는 상황에서 읽었을 때만 상대방의 채팅방을 새로고침 시킨다.
        log.info("send!!");
        if(chatMessageService.notChecked(message)) {
            log.info("reload!");
            sendingOperations.convertAndSend("/sub/chat/reload/" + message.getRoomId(), message);
        }
    }

    /**
     * 내가 채팅방에 있으면 상대방의 메시지를 읽음 처리
     */
    @MessageMapping("/chat/read")
    public void read(ChatMessageDTO message) {
        // 읽지 않은 메시지를 읽음 처리, 읽은 메시지 수 반환해서 message 내용으로 설정
        int readCount = chatMessageService.turnCheckedTrue(message);
        message.setMessage(String.valueOf(readCount));

        // 이 메시지로 인해 내가 참여 중인 채팅방 리스트 페이지에서 읽지 않은 메시지의 수가 업데이트된다.
        sendingOperations.convertAndSend("/sub/checked/" + message.getSenderId(), message);
    }

    /**
     * 채팅방이 삭제되었을 때 처리
     * 삭제한 사람: 채팅방 구독을 중단, 채팅방 창을 종료
     * 채팅 상대방: 상대가 채팅방에서 떠났음을 표시
     */
    @MessageMapping("/chat/leave")
    public void leave(ChatMessageDTO message) {
        ChatMessageEntity.toDTO(chatMessageService.saveMessage(message));   // 채팅방 떠남 메시지 저장
        String targetId = chatMessageService.getTargetId(message);          // 채팅 상대 id 가져오기

        sendingOperations.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);     // 채팅 상대에게 채팅방 떠남 표시
        sendingOperations.convertAndSend("/sub/chat/targetLeft/" + targetId, message);          // 상대방의 구독 종료 처리를 위한 메시지 전송

        // 삭제한 자신에게 메시지를 보내서 구독을 종료한다.
        sendingOperations.convertAndSend("/sub/chat/leave/" + message.getSenderId(), message);

    }

    /**
     * 내가 참여 중인 채팅방 리스트에 입장하면 채팅방 별로 읽지 않은 메시지 수를 전송한다.
     */
    @GetMapping("/unchecked")
    @ResponseBody
    public Map<String, String> unchecked(@SessionAttribute(name = "loginMember", required = true) Member loginMember) {
        List<ChatRoomDTO> chatRoomDTOList = chatRoomService.getRoomsByUserId(loginMember.getId().toString());
        return chatMessageService.getUnchecked(chatRoomDTOList, loginMember.getId());
    }

    /**
     * 채팅방 입장 시 메시지들 반환
     */
    @GetMapping("/messages/{roomId}")
    @ResponseBody
    public List<ChatMessageDTO> getMessages(@PathVariable String roomId) {
        log.info("getMessages is called!");
        List<ChatMessageDTO> chatMessageDTOS = chatMessageService.getMessagesByRoomId(roomId);
        ChatMessageDTO checkMessage = null;
        if(chatMessageDTOS.size() != 0) {
            checkMessage = chatMessageDTOS.get(chatMessageDTOS.size() - 1);
            // log.info("last msg: " + checkMessage.getMessage());

            // 상대방의 마지막 메시지가 채팅방에서 떠나는 것을 알리는 메시지일 때
            // targetId는 나의 id가 된다.
            // 나에게 구독 종료 알림 메시지를 보낸다.
            if (checkMessage.getType() == MessageType.LEAVE) {
                String targetId = chatMessageService.getTargetId(checkMessage);
                sendingOperations.convertAndSend("/sub/chat/targetLeft/" + targetId, checkMessage);
                log.info("leave controller! target: " + targetId);
            }
        }

        return chatMessageDTOS;
    }

    /**
     * 메시지 삭제
     */
    @GetMapping("/message/{messageId}")
    @ResponseBody
    public ChatMessageDTO deleteMessage(@PathVariable String messageId) {
        log.info("deleteMessage called!!");
        ChatMessageDTO deletedMessage = chatMessageService.deleteMessage(messageId);

        // 나와 채팅 상대에게 삭제됨을 알려서 화면을 새로고침 하게 한다.
        sendingOperations.convertAndSend("/sub/chat/reload/" + deletedMessage.getRoomId(), deletedMessage);
        return deletedMessage;
    }
}


