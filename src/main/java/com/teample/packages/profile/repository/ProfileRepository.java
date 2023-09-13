package com.teample.packages.profile.repository;

import com.teample.packages.profile.domain.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository {

    Profile save(Profile profile);
    Optional<Profile> findById(Long id);

    List<Profile> findAllByAuthorId(Long authorId);

    List<Profile> findAll();

    void update(Long updateProfileId, Profile updateParam);

    Long findAuthorIdByProfileId(Long profileId);

    void delete(Long profileId);
}
