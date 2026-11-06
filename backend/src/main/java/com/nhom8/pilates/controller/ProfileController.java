package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.UpdateProfileRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService service;public ProfileController(ProfileService service){this.service=service;}
    @GetMapping public ApiResponse<ProfileResponse> get(){return ApiResponse.success("Lấy hồ sơ thành công",service.get());}
    @PatchMapping public ApiResponse<ProfileResponse> update(@Valid @RequestBody UpdateProfileRequest request){return ApiResponse.success("Cập nhật hồ sơ thành công",service.update(request));}
}
