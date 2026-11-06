package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.*;import com.nhom8.pilates.dto.response.*;import com.nhom8.pilates.service.EmployeeService;
import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;import java.util.List;

@RestController @RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService service;public EmployeeController(EmployeeService service){this.service=service;}
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public ApiResponse<List<EmployeeResponse>> list(){return ApiResponse.success("Lấy danh sách nhân sự thành công",service.list());}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMIN')") public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest r){return ApiResponse.success("Tạo nhân sự thành công",service.create(r));}
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<EmployeeResponse> update(@PathVariable Long id,@Valid @RequestBody EmployeeRequest r){return ApiResponse.success("Cập nhật nhân sự thành công",service.update(id,r));}
    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<EmployeeResponse> status(@PathVariable Long id,@Valid @RequestBody UpdateRecordStatusRequest r){return ApiResponse.success("Cập nhật trạng thái nhân sự thành công",service.status(id,r.getStatus()));}
}
