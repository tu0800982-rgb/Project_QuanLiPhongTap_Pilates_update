package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class ReportController {
    private final ReportService service;
    public ReportController(ReportService service){this.service=service;}
    @GetMapping("/reports/overview") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminReportResponse> overview(
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){
        return ApiResponse.success("Lấy báo cáo tổng quan thành công",service.overview(from,to));
    }
    @GetMapping("/dashboard/staff") @PreAuthorize("hasRole('STAFF')")
    public ApiResponse<StaffDashboardResponse> staff(){
        return ApiResponse.success("Lấy dashboard nhân viên thành công",service.staffDashboard());
    }
}
