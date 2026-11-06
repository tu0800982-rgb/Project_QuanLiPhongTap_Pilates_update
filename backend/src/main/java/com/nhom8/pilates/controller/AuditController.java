package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.AuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    private final AuditService service;
    public AuditController(AuditService service){this.service=service;}
    @GetMapping
    public ApiResponse<PageResponse<AuditLogResponse>> list(@RequestParam(defaultValue="0") int page,
                                                            @RequestParam(defaultValue="20") int size){
        return ApiResponse.success("Lấy nhật ký thao tác thành công",service.list(page,size));
    }
}
