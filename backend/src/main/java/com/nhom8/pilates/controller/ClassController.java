package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.ClassManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClassController {
    private final ClassManagementService service;
    public ClassController(ClassManagementService service){this.service=service;}
    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<List<ClassResponse>> classes(){return ApiResponse.success("Lấy danh sách lớp thành công",service.listClasses());}
    @PostMapping("/classes") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ClassResponse> createClass(@Valid @RequestBody CreateClassRequest request){
        return ApiResponse.success("Tạo lớp thành công",service.createClass(request));
    }
    @PutMapping("/classes/{id}") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ClassResponse> updateClass(@PathVariable Long id,@Valid @RequestBody CreateClassRequest request){return ApiResponse.success("Cập nhật lớp thành công",service.updateClass(id,request));}
    @PatchMapping("/classes/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ClassResponse> updateClassStatus(@PathVariable Long id,@Valid @RequestBody UpdateRecordStatusRequest request){return ApiResponse.success("Cập nhật trạng thái lớp thành công",service.updateClassStatus(id,request.getStatus()));}
    @GetMapping("/schedules")
    public ApiResponse<List<ScheduleResponse>> schedules(){return ApiResponse.success("Lấy lịch học thành công",service.upcoming());}
    @PostMapping("/schedules") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ScheduleResponse> createSchedule(@Valid @RequestBody CreateScheduleRequest request){
        return ApiResponse.success("Tạo lịch học thành công",service.createSchedule(request));
    }
    @PutMapping("/schedules/{id}") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ScheduleResponse> updateSchedule(@PathVariable Long id,@Valid @RequestBody CreateScheduleRequest request){return ApiResponse.success("Cập nhật lịch học thành công",service.updateSchedule(id,request));}
    @PatchMapping("/schedules/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ScheduleResponse> updateScheduleStatus(@PathVariable Long id,@Valid @RequestBody UpdateScheduleStatusRequest request){return ApiResponse.success("Cập nhật trạng thái lịch thành công",service.updateScheduleStatus(id,request.getStatus()));}
}
