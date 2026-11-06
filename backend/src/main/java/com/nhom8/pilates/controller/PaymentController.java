package com.nhom8.pilates.controller;

import com.nhom8.pilates.dto.request.UpdatePaymentStatusRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<PaymentResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return ApiResponse.success("Cập nhật trạng thái giao dịch thành công", service.updateStatus(id, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ApiResponse<List<PaymentResponse>> mine() {
        return ApiResponse.success("Lấy lịch sử thanh toán thành công", service.myPayments());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Page<PaymentResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success("Lấy danh sách giao dịch thành công", service.list(page, size));
    }
}
