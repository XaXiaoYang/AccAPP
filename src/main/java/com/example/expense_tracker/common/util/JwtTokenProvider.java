package com.example.expense_tracker.common.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final UserDetailsService userDetailsService;
    private String encodedSecret;

    // 常量定义
    private static final String USER_ID_KEY = "userId";

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        encodedSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    /**
     * 创建 JWT 令牌
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT 令牌
     */
    public String createToken(String username, Long userId) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(USER_ID_KEY, userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, encodedSecret)
                .compact();
    }

    /**
     * 从 JWT 令牌中获取用户名
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从 JWT 令牌中获取用户ID
     * @param token JWT 令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            Object userIdObj = claims.get(USER_ID_KEY);
            if (userIdObj != null) {
                return Long.parseLong(userIdObj.toString());
            }
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token while getting userId: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 JWT 令牌的有效性
     * @param token JWT 令牌
     * @return 令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 JWT 令牌中获取认证信息
     * @param token JWT 令牌
     * @return 认证信息
     */
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        if (username == null) {
            return null;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 从 JWT 令牌中获取用户名
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsername(String token) {
        return getUsernameFromToken(token);
    }

    /**
     * 从 JWT 令牌中获取用户ID
     * @param token JWT 令牌
     * @return 用户ID
     */
    public Long getUserId(String token) {
        try {
            Claims claims = parseClaims(token);
            return Long.parseLong(claims.get(USER_ID_KEY).toString());
        } catch (Exception e) {
            log.error("Error getting userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 HTTP 请求中解析 JWT 令牌
     * @param req HTTP 请求
     * @return JWT 令牌
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 解析 JWT 令牌中的 Claims
     * @param token JWT 令牌
     * @return Claims 对象
     * @throws JwtException 如果令牌无效
     */
    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(token).getBody();
    }


}
