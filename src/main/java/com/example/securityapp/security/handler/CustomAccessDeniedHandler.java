package com.example.securityapp.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


// AccessDeniedHandler : 
// 접근 거부(HTTP 403 Forbidden) 상황을 처리하는 핸들러
// 권한이 부족한 사용자가 보호된 리소스에 접근하려고 할때 AccessDeniedException 예외가 발생한다.
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

                log.info("CustomAccessDeniedHandler");
        
                Gson gson = new Gson();

                String jsonStr = gson.toJson(Map.of("error", "ERROR_ACCESS_DENIED"));

                response.setContentType("application/json");

                response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 : 권한 부족
            
                PrintWriter pw = response.getWriter();

                pw.println(jsonStr);

                pw.close();
        
    }

    

}
