package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.SaveAttendanceRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service){this.service=service;}

    @GetMapping("/schedules/{id}/attendance-roster")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TRAINER')")
    public ApiResponse<List<AttendanceRosterItemResponse>> roster(@PathVariable Long id){
        return ApiResponse.success("Lấy danh sách điểm danh thành công",service.roster(id));
    }

    @PutMapping("/schedules/{id}/attendance")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TRAINER')")
    public ApiResponse<List<AttendanceResponse>> save(@PathVariable Long id,
                                                      @Valid @RequestBody SaveAttendanceRequest request){
        return ApiResponse.success("Lưu điểm danh thành công",service.save(id,request));
    }

    @GetMapping("/attendance/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ApiResponse<List<AttendanceResponse>> mine(){
        return ApiResponse.success("Lấy lịch sử điểm danh thành công",service.mine());
    }
}
