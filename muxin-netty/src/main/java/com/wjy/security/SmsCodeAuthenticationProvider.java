package com.wjy.security;

import com.wjy.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    private UserService userService;

    public SmsCodeAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String)authenticationToken.getPrincipal();
        String user=userService.getByMobile(mobile);
        Object details = authentication.getDetails();
        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        SmsCodeAuthenticationToken smsCodeAuthenticationToken=new SmsCodeAuthenticationToken(mobile
                , AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        smsCodeAuthenticationToken.setDetails(details);
        return smsCodeAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
