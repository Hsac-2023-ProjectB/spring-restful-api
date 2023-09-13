package com.teample.packages.profile.domain;

import com.teample.packages.member.domain.GenderType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Profile {

    private Long id;          // 프로필 id

    private Long authorId;        // 작성자 id

    private String authorName;        // 작성자 이름

    private GenderType gender; //작성자 성별

    private List<String> fields;      // 분야

    private String tags;        // 태그

    private String introduction; // 소개
}
