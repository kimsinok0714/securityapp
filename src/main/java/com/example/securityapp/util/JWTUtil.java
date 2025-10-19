package com.example.securityapp.util;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import com.example.securityapp.exception.CustomJWTException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/*
  * 1. JWT 토큰 생성
  * 2. JWT 토큰 검증
  * 3. 반드시 키 값은 반드시 30자 이상 사용할 것 
  # 4. 반드시 jjwt library 0.11.5 version 사용할 것
  */

@Slf4j
public class JWTUtil {

    // 키 값은 반드시 30자 이상 사용할 것!!
    private static String key = "1234567890123456789012345678901234567890";


    // 1. JWT 토큰 생성
    public static String generateToken(Map<String, Object> claims, int min) { // min : 만료 시간

        
        // JWT : Header, Claim(Payload), Signature로 구성된다.
        // Header : { "typ": "JWT" } 
        // Claim(Payload) : 인증된 사용자 정보 및 추가 정보 (iat : 발행 시간, exp : 만료 시간)
        // Signature : 무결성 보장 (변조 방지) : 비밀키
        SecretKey key = null;

        try {
             // HMAC-SHA algorithms 사용하여 서명에 사용할 비밀키를 생성한다.
             key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        String jwtStr = Jwts.builder()
                            .setHeader(Map.of("typ", "JWT"))    // 헤더
                            .setClaims(claims)                  // JWT 페이로드 : 사용자 인증 정보
                            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // JWT 발급시간, 현재 시간(시스템 타임존)을 UTC 변경하고 Date 객체로 변환 (초단위)
                            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))  // JWT 만료 시간
                            .signWith(key)  // JWT 서명에 사용되는 비밀 키
                            .compact();
        
        return jwtStr;
    }


    // 2. JWT 토큰 검증
    // JWT 토큰에 포함된 Claim 추출. 반환
    public static Map<String, Object> validateToken(String token) {

        Map<String, Object> claim = null;

        try {
            // 비밀 키의 역할
            // JWT 토큰을 검증하려면 JWT 토큰 생성할때 서명(Signature)에 사용된 비밀키가 필요합니다.
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            // JWT 토큰 검증을 수행하기 위해서 Parser 생성
            // JWT 서명 검증에 사용할 비밀 키 설정
            claim = Jwts.parserBuilder()
                .setSigningKey(key)      // JWT 서명 검증을 위한 비밀 키 설정
                .build()
                .parseClaimsJws(token)   // JWT를 파싱하여 유효성을 검사하고, 서명이 올바르면 토큰의 데이터(Claim)를 추출. 반환
                .getBody();
       
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("Malformed");  // 잘못된 형식의 JWT
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired");
        } catch (InvalidClaimException invalidClaimException) { 
            throw new CustomJWTException("Invalid");    // 클레임이 유효하지 않음
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError");   // 기타 JWT 관련 오류 (서명 검증 실패 등)
        } catch (Exception ex) {
            throw new CustomJWTException("Error");
        }  
        return claim;
    }

}


