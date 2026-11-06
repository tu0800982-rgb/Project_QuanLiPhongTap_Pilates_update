package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.RegisterPackageRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.MemberPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/member-packages")
public class MemberPackageController {
    private final MemberPackageService service;
    public MemberPackageController(MemberPackageService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<PackageRegistrationResponse> register(@Valid @RequestBody RegisterPackageRequest request) {
        return ApiResponse.success("Đăng ký gói thành công, giao dịch đang chờ xác nhận", service.register(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ApiResponse<List<MemberPackageResponse>> mine() {
        return ApiResponse.success("Lấy gói tập của tôi thành công", service.myPackages());
    }
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MemberPackageResponse>> byMember(@PathVariable Long memberId){return ApiResponse.success("Lấy gói tập hội viên thành công",service.packagesOf(memberId));}
}
