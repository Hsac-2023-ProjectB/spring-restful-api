package com.teample.packages.chat.service;

import com.teample.packages.chat.domain.entity.ChatRoomEntity;
import com.teample.packages.chat.domain.repository.ChatRoomRepository;
import com.teample.packages.chat.dto.ChatRoomDTO;
import com.teample.packages.member.domain.Member;
import com.teample.packages.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * db 연결 후 repository 생성해서 기능 이전 필요
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ChatRoomService {

    private final MemberService memberService;              // 멤버 정보 검색을 위해 사용
    private final ChatMessageService chatMessageService;    // 채팅방 삭제 시 메시지 삭제를 위해 사용
    private final ChatRoomRepository chatRoomRepository;

    // 로그
    public static final Logger logger = LogManager.getLogger(ChatRoomService.class);

    /**
     * 채팅방을 db에 저장
     */
    @Transactional
    public ChatRoomDTO saveRoom(ChatRoomDTO chatRoomDTO) {
        chatRoomRepository.save(ChatRoomDTO.toEntity(chatRoomDTO));
        return chatRoomDTO;
    }

    /**
     * 사용자 id로 채팅방 조회
     */
    @Transactional
    public List<ChatRoomDTO> getRoomsByUserId(String userId) {
        List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findBySubmembersContaining(userId);
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();

        if(chatRoomEntities.isEmpty()) return chatRoomDTOS;

        for(ChatRoomEntity chatRoomEntity : chatRoomEntities) {
            chatRoomDTOS.add(ChatRoomEntity.toDTO(chatRoomEntity));
        }

        return chatRoomDTOS;
    }

    /**
     * 채팅방 id로 조회
     */
    @Transactional
    public ChatRoomDTO getRoomByRoomId(String roomId) {
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByRoomId(roomId);
        if(chatRoomEntity != null) return ChatRoomEntity.toDTO(chatRoomEntity);
        else return null;
    }

    /**
     * 채팅 상대방 정보 조회 및 반환
     * member부분 jpa로 변경하면 수정 필요.
     */
    public Member setChatTargetInfo(Long targetId) {
        Member targetMember = memberService.findMemberById(targetId);
        return targetMember;
    }

    /**
     * 채팅방 이름 변경
     */
    @Transactional
    public ChatRoomDTO updateRoomName(String roomId, String newRoomName) {
        ChatRoomEntity targetRoom = chatRoomRepository.findByRoomId(roomId);
        targetRoom.updateRoomName(newRoomName);
        chatRoomRepository.save(targetRoom);
        log.info("updateRoomName: " + targetRoom.getRoomName());
        return ChatRoomEntity.toDTO(targetRoom);
    }

    /**
     * 채팅방 삭제는 2-step 으로 이루어진다.
     * 1. 둘 중 하나가 먼저 나가면, submembers 를 수정해서 한 명이 나갔음을 기록한다.
     * 2. 한 명만 남은 상태에서 채팅방을 나가면 채팅방이 영구적으로 삭제된다.
     */
    @Transactional
    public void deleteChatRoom(String roomId, Member loginMember) {
        ChatRoomEntity targetRoom = chatRoomRepository.findByRoomId(roomId);
        String submembers = targetRoom.getSubmembers();
        String[] temp = submembers.split("-");

        if(!submembers.contains("left")) {
            log.info("first delete!");
            // 만약 채팅방에 참여중인 두 사람 중 한 명이 나가는 경우
            // submembers 는 두 사람의 id를 "-"로 연결한 문자열인데, 여기에서 나가는 사람의 id를 제거하고 그 자리에 'left'를 넣는다.
            submembers = (temp[0].equals(loginMember.getId().toString())) ? "left-" + temp[1] : temp[0] + "-left";
            targetRoom.updateSubmembers(submembers);
            chatRoomRepository.save(targetRoom);
        }

        else {
            // 채팅방에서 상대가 나가고 혼자 남아있는 경우, 채팅방을 삭제한다.
            chatMessageService.deleteAllMessage(targetRoom.getRoomId());
            chatRoomRepository.delete(targetRoom);
        }
    }
}
