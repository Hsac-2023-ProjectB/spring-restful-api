package com.teample.packages.member;

import com.teample.packages.member.domain.GenderType;
import com.teample.packages.member.domain.Member;
import com.teample.packages.member.repository.MemberRepository;
import com.teample.packages.profile.domain.Profile;
import com.teample.packages.profile.repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor

public class TestDataInit {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @PostConstruct
    public void init() {
        /*
        Member member = new Member();
        member.setLoginId("cksgud0403");
        member.setName("cks");
        member.setPassword("111");
        member.setEmail("cksgud0403@naver.com");
        member.setBirthDate("2002-04-03");
        member.setGender(GenderType.Man);

        List<String> fields = new ArrayList<>();

        fields.add("CODING");
        fields.add("WEB");

        member.setFields(fields);

        memberRepository.save(member);


        member = new Member();
        member.setName("park");
        member.setLoginId("park");
        member.setPassword("111");
        member.setEmail("parkpark@naver.com");
        member.setBirthDate("2000-04-03");
        member.setGender(GenderType.Woman);

        fields = new ArrayList<>();

        fields.add("CODING");
        fields.add("WEB");

        member.setFields(fields);

        memberRepository.save(member);

        member = new Member();
        member.setName("kim");
        member.setLoginId("kim");
        member.setPassword("123");
        member.setEmail("kimkim@naver.com");
        member.setBirthDate("2022-05-05");
        member.setGender(GenderType.Man);

        fields = new ArrayList<>();

        fields.add("CODING");
        fields.add("WEB");

        member.setFields(fields);

        memberRepository.save(member);

        // 임시 프로필-------------------------
        Profile profile = new Profile();
        profile.setId(999L);
        profile.setAuthorId(3L);
        profile.setAuthorName("kim");
        profile.setGender(GenderType.Man);
        profile.setFields("Computer");
        profile.setTags("Backend");
        profile.setIntroduction("hello, Teample!!");
        profileRepository.save(profile);

         */
    }
}