package com.teample;

import com.teample.packages.member.repository.*;
import com.teample.packages.profile.repository.JdbcProfileRepository;
import com.teample.packages.profile.repository.MemoryProfileRepository;
import com.teample.packages.profile.repository.ProfileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    public final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public MemberRepository memberRepository() { //memberRepository 빈 등록
        return new JdbcMemberRepository(dataSource);
    }

    @Bean
    public ProfileRepository profileRepository() { //profileRepository 빈 등록
        return new JdbcProfileRepository(dataSource);
    }
}