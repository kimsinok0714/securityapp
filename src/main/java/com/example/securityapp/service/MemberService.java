package com.example.securityapp.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import com.example.securityapp.domain.Member;
import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto login(String username, String password) {

        Member member = memberRepository.getWithRoles(username);

        if ( member == null) {
            throw new IllegalArgumentException("Not Found");
        }
        
        List<String> roles = member.getRoles().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList());

        MemberDto memberDto = new MemberDto(member.getEmail(), member.getPassword(), member.getNickname(), roles);            

        return memberDto;
        
    }

}
