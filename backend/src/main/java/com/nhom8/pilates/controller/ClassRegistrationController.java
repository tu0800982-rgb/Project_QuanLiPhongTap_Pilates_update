package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.ClassRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/class-registrations")
public class ClassRegistrationController {
    private final ClassRegistrationService service;
    public ClassRegistrationController(ClassRegistrationService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<ClassRegistrationResponse> register(@Valid @RequestBody ClassRegistrationRequest request){
        return ApiResponse.success("Đăng ký lớp thành công",service.register(request));
    }
    @PatchMapping("/{id}/cancel") @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<ClassRegistrationResponse> cancel(@PathVariable Long id,@Valid @RequestBody CancelRegistrationRequest request){
        return ApiResponse.success("Hủy đăng ký lớp thành công",service.cancel(id,request));
    }
    @GetMapping("/me") @PreAuthorize("hasRole('MEMBER')")
    public ApiResponse<List<ClassRegistrationResponse>> mine(){
        return ApiResponse.success("Lấy lịch đăng ký thành công",service.mine());
    }
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<ClassRegistrationResponse>> all(){return ApiResponse.success("Lấy danh sách đăng ký lớp thành công",service.all());}
}
