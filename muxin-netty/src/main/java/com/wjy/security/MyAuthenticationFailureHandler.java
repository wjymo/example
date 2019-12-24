package com.wjy.security;

import com.alibaba.fastjson.JSON;
import com.wjy.response.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException{
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
            String message = e.getMessage();
            if(StringUtils.isEmpty(message)){
                errorMessgae = "未知错误";
            }else {
                errorMessgae=message;
            }
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().print(JSON.toJSONString(ResponseUtil.error(errorMessgae)));
    }
}
