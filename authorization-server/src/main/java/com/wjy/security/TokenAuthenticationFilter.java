package com.wjy.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * create by Administrator on 2019/11/20
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {


    @Override
    public void afterPropertiesSet() throws ServletException {
        super.initFilterBean();
    }

    private String tokenSign;

    public TokenAuthenticationFilter(String tokenSign) {
        this.tokenSign = tokenSign;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.debug("【token过滤器】current request: {}", request.getRequestURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("用户{}已认证，无需token校验", authentication.getName());
        } else {
            try {
                // 提取到请求中的token值
                String token = extractToken(request);
                /*if (StringUtils.isNotBlank(token)) {
                    DecodedJWT decodedJWT = verify(token);
                    if (decodedJWT != null) {
                        String jti = decodedJWT.getId();
                        Map<String, Claim> claimMap = decodedJWT.getClaims();
                        if (claimMap != null) {
                            Claim usernameClaim = claimMap.get("username");
                            String username = usernameClaim.asString();
                            String authorities = claimMap.get("authorities").asString();
//                            Claim idClaim = claimMap.get("userId");
//                            WebApplicationContext cxt = ContextLoader.getCurrentWebApplicationContext();
//                            Integer id = idClaim.asInt();
                            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, "null"
                                    , grantedAuthorities);
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                }*/
            } catch (Exception failed) {
                /*if (failed instanceof TokenExpiredException) {
                    log.info("token过期");
                    response.setHeader("xxx", "xxxhuyao");
                }*/
                SecurityContextHolder.clearContext();
                failed.printStackTrace();
                log.error("Authentication request failed: " + failed);
            }
        }
        chain.doFilter(request, response);

    }

//    private DecodedJWT verify(String token) {
//        try {
//            if (StringUtils.isNotBlank(tokenSign)) {
//                Algorithm algorithm = Algorithm.HMAC256(tokenSign);
//                DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
//                Date expiresAt = jwt.getExpiresAt();
//                String id = jwt.getId();
//                return jwt;
//            }
//        }  catch (TokenExpiredException e) {
//            log.error(e.getMessage(),e);
//        }
//        return null;
//    }
//
//    public static void main(String[] args) throws IOException {
//        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(
//                new File("D:\\Documents\\Tencent Files\\245745490\\FileRecv\\sk")));
//        InputStream resourceAsStream = TokenAuthenticationFilter.class.getClassLoader().getResourceAsStream("sk");
//        DataInputStream dataInputStream2 = new DataInputStream(resourceAsStream);
//        byte[] bytes = new byte[48];
//        int len = dataInputStream2.read(bytes);
//        String s = new String(bytes);
////        Algorithm algorithm = Algorithm.HMAC256("<'\u0006貵?蓂旡?蟶6'%g鉊傗z");
//        Algorithm algorithm = Algorithm.HMAC256(bytes);
//        DecodedJWT jwt = JWT.require(algorithm).build().verify(
//                "eyJhbGciOiJIUzI1NiIsImlhdCI6MTUzNDc1NTMwMywiZXhwIjoxNTM0NzU4OTAzfQ.eyJ1aWQiOiJ1c2VyMDAwIiwidGlkIjoicHJvamVjdDEyMzQ1IiwidGltZSI6NjcyMzY4MjA2N30.aL-z9PYmsKNtrruIp-uSN8Cxg4skSF3iqQRzdzaYG-U");
//    }


    public static String ACCESS_TOKEN = "access_token";


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

    private String extractToken(HttpServletRequest request) {
        String token = extractHeaderToken(request);
        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters.");
            token = request.getParameter(ACCESS_TOKEN);
            if (token == null) {
                logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
            }
        }
        return token;
    }


}