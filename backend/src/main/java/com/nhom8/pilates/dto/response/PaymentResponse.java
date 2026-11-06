package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public class PaymentResponse {
    private final Long id;
    private final String transactionCode;
    private final Long memberId;
    private final String memberName;
    private final String packageName;
    private final BigDecimal amount;
    private final String method;
    private final String status;
    private final Instant paidAt;
    private final Instant refundedAt;
    private final Instant createdAt;

    public PaymentResponse(Payment source) {
        id = source.getId(); transactionCode = source.getTransactionCode();
        memberId = source.getMember().getId(); memberName = source.getMember().getFullName();
        packageName = source.getMemberPackage().getPilatesPackage().getName(); amount = source.getAmount();
        method = source.getMethod().name(); status = source.getStatus().name(); paidAt = source.getPaidAt();
        refundedAt = source.getRefundedAt(); createdAt = source.getCreatedAt();
    }
    public Long getId() { return id; }
    public String getTransactionCode() { return transactionCode; }
    public Long getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getPackageName() { return packageName; }
    public BigDecimal getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public Instant getPaidAt() { return paidAt; }
    public Instant getRefundedAt() { return refundedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
