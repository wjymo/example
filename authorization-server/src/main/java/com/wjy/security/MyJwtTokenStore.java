package com.wjy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyJwtTokenStore extends JwtTokenStore {
    @Value("${token.sign}")
    private String tokenSign;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer
     */
    public MyJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
        super(jwtTokenEnhancer);
    }

    /**
     * 存储用户名对应jti，jti对应jwt
     * @param token
     * @param authentication
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String username = authentication.getName();
        String jwtStr = token.getValue();
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        String refreshJwtStr = refreshToken.getValue();
        Algorithm algorithm = Algorithm.HMAC256(tokenSign);
//        DecodedJWT refreshjwt = JWT.require(algorithm).build().verify(refreshJwtStr);
//        String refreshJti = refreshjwt.getId();
        DecodedJWT jwt = JWT.require(algorithm).build().verify(jwtStr);
        String jti = jwt.getId();
        redisTemplate.opsForValue().set(username,jti);
//        redisTemplate.opsForList().rightPushAll(username,jti,refreshJti);
        Map<String,String> map=new HashMap<>();
        map.put("jwt",jwtStr);
        map.put("refresh_jwt",refreshJwtStr);
        redisTemplate.opsForHash().putAll(jti,map);
//        redisTemplate.opsForValue().set(jti,jwtStr);
//        redisTemplate.opsForValue().set(refreshJti,refreshJwtStr);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        // gh-807 Approvals (if any) should only be removed when Refresh Tokens are removed (or expired)
//        String refreshJwtStr = refreshToken.getValue();
//        Algorithm algorithm = Algorithm.HMAC256(tokenSign);
//        DecodedJWT refreshjwt = JWT.require(algorithm).build().verify(refreshJwtStr);
//        String refreshjwtId = refreshjwt.getId();
//        Boolean delete = redisTemplate.delete(refreshjwtId);
//        System.out.println(delete);
    }


    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
//        String value = refreshToken.getValue();
//        Algorithm algorithm = Algorithm.HMAC256(tokenSign);
//        DecodedJWT jwt = JWT.require(algorithm).build().verify(value);
//        Date expiresAt = jwt.getExpiresAt();
//        String jti = jwt.getId();
//        redisTemplate.opsForHash().put(jti,"refresh_jwt",value);
    }
}
