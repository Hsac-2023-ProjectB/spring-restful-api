package com.teample.packages.member.controller;


import com.teample.packages.member.domain.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MemberSaveForm {


    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "이름은 2~10자의 한글과 영문 대/소문자만 사용 가능합니다.")
    private String name; //이름

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{2,10}", message = "아이디는 영문 대/소문자, 숫자만 가능하며 2 ~ 10자리까지 가능합니다.")
    private String loginId; //로그인 ID

    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대/소문자, 숫자, 특수문자만 가능합니다.")
    private String password; //비밀번호

    @NotBlank
    @Email
    private String email; //이메일

    @NotBlank
    private String birthDate; //생년월일

    private GenderType gender; //성별

    private List<String> fields; //관심 분야
}