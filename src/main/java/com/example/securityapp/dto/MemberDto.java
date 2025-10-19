package com.example.securityapp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.ToString;


@ToString
// UserDetails
public class MemberDto extends User {

    private String email;  // username
    private String password;
    private String nickname;    
    private List<String> roleNames = new ArrayList<>();
    

    // Constructor Method
    public MemberDto(String email, String password, String nickname, List<String> roleNames) {

        super(email, password,  
            roleNames.stream().map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName)).collect(Collectors.toList()));
        
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.roleNames = roleNames;
    }   



    // JWT : Header, Claim(Payload), Signature로 구성된다.
    // Header : { "typ": "JWT" } 
    // Claim(Payload) : 인증된 사용자 정보 및 추가 정보 (iat : 발행 시간, exp : 만료 시간)
    // Signature : 무결성 보장
    public Map<String, Object> getClaims() {

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        //map.put("password", password);
        map.put("nickname", nickname);
        map.put("roleNames", roleNames);
        
        return map;
    }
    

}
