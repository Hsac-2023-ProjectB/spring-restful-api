package com.teample.packages.profile.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProfileUpdateForm {


    @NotNull
    private Long id;          // 프로필 id

    private String tags;        // 태그

    @NotBlank
    private String introduction;// 소개



}
