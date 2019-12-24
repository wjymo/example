package com.wjy.security.filter;

import com.alibaba.fastjson.JSON;
import com.wjy.constant.CommonConstant;
import com.wjy.exception.ValidateCodeException;
import com.wjy.validate.code.ImageCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private Set<String> urls=new HashSet<>();
    /**
     * 初始化要拦截的url配置信息
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        urls.add("/user/login");
        urls.add("/sms/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        for (String url : urls) {
            boolean match = antPathMatcher.match(requestURI, url);
            if(match){
                HttpSession session = request.getSession();
                ImageCode codeInCache = (ImageCode)session.getAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY);
                String codeInRequest = request.getParameter(CommonConstant.SUBMIT_IMAGE_CODE_IN_SESSION_KEY);
                if(StringUtils.isEmpty(codeInRequest)){
                    codeInRequest=request.getParameter(CommonConstant.SUBMIT_SMS_CODE_IN_SESSION_KEY);
                }
                if(codeInCache ==null){
                    String codeKey = request.getParameter("imageCodeKey");
                    if(StringUtils.isEmpty(codeKey)){
                        codeKey=request.getParameter("smsCodeKey");
                    }
                    String codeInCacheStr = (String)redisTemplate.opsForValue().get(codeKey);
                    try {
                        if (StringUtils.isBlank(codeInRequest)) {
                            throw new ValidateCodeException("验证码的值不能为空");
                        }
                        if (StringUtils.isEmpty(codeInCacheStr)) {
                            session.removeAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY);
                            throw new ValidateCodeException("验证码已过期");
                        }

                        if (!StringUtils.equals(codeInCacheStr, codeInRequest)) {
                            throw new ValidateCodeException("验证码不匹配");
                        }
                        session.removeAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY);
                        log.info("验证码校验通过");
                    } catch (ValidateCodeException e) {
                        log.error(e.getMessage(),e);
                        authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                        return;
                    }
                }else {
                    try {
                        if (StringUtils.isBlank(codeInRequest)) {
                            throw new ValidateCodeException("验证码的值不能为空");
                        }
                        if (codeInCache == null) {
                            throw new ValidateCodeException("验证码不存在");
                        }

                        if (codeInCache.isExpried()) {
                            session.removeAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY);
                            throw new ValidateCodeException("验证码已过期");
                        }

                        if (!StringUtils.equals(codeInCache.getCode(), codeInRequest)) {
                            throw new ValidateCodeException("验证码不匹配");
                        }
                        session.removeAttribute(CommonConstant.IMAGE_CODE_IN_SESSION_KEY);
                        log.info("验证码校验通过");
                    } catch (ValidateCodeException e) {
                        log.error(e.getMessage(),e);
                        authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                        return;
                    }
                }
                break;
            }
        }
         filterChain.doFilter(request,response);
    }
}
