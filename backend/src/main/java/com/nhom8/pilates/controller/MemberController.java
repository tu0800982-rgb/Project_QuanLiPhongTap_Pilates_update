package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.CreateMemberRequest;
import com.nhom8.pilates.dto.request.UpdateMemberRequest;
import com.nhom8.pilates.dto.request.UpdateRecordStatusRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) { this.memberService = memberService; }

    @GetMapping
    public ApiResponse<PageResponse<MemberResponse>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success("Lấy danh sách hội viên thành công", memberService.search(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> find(@PathVariable Long id) {
        return ApiResponse.success("Lấy thông tin hội viên thành công", memberService.find(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> create(@Valid @RequestBody CreateMemberRequest request) {
        return ApiResponse.success("Thêm hội viên thành công", memberService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<MemberResponse> update(@PathVariable Long id,@Valid @RequestBody UpdateMemberRequest request){
        return ApiResponse.success("Cập nhật hội viên thành công",memberService.update(id,request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<MemberResponse> status(@PathVariable Long id,@Valid @RequestBody UpdateRecordStatusRequest request){
        return ApiResponse.success("Cập nhật trạng thái hội viên thành công",memberService.updateStatus(id,request.getStatus()));
    }
}
