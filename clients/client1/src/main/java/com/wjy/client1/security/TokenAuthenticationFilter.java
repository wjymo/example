package com.wjy.client1.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * create by Administrator on 2019/11/20
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final String authUrl = "http://localhost:8888/oauth/token";

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.initFilterBean();
    }

    private String tokenSign;
    private RedisTemplate<String, Object> redisTemplate;
    private RestTemplate restTemplate;

    public TokenAuthenticationFilter(String tokenSign, RedisTemplate<String, Object> redisTemplate, RestTemplate restTemplate) {
        this.tokenSign = tokenSign;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.debug("【token过滤器】current request: {}", request.getRequestURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("用户{}已认证，无需token校验", authentication.getName());
        } else {
            String refreshJtiKeyInRedis = null;
            String jtiKey = null;
            try {
                String headerUsername = request.getHeader("username");
                String jtiKeyInRedis = (String) redisTemplate.opsForValue().get(headerUsername);
//                List<Object> range = redisTemplate.opsForList().range(headerUsername, 0, -1);
//                if (!CollectionUtils.isEmpty(range)) {
//                    String jtiKeyInRedis = (String) range.get(0);
//                    refreshJtiKeyInRedis = (String) range.get(1);
                    jtiKey = extractToken(request);
                    // 提取到请求中的token值
                    //只有当当前的jti和usernam对应在redis中的jti相同时才往下进行，如果不相同，说明此账号已经在别处登录，此次访问无效
                    if (StringUtils.equals(jtiKey, jtiKeyInRedis)) {
                        //List<Object> objects = redisTemplate.opsForHash().multiGet(jtiKey, Arrays.asList("jwt", "refresh_jwt"));
                        String token = (String) redisTemplate.opsForHash().get(jtiKey, "jwt");
//                        String token = (String) redisTemplate.opsForValue().get(jtiKey);
                        if (StringUtils.isNotBlank(token)) {
                            DecodedJWT decodedJWT = verify(token);
                            if (decodedJWT != null) {
                                String jti = decodedJWT.getId();
                                Map<String, Claim> claimMap = decodedJWT.getClaims();
                                if (claimMap != null) {
                                    Claim usernameClaim = claimMap.get("username");
                                    String username = usernameClaim.asString();
                                    String authorities = claimMap.get("roles").asString();
                                    List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                            new UsernamePasswordAuthenticationToken(username, "null"
                                            , grantedAuthorities);
                                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                }
                            }
                        }
                    }
//                }
            } catch (Exception failed) {
                if (failed instanceof TokenExpiredException) {
                    log.info("token过期，自动刷新");
                    if (StringUtils.isNotEmpty(jtiKey)) {
                        String refreshJwt = (String) redisTemplate.opsForHash().get(jtiKey, "refresh_jwt");
//                        String refreshJwt = (String) redisTemplate.opsForValue().get(refreshJtiKeyInRedis);

                        //定义header
                        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
                        String httpBasic = getHttpBasic("huyao", "111");
                        header.add("Authorization", httpBasic);

                        //定义body
                        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                        body.add("grant_type", "refresh_token");
                        body.add("refresh_token", refreshJwt);
                        body.add("scope", "all");

                        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
                        //String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables

                        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
                        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                            @Override
                            public void handleError(ClientHttpResponse response) throws IOException {
                                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                                    super.handleError(response);
                                }
                            }
                        });
                        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

                        //申请令牌信息
                        Map bodyMap = exchange.getBody();
                        if (!CollectionUtils.isEmpty(bodyMap)) {
                            if (bodyMap.get("access_token") != null && bodyMap.get("refresh_token") != null) {
                                String jti = (String) bodyMap.get("jti");
                                response.setHeader("jti", jti);
                                String roles = (String) bodyMap.get("roles");
                                String username = (String) bodyMap.get("username");
                                List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
                                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                        username, "null", grantedAuthorities);
                                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                                //token刷新成功后，将老jti删除，在认证服务器的springSecurityOauth2中是现成的代码，要实现此功能比较麻烦，不如就在这边处理
                                Boolean delete = redisTemplate.delete(jtiKey);
                                System.out.println(delete);
                            }
                        }
                    }
                }
//                SecurityContextHolder.clearContext();
                failed.printStackTrace();
                log.error("Authentication request failed: " + failed);
            }
        }
        chain.doFilter(request, response);

    }

    private DecodedJWT verify(String token) {
        if (StringUtils.isNotBlank(tokenSign)) {
            Algorithm algorithm = Algorithm.HMAC256(tokenSign);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            Date expiresAt = jwt.getExpiresAt();
            String id = jwt.getId();
            return jwt;
        }
        return null;
    }

    public static String ACCESS_TOKEN = "access_token";


    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
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