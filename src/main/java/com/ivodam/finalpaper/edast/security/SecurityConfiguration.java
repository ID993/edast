package com.ivodam.finalpaper.edast.security;

import com.ivodam.finalpaper.edast.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfiguration {

  private final UserDetailsServiceImpl userDetailsService;

  public SecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider
  authenticationProvider(PasswordEncoder passwordEncoder) {

    var authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return authenticationProvider;
  }

  @Bean
  public SecurityFilterChain
  filterChain(HttpSecurity http,
              DaoAuthenticationProvider authenticationProvider)
      throws Exception {

    http.authenticationProvider(authenticationProvider)
        .csrf(Customizer.withDefaults())
        .authorizeHttpRequests(
            authorize
            -> authorize
                   .requestMatchers("/", "/register", "/login",
                                    "/forgot-password", "/forgot-password/**",
                                    "/error", "/styles/**", "/js/**",
                                    "/json/**", "/webjars/**")
                   .permitAll()
                   .requestMatchers("/admin/**", "/users/**")
                   .hasRole("ADMIN")
                   .requestMatchers(HttpMethod.POST, "/account/delete")
                   .hasAnyRole("ADMIN", "USER")
                   .requestMatchers("/work-requests/all")
                   .hasRole("ADMIN")
                   .requestMatchers(HttpMethod.GET, "/work-requests",
                                    "/user-work-requests/**",
                                    "/search-work-requests")
                   .hasRole("USER")
                   .requestMatchers(HttpMethod.POST, "/work-requests",
                                    "/work-requests/delete/**")
                   .hasRole("USER")
                   .anyRequest()
                   .authenticated())
        .formLogin(
            form -> form.loginPage("/login").defaultSuccessUrl("/", true))
        .logout(logout
                -> logout.logoutUrl("/logout")
                       .logoutSuccessUrl("/")
                       .clearAuthentication(true)
                       .invalidateHttpSession(true)
                       .deleteCookies("JSESSIONID"));

    return http.build();
  }
}