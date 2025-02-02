package com.example.securityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.securityapp.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    
    // JPQL
    @Query("SELECT m FROM Member AS m JOIN FETCH m.roles WHERE m.email = :email")    
    Member getWithRoles(@Param("email") String email);

}
