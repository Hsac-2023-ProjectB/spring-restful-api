package com.teample.packages.profile.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileSaveForm {

    @NotNull
    private Long authorId;        // 작성자 id

    private String tags;        // 태그

    @NotBlank
    private String introduction;// 소개
}
