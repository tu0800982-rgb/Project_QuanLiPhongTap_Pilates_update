package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.LoginRequest;
import com.nhom8.pilates.dto.request.RefreshRequest;
import com.nhom8.pilates.dto.request.LogoutRequest;
import com.nhom8.pilates.dto.response.ApiResponse;
import com.nhom8.pilates.dto.response.LoginResponse;
import com.nhom8.pilates.dto.response.UserSummaryResponse;
import com.nhom8.pilates.security.StudioUserDetails;
import com.nhom8.pilates.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Đăng nhập thành công", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.success("Làm mới phiên đăng nhập thành công",authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization,
                                    @RequestBody(required=false) LogoutRequest request) {
        String accessToken=authorization.substring(7);
        authService.logout(accessToken,request==null?null:request.getRefreshToken());
        return ApiResponse.success("Đăng xuất thành công",null);
    }

    @GetMapping("/me")
    public ApiResponse<UserSummaryResponse> me(@AuthenticationPrincipal StudioUserDetails principal) {
        return ApiResponse.success("Lấy thông tin tài khoản thành công",
                new UserSummaryResponse(principal.getUser()));
    }
}
