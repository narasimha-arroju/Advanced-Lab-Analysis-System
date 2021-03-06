package org.alas.backend.configurations;

import lombok.var;
import org.alas.backend.repositories.UserRepository;
import org.alas.backend.security.jwt.JwtAuthenticationFilter;
import org.alas.backend.security.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                JwtProvider tokenProvider,
                                                ReactiveAuthenticationManager reactiveAuthenticationManager) {
        final String[] PROTECTED_PATHS = {
                "/candidate/**",
                "/author/**"
        };

        return http.cors().and().csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(it -> it
                        .pathMatchers(HttpMethod.DELETE, PROTECTED_PATHS).hasRole("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, PROTECTED_PATHS).hasRole("ROLE_ADMIN")
//                        .pathMatchers(HttpMethod.POST, "/candidate/**").hasRole("ROLE_CANDIDATE")
//                        .pathMatchers(HttpMethod.GET, "/candidate/**").hasRole("ROLE_CANDIDATE")
//                        .pathMatchers(HttpMethod.POST, "/author/**").hasRole("ROLE_AUTHOR")
//                        .pathMatchers(HttpMethod.GET, "/author/**").hasRole("ROLE_AUTHOR")
                        .pathMatchers(PROTECTED_PATHS).authenticated()
                        .anyExchange().permitAll()
                )
                .addFilterAt(new JwtAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();


    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {

        return username -> users.findByUsername(username)
                .map(u -> User
                        .withUsername(u.getUsername()).password(u.getPassword())
                        .authorities(u.getRoles().toArray(new String[0]))
                        .accountExpired(!u.isActive())
                        .credentialsExpired(!u.isActive())
                        .disabled(!u.isActive())
                        .accountLocked(!u.isActive())
                        .build()
                );
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}