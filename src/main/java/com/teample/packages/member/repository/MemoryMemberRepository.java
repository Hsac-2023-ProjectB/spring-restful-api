package com.teample.packages.member.repository;

import com.teample.packages.member.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MemoryMemberRepository implements MemberRepository{
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    /**
     * 회원정보 저장
     */
    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    /**
     * 고유 회원 id로 회원 조회
     */
    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * 회원이 지정한 id로 회원 조회
     */
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        for(Member m : store.values()) {
            if(m.getLoginId().equals(loginId)) {
                return Optional.of(m);
            }
        }

        return Optional.empty();
    }

    /**
     * 회원 정보 수정
     */
    public void update(Long updateMemberId, Member updateParam) {
        Member findMember = findById(updateMemberId).get();

        findMember.setName(updateParam.getName());
        findMember.setPassword(updateParam.getPassword());
        findMember.setEmail(updateParam.getEmail());
        findMember.setBirthDate(updateParam.getBirthDate());
        findMember.setGender(updateParam.getGender());
        findMember.setFields(updateParam.getFields());
    }

    /**
     * 회원 탈퇴
     */
    @Override
    public void delete(Long id) {
        Member deleteMember = findById(id).get();
        store.remove(deleteMember.getId());
    }
}

