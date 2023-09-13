package com.teample.packages.member.service;


import com.teample.packages.member.domain.Member;
import com.teample.packages.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoginService {


    private final MemberRepository memberRepository;


    // return null 이면 로그인 실패

    public Member login(String loginId, String password) {
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);

        return findMemberOptional.filter(m -> m.getPassword().equals(password))
                .orElse(null);

    }

}
