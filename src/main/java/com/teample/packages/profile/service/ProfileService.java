package com.teample.packages.profile.service;


import com.teample.packages.exception.EmptyDataException;
import com.teample.packages.member.domain.Member;
import com.teample.packages.profile.domain.Profile;
import com.teample.packages.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {


    private final ProfileRepository profileRepository;


    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }


    public Profile getProfile(long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EmptyDataException("해당 이름을 가진 유저가 데이터가 없음"));
        return profile;
    }

    public Profile saveProfile(Profile Profile) {
       return profileRepository.save(Profile);
    }

    public void updateProfile(Long updateProfileId, Profile updateParam) {
        profileRepository.update(updateProfileId, updateParam);
    }

    public void deleteProfile(Long profileId) {
        profileRepository.delete(profileId);
    }

    public Long findAuthor(Long profileId) {
        return profileRepository.findAuthorIdByProfileId(profileId);
    }

    public List<Profile> findProfilesById(Long id) {
        return profileRepository.findAllByAuthorId(id);
    }
}
