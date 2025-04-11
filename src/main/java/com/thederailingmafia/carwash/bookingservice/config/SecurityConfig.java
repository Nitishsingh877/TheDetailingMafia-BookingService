package com.thederailingmafia.carwash.bookingservice.config;

import com.thederailingmafia.carwash.bookingservice.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(a -> a.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/order/health").permitAll()
                        .requestMatchers("/api/order/wash-now", "/api/order/schedule").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/api/order/pending", "/api/order/assign").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/order/current", "/api/order/past", "/api/order/**", "api/order/{id}")
                        .hasAnyAuthority("ROLE_CUSTOMER", "ROLE_WASHER", "ROLE_ADMIN") // Add ROLE_ prefix
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}