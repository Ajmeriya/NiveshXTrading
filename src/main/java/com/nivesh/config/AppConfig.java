package com.nivesh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /// this is first impl of securiety
        /// this will create the pre-built login form also
        http.sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(Authorizae -> Authorizae.requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())

                //@This JwtTokenValidator will check the token is valid or not
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                //when we connect our backend to frontend then this will help to avoid the cors error
                .cors(cors -> cors.configurationSource(corsConfigrationSource()));

        return http.build();
    }

    /**If your frontend (e.g., React, Angular) runs on a different domain or port (http://localhost:3000) than your backend (http://localhost:8080), the browser blocks requests unless CORS is properly configured. */
    // CORS Configuration
    private CorsConfigurationSource corsConfigrationSource() {
        return request -> null; // minimal valid return
    }
}
