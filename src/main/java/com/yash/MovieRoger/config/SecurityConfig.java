package com.yash.MovieRoger.config;

import com.yash.MovieRoger.service.UserAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //for security at method level for any particular type of user
public class SecurityConfig {
    public static final String ADMIN_AUTH = "ADMIN";
    public static final String USER_AUTH = "USER";

    @Bean
    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("Yash")
//                .password(encoder.encode("user_1"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withUsername("John")
//                .password(encoder.encode("user_2"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
        return new UserAuthService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/movie/**", "/theater/**", "/show/add").hasAuthority("ADMIN")
                        .requestMatchers("/review/**","/show/search","/movie/title", "/ticket/**").hasAuthority("USER")
                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
