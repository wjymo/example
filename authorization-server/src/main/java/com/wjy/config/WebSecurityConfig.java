package com.wjy.config;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.wjy.response.CommonResponse;
import com.wjy.response.ResponseUtil;
import com.wjy.response.ResultCode;
import com.wjy.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

import java.util.Calendar;
import java.util.Map;

@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /*@Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }*/
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.securityContext().securityContextRepository(new NullSecurityContextRepository());
        http.authorizeRequests().antMatchers("/oauth/token").permitAll();
        http.csrf().disable();
        /*http.addFilterBefore(new TokenAuthenticationFilter("xiaoer"), UsernamePasswordAuthenticationFilter.class);
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

        http.formLogin().loginProcessingUrl("/user/login")*/
                /*.successHandler((request,response, authentication)->{
            String tokenSign = "xiaoer";
            Algorithm algorithm = Algorithm.HMAC256(tokenSign);
            Object principal = authentication.getPrincipal();
            String username = authentication.getName();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            String token = JWT.create().withClaim("username", username)
                    .withClaim("authorities", Joiner.on(",").join(authentication.getAuthorities().toArray()))
//                    .withClaim("ip", remoteIp)
                    .withExpiresAt(calendar.getTime()).sign(algorithm);

            Map<String, Object> map = Maps.newHashMap();
            map.put("token", token);
            map.put("username", authentication.getName());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().print(JSON.toJSONString(ResponseUtil.success(map)));
        }).failureHandler((request,response, e)->{
            log.info("登录失败，异常信息：{}", e.getMessage());
            String errorMessgae = "";
            if (e instanceof BadCredentialsException) {
                errorMessgae = "密码错误";
            } else if (e instanceof AuthenticationServiceException) {
                errorMessgae = "账号不存在";
            } else if (e instanceof AccountExpiredException) {
                errorMessgae = "账户异地登录";
            } else if (e instanceof AuthenticationCredentialsNotFoundException) {
                errorMessgae = "账户已过期";
            } else if (e instanceof DisabledException) {
                errorMessgae = "账户被禁用";
            } else if (e instanceof LockedException) {
                errorMessgae = "账户未审核";
            } else {
                errorMessgae = "未知错误";
            }
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().print(JSON.toJSONString(ResponseUtil.error(errorMessgae)));
        })*/;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
