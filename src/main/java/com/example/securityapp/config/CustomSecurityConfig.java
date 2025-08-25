package com.example.securityapp.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.securityapp.security.filter.JWTCheckFilter;
import com.example.securityapp.security.handler.ApiLoginFailHandler;
import com.example.securityapp.security.handler.ApiLoginSuccessHandler;
import com.example.securityapp.security.handler.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Spring Boot 3.1 Version 부터 Spring Security 부분이 많이 변경됨!!

/* 
    - EnableWebSecurity :    
    - Spring Security 필터 체인을 활성화하여 애플리케이션의 모든 HTTP 요청이 Security Context를 통해 처리되도록 설정한다.
*/

/* 
    - @EnableMethodSecurity :
    - Spring Security의 다양한 어노테이션을 사용하여 메서드 수준에서 접근 제어를 활성화한다.
      (예: @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed)
    - 이를 통해 서비스 계층이나 컨트롤러 계층에서 세밀한 보안 규칙을 정의할 수 있습니다.
 */


@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class CustomSecurityConfig {

    /*
     * 1. CSRF (사이트 간 요청 위조, Cross-Site Request Forgery) 설정  비활성화
     *    - GET 방식을 제외한 모든 요청에 CSRF 토큰을 사용하는데 API 서버 구현 시는 사용하지 않는다.
     * 2. CORS 설정 활성화
     * 3. 세션 비활성화 : JWT 인증 토큰
     * 4. 패스워드 암호화
     */    

        
    /*
    * Spring Security의 SecurityFilterChain을 설정하여 애플리케이션의 보안 구성 설정한다.
    * @Bean으로 SecurityFilterChain 정의한다.
    * HttpSecurity : HTTP 보안 설정을 구성하는데 사용된다.
    */
    @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("-------------------------CustomSecurityConfig.filterChain");
    
        // CORS 설정 
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());            
        });


        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());


        // 세션 비활성화
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // 로그인 요청
        // 폼 기반 로그인 요청 처리 (POST) 
        // UsernamePasswordAuthenticationFilter가 실행되어 로그인 요청 처리.
        http.formLogin(config -> {  // 로그인 관련 설정
            //username, password
            config.loginPage("/api/v1/members/login");         //  로그인 폼  지정 GET
			config.loginProcessingUrl("/api/member/login");    //  POST
            // config.usernameParameter("email");
            // config.passwordParameter("pwd");
            config.successHandler(new ApiLoginSuccessHandler());      
            config.failureHandler(new ApiLoginFailHandler());
        });


        // 일반 요청
        // Spring Security 필터 체인에 새로운 커스텀 필터를 등록한다.
        // UsernamePasswordAuthenticationFilter 보다 JWTCheckFilter 를 먼저 수행한다.
        
        /* 
         * 1. /api/v1/members/login 요청한 경우
         * - usernamePasswordAuthenticationFilter  필터를 사용하여 로그인 처리를 수행한다.
         * - UserDetailsService의 loadUserByUsername 메소드를 호출한다.
         * 2. /api/v1/items 요청한 경우
         * - JWTCheckFilter 에서 SecurityContextHolder에 인증 정보가 저장되므로, 
         * - 이후 UsernamePasswordAuthenticationFilter는 별도의 추가 인증을 수행하지 않고 통과된다.
         */

        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);  


        // Spring Security에서 발생하는 권한 관련 예외를 처리
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        return http.build();
     }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 패스워드 암호화
        return new BCryptPasswordEncoder();
    }


     /*
	  *  CORR(Cross-Origin Resource Sharing) 설정
	  */
     @Bean
     public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
			
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));   

        // configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));   
        
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // 클라이언트가 인증 정보(예: 쿠키, Authorization 헤더, TLS 클라이언트 인증)를 요청 헤더에 포함할 수 있도록 허용한다.
        configuration.setAllowCredentials(true);
		
        // 모든 경로에 대해 CORS 설정을 적용한다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    
    }

}
