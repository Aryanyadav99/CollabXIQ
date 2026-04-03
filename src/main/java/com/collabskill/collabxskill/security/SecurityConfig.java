package com.collabskill.collabxskill.security;


import com.collabskill.collabxskill.Service.AppSettingsService;
import com.collabskill.collabxskill.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // JWT utility for parsing & generating tokens
    @Autowired
    private JwtUtil jwtUtil;

    // User DB access
    @Autowired
    private UserRepository userRepository;

    // App level settings (maintenance mode etc)
    @Autowired
    private AppSettingsService appSettingsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors-> {}) // use default CORS configuration defined in WebConfig
                .authorizeHttpRequests(auth -> auth
                        // allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // public endpoints
                        .requestMatchers("/ws-chat/**",
                                "/api/users/forgot-password",
                                "/api/users/reset-password",
                                "/api/users/resend-otp",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/index.html",
                                "/csrf-token")
                        .permitAll()
                        // signup
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // login
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        // OTP verify
                        .requestMatchers(HttpMethod.POST, "/api/users/verify-otp").permitAll()
                        // refresh token
                        .requestMatchers(HttpMethod.POST, "/api/users/refresh").permitAll()
                        // everything else needs authentication
                        .anyRequest().authenticated()
                )
                // add JWT filter before Spring authentication filter
                .addFilterBefore(
                        new JwtAuthFilter(jwtUtil, userRepository, appSettingsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
