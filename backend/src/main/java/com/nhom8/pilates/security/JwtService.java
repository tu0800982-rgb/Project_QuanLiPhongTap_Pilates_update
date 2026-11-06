package com.nhom8.pilates.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration accessTokenLifetime;
    private final Duration refreshTokenLifetime;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes,
                      @Value("${app.jwt.refresh-token-days}") long refreshTokenDays) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenLifetime = Duration.ofMinutes(accessTokenMinutes);
        this.refreshTokenLifetime = Duration.ofDays(refreshTokenDays);
    }

    public String createAccessToken(StudioUserDetails principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(principal.getUsername())
                .claim("role", principal.getUser().getRole().getCode().name())
                .claim("uid", principal.getUser().getId())
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenLifetime)))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(StudioUserDetails principal) {
        Instant now=Instant.now();
        return Jwts.builder().id(UUID.randomUUID().toString()).subject(principal.getUsername())
                .claim("uid",principal.getUser().getId()).claim("type","refresh")
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(refreshTokenLifetime)))
                .signWith(key).compact();
    }

    public String extractUsername(String token) { return parse(token).getSubject(); }
    public String extractTokenId(String token) { return parse(token).getId(); }
    public Instant extractExpiration(String token) { return parse(token).getExpiration().toInstant(); }
    public boolean isRefreshToken(String token) { return "refresh".equals(parse(token).get("type",String.class)); }
    public boolean isValid(String token, StudioUserDetails principal) {
        Claims claims = parse(token);
        return claims.getSubject().equalsIgnoreCase(principal.getUsername())
                && claims.getExpiration().after(new Date());
    }
    public long getExpiresInSeconds() { return accessTokenLifetime.toSeconds(); }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
