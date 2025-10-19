package com.example.securityapp.security.filter;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


// JWTCheckFilter :
// 1. 클라이언트 요청이 있는 경우 JWT 인증 토큰을 체크할 지 여부 확인 
// 2. 클라이언트로 부터 전송된 'Authorization' 요청 헤더에서 JWT 토큰 정보를 추출하여 JWT 토큰을 검증하고, 
//    UsernamePasswordAuthenticationToken 인증 객체를 생성하여 SecurityContext에 저장한다.


@Component  //?
@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter { // OncePerRequestFilter : HTTP 요청 마다 실행되도록 보장되는 필터를 구현

    
    /*
     * false : 클라이언트 요청이 있는 경우 JWT Access Token을 체크한다.
     * true : 클라이언트 요청이 있는 경우 JWT Access Token을 체크하지 않는다.
     */

    // Preflight 요청은 Access Token을 체크하지 않는다.
    // Preflight 요청 (CORS Preflight Request)
    // 웹 브라우저가 CORS 정책을 따르는 서버에 HTTP 요청을 보내기 전에, 요청이 허용될지를 확인하기 위해 사전에 보내는 OPTIONS 요청입니다.  
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        log.info("Request URI : {}", path);

        if (request.getMethod().equalsIgnoreCase("OPTIONS") || path.startsWith("/api/v1/members/")) {
            return true;
        }        
        
        return false;
    }
    

    /*
     * shouldNotFilter 메서드가 true를 반환하면, doFilterInternal 메서드는 호출되지 않는다.
     * shouldNotFilter 메서드가 false를 반환하면, doFilterInternal 메서드가 호출되어 요청을 처리한다.
    */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String  authHeader = request.getHeader("Authorization");

        log.info("Authorization Header : {} ", authHeader); //  [Bearer][공백][JWT문자열]

        try {
            // [Bearer][공백][JWT문자열]
            String accessToken = authHeader.substring(7);

            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("claims : {}", claims);

            String email = (String)claims.get("email");
            String password = (String)claims.get("password");
            String nickname = (String)claims.get("nickname");
            @SuppressWarnings("unchecked")
            List<String> roleNames = (List<String>)claims.get("roleNames");

            MemberDto memberDto = new MemberDto(email, password, nickname, roleNames);  // Principal

            log.info("memberDto : {}", memberDto);
            
            /*
             * UsernamePasswordAuthenticationToken :                      
             * 인증에 성공한 경우 사용자 정보(principal), credentials(자격증명), 권한 목록(authorities), 그리고 추가 정보를 포함하는 인증 객체를 생성한다.
             * 이 객체는 Spring Security의 SecurityContext에 저장되어 인증 정보를 유지할 수 있다.
             */   
            
            UsernamePasswordAuthenticationToken   authentication = 
                    new UsernamePasswordAuthenticationToken(memberDto, password, memberDto.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication( authentication);
            
            filterChain.doFilter(request, response);
             

        } catch (Exception e) {

            // 인증에 실패한 경우
            log.error("Error : {}", e.getMessage());

            Throwable cause = e.getCause();
            
            if (cause instanceof AccessDeniedException) {

              // jsonStr = gson.toJson(Map.of("error", "ERROR_ACCESS_DENIED"));
              throw (AccessDeniedException)cause;

            } else {

                Gson gson = new Gson();

                String jsonStr = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
                
                response.setContentType("application/json; charset=UTF-8");

                PrintWriter pw = response.getWriter();
                
                pw.println(jsonStr);
    
                pw.close();

            }

           
            

        }
        
    }

    

}
