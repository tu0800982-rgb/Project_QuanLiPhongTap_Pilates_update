package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CancelRegistrationRequest {
    @NotBlank(message = "Vui lòng nhập lý do hủy")
    @Size(max = 500, message = "Lý do không được quá 500 ký tự")
    private String reason;
    public CancelRegistrationRequest() { }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
