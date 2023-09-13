package com.teample.packages.profile.repository;

import com.teample.packages.profile.domain.Profile;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MemoryProfileRepository implements ProfileRepository {

    private static final Map<Long, Profile> store = new HashMap<>();
    private static long sequence = 0L;


    //프로필 저장
    public Profile save(Profile profile) {
        profile.setId(++sequence);
        log.info("save: profile={}", profile);
        store.put(profile.getId(), profile);
        return profile;
    }

    public Optional<Profile> findById(Long id) {
        return Optional.of(store.get(id));
    }

    public List<Profile> findAllByAuthorId(Long authorId) {

        ArrayList<Profile> profiles = new ArrayList<>();

        for( Long key : store.keySet() ){
            Profile profile = store.get(key);

            if(profile.getAuthorId().equals(authorId)) {
                profiles.add(profile);
            }
        }

        return profiles;

    }

    public List<Profile> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long updateProfileId, Profile updateParam) {
        Optional<Profile> optionalProfile = findById(updateProfileId);

        if(optionalProfile.isPresent()) {
            Profile findProfile = optionalProfile.get();

            findProfile.setFields(updateParam.getFields());
            findProfile.setIntroduction(updateParam.getIntroduction());
            findProfile.setTags(updateParam.getTags());
        }

    }

    public void delete(Long profileId) {
        Optional<Profile> optionalProfile = findById(profileId);

        if(optionalProfile.isPresent()) {
            Profile findProfile = optionalProfile.get();
            store.remove(findProfile.getId());
        }
    }


    public void clearStore() {
        store.clear();
    }

    public Long findAuthorIdByProfileId(Long profileId) {
        if(store.get(profileId) != null)
            return store.get(profileId).getAuthorId();

        else return 0L;
    }
}
