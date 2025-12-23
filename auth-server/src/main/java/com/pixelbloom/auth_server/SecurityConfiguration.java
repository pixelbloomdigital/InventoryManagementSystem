package com.pixelbloom.auth_server;

import com.pixelbloom.auth_server.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/login", "/css/**", "/js/**").permitAll();
                    registry.requestMatchers("/home", "/register/**").permitAll();
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/user/**").hasRole("USER");
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .loginProcessingUrl("/login")   // important
                          //  .defaultSuccessUrl("/home", true)
                           // .successHandler(new CustomAuthenticationSuccessHandler())
                            .permitAll();
                            })
                .build();
    }

   /* @Bean
    public UserDetailsService userDetailsService() {
        UserDetails normalUser = User.builder()
                .username("user")
                .password("$2a$12$HFYngHcfLROlm57FC2kZiOn2mlpkG8qSduzbY7Q7QtktJ24Q7zul2")
                .roles("USER")
                .build();
       UserDetails adminUser = User.builder()
               .username("admin")
               .password("$2a$12$Nj.mDTe6Fn.cd0KGjhSi/OUYbXR5/xUUmKwYSXjZtrfuK3DXh5KbS")
               .roles("USER","ADMIN")
               .build();
        return new InMemoryUserDetailsManager(normalUser,adminUser);
    }*/

    @Bean
    public UserDetailsService userDetailsService(){
        return myUserDetailsService;
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}




