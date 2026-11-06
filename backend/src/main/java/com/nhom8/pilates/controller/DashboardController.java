package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service){this.service=service;}
    @GetMapping("/admin") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminDashboardResponse> admin(){return ApiResponse.success("Lấy dashboard quản trị thành công",service.admin());}
    @GetMapping("/trainer") @PreAuthorize("hasRole('TRAINER')")
    public ApiResponse<TrainerDashboardResponse> trainer(){return ApiResponse.success("Lấy trang chủ huấn luyện viên thành công",service.trainer());}
    @GetMapping("/member") @PreAuthorize("hasRole('MEMBER')")
    public ApiResponse<MemberDashboardResponse> member(){return ApiResponse.success("Lấy trang chủ hội viên thành công",service.member());}
}
