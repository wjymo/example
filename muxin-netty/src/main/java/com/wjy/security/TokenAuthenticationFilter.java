package com.wjy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private String tokenSign;

    public TokenAuthenticationFilter(String tokenSign) {
        this.tokenSign = tokenSign;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("用户{}已认证，无需token校验", authentication.getName());
        } else {
            try {
                // 提取到请求中的token值
                String token = extractHeaderToken(request);
                if (StringUtils.isNotBlank(token)) {
                    DecodedJWT decodedJWT = verify(token);
                    if (decodedJWT != null) {
                        String jti = decodedJWT.getId();
                        Map<String, Claim> claimMap = decodedJWT.getClaims();
                        if (!CollectionUtils.isEmpty(claimMap )) {
                            Claim usernameClaim = claimMap.get("username");
                            String username = usernameClaim.asString();
                            String authorities = claimMap.get("authorities").asString();
                            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                    new UsernamePasswordAuthenticationToken(username, "null", grantedAuthorities);
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                }
            } catch (Exception e) {
                if (e instanceof TokenExpiredException) {
                    log.info("token过期");
//                    response.setHeader("xxx", "xxxhuyao");
                }
                SecurityContextHolder.clearContext();
                log.error("Authentication request failed: {}",e.getMessage(),e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            String authHeaderValue = value;
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }
        return null;
    }

    private DecodedJWT verify(String token) {
        try {
            if (StringUtils.isNotBlank(tokenSign)) {
                Algorithm algorithm = Algorithm.HMAC256(tokenSign);
                DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
                Date expiresAt = jwt.getExpiresAt();
                String id = jwt.getId();
                return jwt;
            }
        }  catch (TokenExpiredException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("111");
        System.out.println(encode);
    }
}
