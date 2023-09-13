package com.teample.packages.mypage.controller;


import com.teample.packages.member.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping(value = "/mypage")
public class MyPageController {

    @GetMapping
    public String mypage(@SessionAttribute(name = "loginMember") Member loginMember, Model model) {

        if(loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        }

        return "mypage/mypage";
    }

    @GetMapping("/mychat")
    public String myPage(@SessionAttribute(name = "loginMember", required = true) Member loginMember) {
        return "chat/myChatRooms";
    }

}
