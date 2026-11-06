package com.nhom8.pilates.dto.request;

import com.nhom8.pilates.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePaymentStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private PaymentStatus status;
    @Size(max = 500, message = "Lý do không được quá 500 ký tự")
    private String reason;

    public UpdatePaymentStatusRequest() { }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
