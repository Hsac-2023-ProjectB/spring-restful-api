package com.teample.packages.chat.controller;

import com.teample.packages.chat.dto.ChatMessageDTO;
import com.teample.packages.chat.service.ChatMessageService;
import com.teample.packages.chat.service.ChatRoomService;
import com.teample.packages.chat.dto.ChatRoomDTO;
import com.teample.packages.member.domain.Member;
import com.teample.packages.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ProfileService profileService;
    public static final Logger logger = LogManager.getLogger(ChatRoomController.class);
    private final SimpMessageSendingOperations sendingOperations; //특정 Broker로 메세지를 전달. SimpMessagingTemplate 와 같음.
    /**
     * 마이페이지에서 요청 - 사용자가 참여중인 채팅방들 userId로 조회 후 반환.
     */
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDTO> myRoom(@SessionAttribute(name = "loginMember", required = true) Member loginMember) {
        return chatRoomService.getRoomsByUserId(loginMember.getId().toString());
    }

    /**
     * 채팅방 생성
     */
    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDTO createRoom(@RequestParam String roomId, @RequestParam String roomName, @RequestParam String subMembers, @RequestParam String creatorId) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setRoomId(roomId);
        chatRoomDTO.setRoomName(roomName);
        chatRoomDTO.setSubmembers(subMembers);

        // 채팅 상대가 '참여 중인 채팅방 리스트 페이지'에 있을 때 동적으로 화면 업데이트를 위해 메시지 전송
        String[] temp = roomId.split("-");
        String targetId = temp[0].equals(creatorId) ? temp[1] : temp[0];
        sendingOperations.convertAndSend("/sub/newChatRoom/" + targetId, new ChatMessageDTO());

        return chatRoomService.saveRoom(chatRoomDTO);
    }

    /**
     * roomId로 채팅방 조회 후 반환 - startRoom.js - searchRoom()에서 사용
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDTO findRoom(@PathVariable String roomId) {
        return chatRoomService.getRoomByRoomId(roomId);
    }

    /**
     * 채팅방 입장 화면으로 매핑
     */
    @GetMapping("/room/enter")
    public String enterRoom() {
        return "chat/chattingRoom";
    }

    /**
     * 채팅방에 필요한 로그인정보, 채팅 상대 정보 반환
     */
    @GetMapping("/info/{profileId}")
    @ResponseBody
    public Map<String, Member> getChatMemberInfo(@PathVariable Long profileId, @SessionAttribute(name = "loginMember", required = true) Member loginMember) {
        Map<String, Member> chatInfo = new HashMap<>();
        Long authorId = 0L;
        chatInfo.put("loginMember", loginMember);

        // profileId == 0이면 targetMember 정보를 가져오지 않는다.
        if(profileId != 0) authorId = profileService.findAuthor(profileId);
        if(authorId != 0L) {
            Member targetMember = chatRoomService.setChatTargetInfo(authorId);
            chatInfo.put("targetMember", targetMember);
        }

        return chatInfo;
    }

    /**
     * 채팅방 이름 변경
     */
    @PostMapping("/roomName")
    @ResponseBody
    public void changeRoomName(@RequestParam String roomId, @RequestParam String newRoomName) {
        chatRoomService.updateRoomName(roomId, newRoomName);
    }

    @GetMapping("/leaveRoom/{roomId}")
    @ResponseBody
    public void deleteRoom(@PathVariable String roomId, @SessionAttribute(name = "loginMember", required = true) Member loginMember) {
        chatRoomService.deleteChatRoom(roomId, loginMember);
    }
}
