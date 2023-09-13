package com.teample.packages.member.repository;

import com.teample.packages.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);

    void update(Long id, Member updateParam);

    Optional<Member> findByLoginId(String loginId);

    void delete(Long id);


}
