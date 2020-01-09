package com.hrm.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/9 10:01
 */
@Getter
@Setter
@ConfigurationProperties("jwt.config")
public class JwtUtil {
    // 私钥
    private String key;
    // 过期时间
    private long ttl;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * 签发
     * @param id 用户id
     * @param subject 用户名
     * @param map 自定义参数
     * @return
     */
    public String createJWT(String id, String subject, Map<String,Object> map){
        long now=System.currentTimeMillis();
        long exp=now+ttl;
        JwtBuilder jwtBuilder = Jwts.builder().setId(id)
                .setSubject(subject).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, key);
        for(Map.Entry<String,Object> entry:map.entrySet()) {
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }
        if(ttl>0){
            jwtBuilder.setExpiration( new Date(exp));
         }
        String token = jwtBuilder.compact();
        return token;
    }

    /**
     * 解析
     * @param token
     * @return
     */
    public Claims parseJWT(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
        }
        return claims;
    }
}
