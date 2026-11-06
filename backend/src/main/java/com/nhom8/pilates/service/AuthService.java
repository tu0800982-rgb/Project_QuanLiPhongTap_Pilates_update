package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.LoginRequest;
import com.nhom8.pilates.dto.request.RefreshRequest;
import com.nhom8.pilates.dto.response.LoginResponse;
import com.nhom8.pilates.dto.response.UserSummaryResponse;
import com.nhom8.pilates.entity.User;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.UserRepository;
import com.nhom8.pilates.repository.RefreshTokenRepository;
import com.nhom8.pilates.repository.RevokedTokenRepository;
import com.nhom8.pilates.entity.RefreshToken;
import com.nhom8.pilates.entity.RevokedToken;
import com.nhom8.pilates.security.JwtService;
import com.nhom8.pilates.security.StudioUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       RevokedTokenRepository revokedTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository=refreshTokenRepository;
        this.revokedTokenRepository=revokedTokenRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(),
                            request.getPassword()));
            StudioUserDetails principal = (StudioUserDetails) authentication.getPrincipal();
            User user = principal.getUser();
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            return issueTokens(principal);
        } catch (BadCredentialsException exception) {
            throw new BusinessException("INVALID_CREDENTIALS", "Email hoặc mật khẩu không chính xác",
                    HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException exception) {
            throw new BusinessException("ACCOUNT_UNAVAILABLE", "Tài khoản không thể đăng nhập",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        try {
            String raw=request.getRefreshToken();
            if (!jwtService.isRefreshToken(raw)) throw new JwtException("Wrong token type");
            String hash=hash(raw);
            RefreshToken stored=refreshTokenRepository.findByHashForUpdate(hash)
                    .orElseThrow(() -> new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED));
            if (stored.getRevokedAt()!=null || stored.getExpiresAt().isBefore(Instant.now()))
                throw new BusinessException("REFRESH_TOKEN_EXPIRED", "Refresh token đã hết hạn hoặc bị thu hồi", HttpStatus.UNAUTHORIZED);
            if (!stored.getUser().getEmail().equalsIgnoreCase(jwtService.extractUsername(raw)))
                throw new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED);
            StudioUserDetails principal=new StudioUserDetails(stored.getUser());
            String access=jwtService.createAccessToken(principal);
            String replacement=jwtService.createRefreshToken(principal);
            RefreshToken newStored=storeRefreshToken(replacement,stored.getUser());
            stored.setRevokedAt(Instant.now()); stored.setReplacedByHash(newStored.getTokenHash());
            refreshTokenRepository.save(stored);
            return new LoginResponse(access,replacement,jwtService.getExpiresInSeconds(),new UserSummaryResponse(stored.getUser()));
        } catch (BusinessException exception) {
            throw exception;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void logout(String accessToken,String refreshToken) {
        try {
            String tokenId=jwtService.extractTokenId(accessToken);
            if (!revokedTokenRepository.existsByTokenId(tokenId)) {
                RevokedToken revoked=new RevokedToken(); revoked.setTokenId(tokenId);
                revoked.setExpiresAt(jwtService.extractExpiration(accessToken)); revokedTokenRepository.save(revoked);
            }
            if (refreshToken!=null && !refreshToken.isBlank()) {
                refreshTokenRepository.findByHashForUpdate(hash(refreshToken)).ifPresent(stored -> {
                    if (stored.getRevokedAt()==null) { stored.setRevokedAt(Instant.now()); refreshTokenRepository.save(stored); }
                });
            }
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException("INVALID_TOKEN", "Token đăng xuất không hợp lệ", HttpStatus.BAD_REQUEST);
        }
    }

    private LoginResponse issueTokens(StudioUserDetails principal) {
        String access=jwtService.createAccessToken(principal);
        String refresh=jwtService.createRefreshToken(principal);
        storeRefreshToken(refresh,principal.getUser());
        return new LoginResponse(access,refresh,jwtService.getExpiresInSeconds(),new UserSummaryResponse(principal.getUser()));
    }

    private RefreshToken storeRefreshToken(String raw,User user) {
        RefreshToken stored=new RefreshToken(); stored.setTokenHash(hash(raw)); stored.setUser(user);
        stored.setExpiresAt(jwtService.extractExpiration(raw)); return refreshTokenRepository.save(stored);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 không khả dụng",exception);
        }
    }
}
