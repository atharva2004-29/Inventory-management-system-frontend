package com.example.frontend_orderinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public: login endpoints and static assets
                .requestMatchers("/login", "/customer-login", "/register", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // Only ADMIN can perform write operations (POST, PUT, PATCH, DELETE)
                .requestMatchers(HttpMethod.POST).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")

                // Everything else (GETs) requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")      // Spring Security handles POST /login for admins
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Only one in-memory admin — customers log in via email-only custom flow
        UserDetails admin = User.builder()
                .username("admin.@system.com")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
