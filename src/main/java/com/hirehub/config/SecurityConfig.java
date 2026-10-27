
package com.hirehub.config;

import com.hirehub.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public frontend files
                        .requestMatchers(
                                "/", "/index.html", "/style.css", "/script.js"
                        ).permitAll()

                        // Login and registration
                        .requestMatchers("/api/auth/**", "/error").permitAll()

                        // Anyone can view jobs
                        .requestMatchers(HttpMethod.GET, "/api/jobs", "/api/jobs/**")
                        .permitAll()

                        // Only recruiters can create, update, or delete jobs
                        .requestMatchers("/api/jobs", "/api/jobs/**")
                        .hasRole("RECRUITER")

                        // Candidates can apply
                        .requestMatchers(HttpMethod.POST,
                                "/api/applications/candidate/*/job/*")
                        .hasRole("CANDIDATE")

                        // Other application endpoints require authentication
                        .requestMatchers("/api/applications/**").authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
