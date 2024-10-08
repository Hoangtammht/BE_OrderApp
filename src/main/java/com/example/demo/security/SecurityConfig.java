package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configurationSource = new CorsConfiguration();
        configurationSource.setAllowedOrigins(List.of("*"));
        configurationSource.setAllowedMethods(List.of("*"));
        configurationSource.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configurationSource);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        http.cors().and();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests()
                .antMatchers("/user/loginUser/**",
                        "/swagger-ui.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/user/registerUser").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(GET, "/user/getListUserByRole").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(PUT, "/user/editAccount").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(GET, "/role/getAllRoles").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(PUT, "/user/deleteAccount").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(GET, "/class/getAllClass").permitAll();
        http.authorizeRequests().antMatchers(GET, "/schedule/getListSchedule").permitAll();
        http.authorizeRequests().antMatchers(PUT, "/class/assignClass").hasAnyAuthority("Admin");
        http.authorizeRequests().antMatchers(POST, "/menu/addDishToMenu").hasAnyAuthority("Chef");
        http.authorizeRequests().antMatchers(GET, "/menu/getMenuByDate").hasAnyAuthority("Teacher", "Chef");
        http.authorizeRequests().antMatchers(POST, "/order/createOrder").hasAnyAuthority("Teacher");
        http.authorizeRequests().antMatchers(POST, "/order/editOrder").hasAnyAuthority("Teacher");
        http.authorizeRequests().antMatchers(GET, "/order/getListOrdersByDate").hasAnyAuthority("Accountant", "Chef");
        http.authorizeRequests().antMatchers(GET, "/order/getOrderByTeacherName").hasAnyAuthority("Accountant", "Teacher", "Chef");
        http.authorizeRequests().antMatchers(PUT, "/order/confirmOrder").hasAnyAuthority("Accountant");
        http.authorizeRequests().antMatchers(GET, "/menu/getListMenuForAccountant").hasAnyAuthority("Accountant");
        http.authorizeRequests().antMatchers(PUT, "/menu/updatePriceOfDish").hasAnyAuthority("Accountant");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


}