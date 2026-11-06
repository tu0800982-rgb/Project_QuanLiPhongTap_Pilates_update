package com.nhom8.pilates.dto.request;

import com.nhom8.pilates.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class RegisterPackageRequest {
    private Long memberId;
    @NotNull(message = "Gói tập không được để trống")
    private Long packageId;
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;

    public RegisterPackageRequest() { }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
