package com.teample.packages.profile.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProfileDeleteForm {

    @NotBlank(message = "비밀번호를 입력해주세요!")
    private String password;
}
