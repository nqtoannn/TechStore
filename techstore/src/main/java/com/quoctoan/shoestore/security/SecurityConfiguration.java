package com.quoctoan.shoestore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static com.quoctoan.shoestore.Enum.Permission.*;
import static com.quoctoan.shoestore.Enum.Role.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors(withDefaults())
                .authorizeHttpRequests()
                .requestMatchers(
                        "/shoestore/api/auth/**",
                        "/shoestore/api/productItem/**",
                        "/shoestore/api/products/**",
                        "/shoestore/api/review/**",
                        "/shoestore/api/category/**",
                        "/shoestore/api/payment/**",
                        "/shoestore/api/account/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                )
                .permitAll()
                .requestMatchers("/shoestore/api/management/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .requestMatchers("/shoestore/api/employee/**").hasAnyRole(EMPLOYEE.name(), ADMIN.name())
                .requestMatchers("/shoestore/api/customer/**").hasAnyRole(CUSTOMER.name(), ADMIN.name(), EMPLOYEE.name())
                .requestMatchers("/shoestore/api/address/**").hasAnyRole(CUSTOMER.name(), ADMIN.name())
                .requestMatchers("/shoestore/api/users/**").hasAnyRole(CUSTOMER.name(), EMPLOYEE.name(), ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout();
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
