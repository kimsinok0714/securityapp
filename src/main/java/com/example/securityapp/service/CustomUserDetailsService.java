package com.example.securityapp.service;



import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.securityapp.domain.Member;
import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// UserDetailsService : 
// Spring Security에서 사용자 인증을 처리하기 위해 사용자 정보를 로드하는 데 사용되는 핵심 인터페이스입니다.


@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;



    // 인증된 사용자 정보(Principal)를 반환한다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("================================ loadUserByUsername : {}", username);

        Member member = memberRepository.getWithRoles(username);  // email

        if (member == null) {
            throw new  UsernameNotFoundException("No email found.");
        }

        MemberDto memberDto = new MemberDto(member.getEmail(), 
                                            member.getPassword(), 
                                            member.getNickname(), 
                                            member.getRoles().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList()));
                                            // List<String> roles;

        log.info("memberDto : {}", memberDto);

        return memberDto;

    }

    

}
