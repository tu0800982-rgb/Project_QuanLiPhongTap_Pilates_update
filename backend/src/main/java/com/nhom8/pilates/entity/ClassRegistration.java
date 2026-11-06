package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.RegistrationStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "class_registrations")
public class ClassRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule schedule;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_package_id", nullable = false)
    private MemberPackage memberPackage;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registered_by", nullable = false)
    private User registeredBy;
    @Column(name = "registered_at", nullable = false, insertable = false, updatable = false)
    private Instant registeredAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private User cancelledBy;
    @Column(name = "cancelled_at") private Instant cancelledAt;
    @Column(name = "cancellation_reason", length = 500) private String cancellationReason;
    @Column(name = "late_cancellation", nullable = false) private boolean lateCancellation;
    @Version private long version;

    public ClassRegistration() { }
    public Long getId() { return id; }
    public ClassSchedule getSchedule() { return schedule; }
    public void setSchedule(ClassSchedule schedule) { this.schedule = schedule; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public MemberPackage getMemberPackage() { return memberPackage; }
    public void setMemberPackage(MemberPackage memberPackage) { this.memberPackage = memberPackage; }
    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }
    public User getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(User registeredBy) { this.registeredBy = registeredBy; }
    public Instant getRegisteredAt() { return registeredAt; }
    public User getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(User cancelledBy) { this.cancelledBy = cancelledBy; }
    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public boolean isLateCancellation() { return lateCancellation; }
    public void setLateCancellation(boolean lateCancellation) { this.lateCancellation = lateCancellation; }
    public long getVersion() { return version; }
}
