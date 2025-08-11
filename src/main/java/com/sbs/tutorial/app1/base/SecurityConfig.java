package com.sbs.tutorial.app1.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http // csrf.disable() : 완전 비활성화
        .csrf(csrf -> csrf.ignoringRequestMatchers("/**")) // 특정 경로는 CSRF 제외
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/member/join").permitAll()
            .requestMatchers("/css/**", "/js/**").permitAll()
            .anyRequest().authenticated() // .anyRequest().authenticated() : 특정 경로는 로그인 필요
        )
        .formLogin(form -> form
            .loginPage("/member/login") // GET : 로그인 페이지
            .loginProcessingUrl("/member/login") // POST : 로그인 처리
            .defaultSuccessUrl("/member/profile") // 로그인 성공시 리다이렉트
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/member/logout") // GET : 로그아웃
            .logoutSuccessUrl("/") // 로그아웃 성공시 리다이렉트
            .permitAll()
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
    // 스프링 시큐리티 인증을 처리
    // 커스텀 인증 로직을 구현할 때 필요
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
