package com.e_commerce.SNEAKERHEAD.Security;

import com.e_commerce.SNEAKERHEAD.Service.CustomOAuth2UserService;
import com.e_commerce.SNEAKERHEAD.Service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    CustomFailureHandler customFailureHandler;

    @Autowired
    CustomOidcUserService customOidcUserService;

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the "ROLE_" prefix
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return authorities -> authorities.stream()
                .map(authority -> {
                    if (authority.getAuthority().equals("OIDC_USER")) {
                        return new SimpleGrantedAuthority("USER"); // Map to "ROLE_USER"
                    }
                    return authority;
                })
                .collect(Collectors.toList());
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**","/userlogin","/signup","/otpverification",
                                "/otpverification/data","/signup/userdata","/oauth2/authorization/google",
                                "/login/oauth2/code/google","/resend-otp","/forgetpassword/{email}","/forgetpassword",
                                "/otpverification/{email}","/resetpassword","/resetpassword/data","/",
                                "/user/shop").permitAll()
                        .requestMatchers("/css/**","/assets/**","/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin( form -> form
                        .loginPage("/userlogin")
                        .loginProcessingUrl("/logindata")
                        .passwordParameter("password")
                        .usernameParameter("username")
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll()
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        )
                .oauth2Login(oauth -> oauth
                        .loginPage("/userlogin")
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(grantedAuthoritiesMapper())
                                .oidcUserService(customOidcUserService))
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }



}
