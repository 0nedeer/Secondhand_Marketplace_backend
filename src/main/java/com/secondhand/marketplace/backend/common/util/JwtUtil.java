package com.secondhand.marketplace.backend.common.util;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @Value("${jwt.reset-expiration:300000}")
    private Long resetExpiration;

    // Token 黑名单
    private final ConcurrentHashMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成重置密码 Token
     */
    public String generateResetToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + resetExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "reset")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证并解析重置 Token
     */
    public Long validateResetToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String type = claims.get("type", String.class);
            if (!"reset".equals(type)) {
                throw new BusinessException("无效的Token类型");
            }

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new BusinessException("Token已过期");
        } catch (JwtException e) {
            throw new BusinessException("Token无效: " + e.getMessage());
        }
    }

    /**
     * 验证 Token 并获取用户ID
     */
    public Long validateTokenAndGetUserId(String token) {
        // 移除 Bearer 前缀
        String cleanToken = token;
        if (cleanToken != null && cleanToken.startsWith("Bearer ")) {
            cleanToken = cleanToken.substring(7);
        }

        // 检查黑名单
        if (isBlacklisted(cleanToken)) {
            throw new BusinessException("Token已被注销");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(cleanToken)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new BusinessException("Token已过期");
        } catch (JwtException e) {
            throw new BusinessException("Token无效");
        }
    }

    /**
     * 从 Token 中获取用户ID（不验证过期时间，慎用）
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException e) {
            throw new BusinessException("Token无效");
        }
    }

    /**
     * 将 Token 加入黑名单
     */
    public void addToBlacklist(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        blacklist.put(token, true);
    }

    /**
     * 检查 Token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /**
     * 验证 Token 是否有效（不抛异常）
     */
    public boolean isValidToken(String token) {
        try {
            validateTokenAndGetUserId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}