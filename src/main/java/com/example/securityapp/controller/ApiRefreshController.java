package com.example.securityapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.securityapp.exception.CustomJWTException;
import com.example.securityapp.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


// Access Token, Rrefresh Token 재발급

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiRefreshController {    

    // Request Header : Authorization
    // QueryString : refreshToken
    @GetMapping("/members/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, @RequestParam("refreshToken") String refreshToken) {

        // if (refreshToken == null) {            
        //     throw new CustomJWTException("NULL_REFRESH");
        // }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_AUTH");
        }

        // 1. Access Token이 만료되지 않았다면 기존 Access Token과 Refresh Token을 그대로 전송
        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // 2. Access Token이 만료되고, Refresh Token이 검증에 성공한 경우 새로운 Access Token 생성
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        
        log.info("Refresh Token Claims : {}", claims);

        // 새로운 Access Token 생성        
        String newAccessToken = JWTUtil.generateToken(claims, 10); // 10분


        // 3. Refresh Token이 만료 시간을 체크한 후 만료 시간이 1시간 미만인 경우 Refresh Token을 생성
        //    만료 시간이 충분한 경우 기존 Refresh Token을 전송

        // iat : Refresh Token 발행 시간
        // exp : Refresh Token 만료 시간 

        log.info("exp : {}", claims.get("exp"));
        log.info("exp(Integer) : {}", (Integer)claims.get("exp"));  
        
        String newRefreshToken = checkTime((Integer)claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

        // 4. 만약 Access Token과 Refresh Token이 모두 만료되었다면 
        //    사용자는 다시 로그인을 해야 한다.
        
    }
    

    // 액세스 토큰이 만료 여부 확인
    private boolean checkExpiredToken(String accessToken) {

        try {
            JWTUtil.validateToken(accessToken);    
        } catch (Exception e) {
            if (e.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }

    // Refresh Token의 만료 시간이 1시간 미만인지 여부 확인
    private boolean checkTime(Integer exp) {  // 초 단위 시간

        // 1. 초 → 밀리초 변환해서 Date 객체 생성
        //java.util.Date expDate = new java.util.Date((long)exp * 60 * 1000);
        java.util.Date expDate = new java.util.Date(exp * 1000L);
        
        //2. 현재 시각과의 차이 계산 (ms)
        long gap = expDate.getTime() - System.currentTimeMillis();  // 단위 : ms
        
        // 3. 밀리초 → 분으로 변환
        long leftmin = gap / (1000 * 60);  // 단위  : minute
    
        return leftmin < 60;
    }


}














