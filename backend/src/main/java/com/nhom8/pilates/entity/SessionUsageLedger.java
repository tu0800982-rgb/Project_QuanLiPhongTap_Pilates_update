package com.nhom8.pilates.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "session_usage_ledger")
public class SessionUsageLedger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_package_id", nullable = false)
    private MemberPackage memberPackage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private ClassRegistration registration;
    @Column(nullable = false) private int delta;
    @Column(nullable = false, length = 100) private String reason;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    public SessionUsageLedger() { }
    public Long getId() { return id; }
    public MemberPackage getMemberPackage() { return memberPackage; }
    public void setMemberPackage(MemberPackage memberPackage) { this.memberPackage = memberPackage; }
    public Attendance getAttendance() { return attendance; }
    public void setAttendance(Attendance attendance) { this.attendance = attendance; }
    public ClassRegistration getRegistration() { return registration; }
    public void setRegistration(ClassRegistration registration) { this.registration = registration; }
    public int getDelta() { return delta; }
    public void setDelta(int delta) { this.delta = delta; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
}
