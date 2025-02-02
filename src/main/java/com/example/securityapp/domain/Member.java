package com.example.securityapp.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_member")
public class Member {

    @Id
    private String email;
    
    private String password;

    private String nickname;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_roles",  joinColumns = @JoinColumn(name = "email", referencedColumnName = "email"))
    @Enumerated(EnumType.STRING)
    private List<MemberRole> roles = new ArrayList<>();

    
    // 권한 추가
    public void addRoles(MemberRole memberRole) {
        if (!roles.contains(memberRole)) {
            this.roles.add(memberRole);
        }        
    }

    // 권한 삭제
    public void removeRole() {
        this.roles.clear();
    }

    // 비즈니스 메소드
    
    public void changePw(String password) {
        this.password = password;
    } 

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }


}


