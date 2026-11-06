package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.MemberPackageStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "member_packages")
public class MemberPackage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private PilatesPackage pilatesPackage;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "total_sessions", nullable = false)
    private int totalSessions;
    @Column(name = "remaining_sessions", nullable = false)
    private int remainingSessions;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberPackageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renewed_from_id")
    private MemberPackage renewedFrom;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
    @Version private long version;

    public MemberPackage() { }
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public PilatesPackage getPilatesPackage() { return pilatesPackage; }
    public void setPilatesPackage(PilatesPackage pilatesPackage) { this.pilatesPackage = pilatesPackage; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    public int getRemainingSessions() { return remainingSessions; }
    public void setRemainingSessions(int remainingSessions) { this.remainingSessions = remainingSessions; }
    public MemberPackageStatus getStatus() { return status; }
    public void setStatus(MemberPackageStatus status) { this.status = status; }
    public MemberPackage getRenewedFrom() { return renewedFrom; }
    public void setRenewedFrom(MemberPackage renewedFrom) { this.renewedFrom = renewedFrom; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
