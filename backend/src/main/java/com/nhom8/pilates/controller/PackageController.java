package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.response.ApiResponse;
import com.nhom8.pilates.dto.response.PackageResponse;
import com.nhom8.pilates.enums.RecordStatus;
import com.nhom8.pilates.repository.PilatesPackageRepository;
import com.nhom8.pilates.dto.request.PackageRequest;
import com.nhom8.pilates.dto.request.UpdateRecordStatusRequest;
import com.nhom8.pilates.dto.response.ClassTypeResponse;
import com.nhom8.pilates.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {
    private final PackageService service;
    public PackageController(PackageService service) { this.service=service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<List<PackageResponse>> list() {
        return ApiResponse.success("Lấy danh sách gói tập thành công", service.list());
    }
    @GetMapping("/class-types") @PreAuthorize("hasAnyRole('ADMIN','STAFF','MEMBER')")
    public ApiResponse<List<ClassTypeResponse>> types(){return ApiResponse.success("Lấy loại lớp thành công",service.types());}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<PackageResponse> create(@Valid @RequestBody PackageRequest request){return ApiResponse.success("Tạo gói tập thành công",service.create(request));}
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<PackageResponse> update(@PathVariable Long id,@Valid @RequestBody PackageRequest request){return ApiResponse.success("Cập nhật gói tập thành công",service.update(id,request));}
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<PackageResponse> status(@PathVariable Long id,@Valid @RequestBody UpdateRecordStatusRequest request){return ApiResponse.success("Cập nhật trạng thái gói thành công",service.status(id,request.getStatus()));}
}
