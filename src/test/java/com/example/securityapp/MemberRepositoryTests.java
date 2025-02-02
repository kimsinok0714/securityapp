package com.example.securityapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import com.example.securityapp.domain.Member;
import com.example.securityapp.domain.MemberRole;
import com.example.securityapp.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;



@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원 등록

    @Test
    @Rollback(false)
    void testSave() {

        for (int i = 1; i <= 10 ; i++) {

            Member member =  Member.builder()
                .email("user" + i + "@gmail.com")
                .password(passwordEncoder.encode("1111"))
                .nickname("user" + i)
                .build();   

            member.addRoles(MemberRole.USER);
            
            if ( i >= 5) {
                member.addRoles(MemberRole.MANAGER);
            }

            if (i >= 8) {
                member.addRoles(MemberRole.ADMIN);
            }   

            memberRepository.save(member);

        }

    }

    // 회원 정보 조회
    @Test
    void testGetWithRoles() {

        // given
        String email = "user1@gmail.com";
       

        // when
        Member member = memberRepository.getWithRoles(email);

        // then
        assertNotNull(member);

        log.info("email : {}", member.getEmail());

        for (MemberRole role : member.getRoles()) {
            
            log.info("role : {}", role);

        }


    }

}
