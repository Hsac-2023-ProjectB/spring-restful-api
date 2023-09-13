package com.teample.packages.member.controller;


import com.teample.packages.member.domain.GenderType;
import com.teample.packages.member.domain.Member;
import com.teample.packages.member.service.MemberService;
import com.teample.packages.profile.domain.Profile;
import com.teample.packages.profile.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final ProfileService profileService;

    @ModelAttribute("genderType")
    public GenderType[] genderTypes() {
        return GenderType.values();
    }


    @ModelAttribute("fields")
    public Map<String, String> fields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("CODING", "코딩");
        fields.put("WEB", "웹");
        fields.put("COOKING", "요리");
        fields.put("PPT", "PPT");
        return fields;
    }

    //회원 가입 폼
    @GetMapping("/signup")
    public String createForm(Model model) {
        model.addAttribute("MemberForm", new MemberSaveForm());
        return "members/createMemberForm";
    }

    //회원 가입
    @PostMapping("/signup")
    public String create(@Validated @ModelAttribute ("MemberForm") MemberSaveForm memberForm, BindingResult bindingResult, Model model) {

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "members/createMemberForm";
        }


        Member member = new Member();


        member.setName(memberForm.getName());
        member.setLoginId(memberForm.getLoginId());
        member.setPassword(memberForm.getPassword());
        member.setEmail(memberForm.getEmail());
        member.setBirthDate(Date.valueOf(memberForm.getBirthDate()));
        member.setGender(memberForm.getGender());
        member.setFields(memberForm.getFields());

        log.info("member = {}", member.toString());

        memberService.join(member);

        return "redirect:/";
    }


    @GetMapping("/{memberId}")
    public String member(@SessionAttribute(name = "loginMember", required = true) Member loginMember, @PathVariable Long memberId, Model model) {

        if (!(memberId.equals(loginMember.getId()))) {
            return "redirect:/mypage";
        }

        Member member = memberService.findMemberById(memberId);
        model.addAttribute("member", member);

        return "members/Member";
    }


    @GetMapping("/{memberId}/edit")
    public String updateMemberForm(@SessionAttribute(name = "loginMember", required = true) Member loginMember, @PathVariable Long memberId, Model model) {


        if(!loginMember.getId().equals(memberId)) {
            return "redirect:/mypage";
        }

        Member member = memberService.findMemberById(memberId);

        model.addAttribute("member", member);

        return "members/updateMemberForm";
    }


    @PutMapping("/{memberId}/edit")
    public String updateMember(@PathVariable Long memberId, @Validated @ModelAttribute ("member") MemberUpdateForm form, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "members/updateMemberForm";
        }

        Member memberParam = new Member();

        memberParam.setName(form.getName());
        memberParam.setPassword(form.getPassword());
        memberParam.setEmail(form.getEmail());
        memberParam.setBirthDate(Date.valueOf(form.getBirthDate()));
        memberParam.setGender(form.getGender());
        memberParam.setFields(form.getFields());

        memberService.updateMember(memberId, memberParam);

        return "redirect:/members/{memberId}";
    }


    @DeleteMapping("/{memberId}")
    public String delete(@SessionAttribute(name = "loginMember", required = true) Member loginMember, @PathVariable Long memberId, HttpSession session) {

        if (!(memberId.equals(loginMember.getId()))) {
            return "redirect:/mypage";
        }

        Member member = memberService.findMemberById(memberId);

        memberService.withdrawal(member.getId());

        if(session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    @GetMapping("/{memberId}/profiles")
    public String getMemberProfiles(@PathVariable Long memberId, Model model) {

        List<Profile> profiles = profileService.findProfilesById(memberId);

        model.addAttribute("profiles", profiles);

        return "profiles/ProfileList";
    }

}
