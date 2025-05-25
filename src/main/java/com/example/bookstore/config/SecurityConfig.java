package com.example.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Temporarily disable CSRF for easier development
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/auth/**", "/manual-logout").permitAll() // Cho phép truy cập login, register và tài nguyên tĩnh
                .requestMatchers("/dashboard/**").hasRole("ADMIN") // Chỉ admin vào dashboard
                .requestMatchers("/home/**").authenticated() // Home yêu cầu đăng nhập
                .anyRequest().authenticated() // Các yêu cầu khác cần đăng nhập
            )
            .formLogin(form -> form
                .loginPage("/login") // Trang login tùy chỉnh
                .loginProcessingUrl("/login") // URL xử lý login
                .defaultSuccessUrl("/welcome", true) // Chuyển hướng sau đăng nhập thành công
                .failureUrl("/login?error=true") // Chuyển hướng khi đăng nhập thất bại
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}