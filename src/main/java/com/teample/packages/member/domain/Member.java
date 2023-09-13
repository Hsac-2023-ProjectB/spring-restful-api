package com.teample.packages.member.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.util.List;

@Getter @Setter
@ToString
public class Member {

    private Long id;

    private String name; //이름

    private String loginId; //로그인 ID

    private String password; //비밀번호

    private String email; //이메일

    private Date birthDate; //생년월일

    private GenderType gender; //성별

    private List<String> fields; //관심 분야

}
