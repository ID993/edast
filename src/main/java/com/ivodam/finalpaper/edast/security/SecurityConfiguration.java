package com.ivodam.finalpaper.edast.security;

import com.ivodam.finalpaper.edast.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfiguration{

    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http
                //.anonymous().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/")
                .permitAll()
                .requestMatchers("/register")
                .permitAll()
                .requestMatchers("/login")
                .permitAll()
                .requestMatchers("/styles/**")
                .permitAll()
                .requestMatchers("/forgot-password/**")
                .hasRole("ANONYMOUS")
                .requestMatchers("/users/**")
                .hasAnyRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(form -> form.loginPage("/login"))
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        return http.build();
    }


}
