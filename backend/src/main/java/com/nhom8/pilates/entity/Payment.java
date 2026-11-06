package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.PaymentMethod;
import com.nhom8.pilates.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_code", nullable = false, unique = true, length = 40)
    private String transactionCode;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_package_id", nullable = false)
    private MemberPackage memberPackage;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;
    @Column(name = "paid_at") private Instant paidAt;
    @Column(name = "refunded_at") private Instant refundedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by") private User performedBy;
    @Column(name = "refund_reason", length = 500) private String refundReason;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
    @Version private long version;

    public Payment() { }
    public Long getId() { return id; }
    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public MemberPackage getMemberPackage() { return memberPackage; }
    public void setMemberPackage(MemberPackage memberPackage) { this.memberPackage = memberPackage; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
    public Instant getRefundedAt() { return refundedAt; }
    public void setRefundedAt(Instant refundedAt) { this.refundedAt = refundedAt; }
    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
