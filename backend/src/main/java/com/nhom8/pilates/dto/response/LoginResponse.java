package com.nhom8.pilates.dto.response;

public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    private final long expiresInSeconds;
    private final UserSummaryResponse user;

    public LoginResponse(String accessToken, String refreshToken, long expiresInSeconds, UserSummaryResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public UserSummaryResponse getUser() { return user; }
}
