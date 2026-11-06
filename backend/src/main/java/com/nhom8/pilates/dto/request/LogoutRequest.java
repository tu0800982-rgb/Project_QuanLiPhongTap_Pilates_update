package com.nhom8.pilates.dto.request;

public class LogoutRequest {
    private String refreshToken;
    public LogoutRequest() { }
    public String getRefreshToken(){return refreshToken;}
    public void setRefreshToken(String refreshToken){this.refreshToken=refreshToken;}
}
