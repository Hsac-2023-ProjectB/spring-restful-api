package com.teample.packages.profile.controller;

import com.teample.packages.member.domain.Member;
import com.teample.packages.profile.domain.Profile;
import com.teample.packages.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/profiles")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @ModelAttribute("fields")
    public Map<String, String> fields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("CODING", "코딩");
        fields.put("WEB", "웹");
        fields.put("COOKING", "요리");
        fields.put("PPT", "PPT");
        return fields;
    }

    @GetMapping
    public String getProfiles(Model model) {

        List<Profile> profiles = profileService.getAllProfiles();

        model.addAttribute("profiles", profiles);

        return "profiles/ProfileList";
    }


    @GetMapping("/{profileId}")
    public String getProfileById(@PathVariable Long profileId, Model model) {
        Profile profile = profileService.getProfile(profileId);

        model.addAttribute("profile", profile);

        return "profiles/Profile";

    }

    @GetMapping("/add")
    public String createProfileForm(Model model) {
        model.addAttribute("profile", new Profile());
        return "profiles/createProfileForm";
    }

    @PostMapping("add")

    public String crateProfile(@Validated @ModelAttribute("profile") ProfileSaveForm profileSaveForm, BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = true)Member loginMember, Model model, RedirectAttributes redirectAttributes) {

        log.info("ProfileSaveForm = {} ", profileSaveForm);

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "profiles/createProfileForm";
        }


        Profile profile = new Profile();

        profile.setAuthorId(loginMember.getId());
        profile.setAuthorName(loginMember.getName());
        profile.setGender(loginMember.getGender());
        profile.setTags(profileSaveForm.getTags());
        profile.setIntroduction(profileSaveForm.getIntroduction());

        Profile savedProfile = profileService.saveProfile(profile);

        redirectAttributes.addAttribute("profileId", savedProfile.getId());
        return "redirect:/profiles/{profileId}";

    }


    @GetMapping("/{profileId}/edit")
    public String updateProfileForm(@SessionAttribute(name = "loginMember", required = true) Member loginMember, @PathVariable Long profileId, Model model) {

        Profile profile = profileService.getProfile(profileId);


        if (!(profile.getAuthorId().equals(loginMember.getId()))) { //프로필 작성자가 아니라면
            return "redirect:/profiles/{profileId}";
        }

        model.addAttribute("profile", profile);
        return "profiles/updateProfileForm";
    }

    @PutMapping("/{profileId}/edit")
    public String updateProfile(@PathVariable Long profileId, @Validated @ModelAttribute("profile") ProfileUpdateForm form, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "profiles/updateProfileForm";
        }

        Profile profileParam = new Profile();

        profileParam.setTags(form.getTags());
        profileParam.setIntroduction(form.getIntroduction());

        profileService.updateProfile(profileId, profileParam);

        return "redirect:/profiles/{profileId}";
    }


    @GetMapping("/{profileId}/delete")
    public String deleteProfileForm(Model model, @PathVariable Long profileId) {

        model.addAttribute("profileId", profileId);

        model.addAttribute("profile", new ProfileDeleteForm());

        return "profiles/deleteProfileForm";
    }


        @DeleteMapping("/{profileId}/delete")
    public String deleteProfile(@Validated @ModelAttribute("profile") ProfileDeleteForm form, BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = true) Member loginMember, @PathVariable Long profileId, Model model) {

        if (bindingResult.hasErrors()) {
                log.info("errors={}", bindingResult);
                return "profiles/deleteProfileForm";
        }

        Profile profile = profileService.getProfile(profileId);


        if (!(profile.getAuthorId().equals(loginMember.getId())) || !(form.getPassword().equals(loginMember.getPassword()))) { //프로필 작성자가 아니라면

            bindingResult.reject("deleteFail", "프로필 작성자가 아니거나 입력한 비밀번호가 맞지 않습니다.");

            return "profiles/deleteProfileForm";
        }

        profileService.deleteProfile(profileId);

        return "redirect:/profiles";
    }
}
