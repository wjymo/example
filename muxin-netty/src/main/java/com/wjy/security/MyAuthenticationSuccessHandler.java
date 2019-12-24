package com.wjy.security;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.wjy.response.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${jwt.sign}")
    private String tokenSign;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Algorithm algorithm = Algorithm.HMAC256(tokenSign);
        String username = authentication.getName();
        Object principal = authentication.getPrincipal();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        String token = JWT.create().withClaim("username", username)
                .withClaim("authorities", Joiner.on(",").join(authentication.getAuthorities().toArray()))
                .withExpiresAt(calendar.getTime()).sign(algorithm);

        Map<String, Object> map = Maps.newHashMap();
        map.put("token", token);
        map.put("username", authentication.getName());

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().print(JSON.toJSONString(ResponseUtil.success(map)));
    }
}
