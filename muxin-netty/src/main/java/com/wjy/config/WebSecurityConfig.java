package com.wjy.config;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.wjy.response.CommonResponse;
import com.wjy.response.ResponseUtil;
import com.wjy.response.ResultCode;
import com.wjy.security.TokenAuthenticationFilter;
import com.wjy.security.filter.ValidateCodeFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

import java.util.Calendar;
import java.util.Map;

@Slf4j
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${jwt.sign}")
    private String tokenSign;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;
    @Autowired
    private ExtendSecurityConfig extendSecurityConfig;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(extendSecurityConfig);
        //放开swagger
        http.authorizeRequests()
                .antMatchers("/swagger-ui.html", "/swagger-resources/**"
                        , "/images/**", "/webjars/**", "/v2/api-docs", "/configuration/ui"
                        , "/configuration/security").permitAll();
        http.securityContext().securityContextRepository(new NullSecurityContextRepository());
        http.authorizeRequests().antMatchers("/code/*").permitAll();
        http.authorizeRequests().antMatchers("/user/regist").permitAll();
        http.authorizeRequests().antMatchers("/user/**").permitAll();
//        http.authorizeRequests().anyRequest().access("@rbacService.hasPermission(request,authentication)");
        http.authorizeRequests().anyRequest().authenticated();

        http.csrf().disable();
        http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
        http.addFilterBefore(new TokenAuthenticationFilter(tokenSign), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler((req, resp, ex) -> {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            CommonResponse commonResponse= ResponseUtil.error(ResultCode.ACCESS_ERROR);
            String json = JSON.toJSONString(commonResponse);
            resp.getWriter().print(json);
        });
        http.exceptionHandling().authenticationEntryPoint((req, resp, ex) -> {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            CommonResponse commonResponse= ResponseUtil.error(ResultCode.AUTH_ERROR);
            String json = JSON.toJSONString(commonResponse);
            resp.getWriter().print(json);
        });
        http.formLogin().loginProcessingUrl("/user/login").successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);


    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
