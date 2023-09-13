package com.teample.packages.member.domain;


public enum GenderType {
    Man("남자"), Woman("여자");

    private final String description;

    GenderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}