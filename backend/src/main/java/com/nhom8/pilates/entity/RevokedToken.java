package com.nhom8.pilates.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_id", nullable = false, unique = true, length = 64)
    private String tokenId;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at", nullable = false, insertable = false, updatable = false)
    private Instant revokedAt;
    public RevokedToken() { }
    public Long getId(){return id;} public String getTokenId(){return tokenId;}
    public void setTokenId(String tokenId){this.tokenId=tokenId;} public Instant getExpiresAt(){return expiresAt;}
    public void setExpiresAt(Instant expiresAt){this.expiresAt=expiresAt;} public Instant getRevokedAt(){return revokedAt;}
}
