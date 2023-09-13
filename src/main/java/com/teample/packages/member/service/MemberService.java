package com.teample.packages.member.service;

import com.teample.packages.exception.EmptyDataException;
import com.teample.packages.member.domain.Member;
import com.teample.packages.member.repository.MemberRepository;
import com.teample.packages.profile.domain.Profile;
import com.teample.packages.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 회원 id로 조회
     */
    public Member findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EmptyDataException("해당 이름을 가진 유저가 데이터가 없음"));;
        return member;
    }

    public void updateMember(Long id, Member memberParam) {
        memberRepository.update(id, memberParam);
    }

    public void withdrawal(Long id) {
        memberRepository.delete(id);
    }
}
