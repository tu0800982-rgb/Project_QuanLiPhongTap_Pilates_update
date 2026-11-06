package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.AttendanceStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "attendance", uniqueConstraints =
        @UniqueConstraint(name = "uk_attendance_schedule_member", columnNames = {"schedule_id", "member_id"}))
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule schedule;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false)
    private ClassRegistration registration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceStatus status;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marked_by", nullable = false)
    private User markedBy;
    @Column(name = "marked_at", nullable = false)
    private Instant markedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Version private long version;

    public Attendance() { }
    public Long getId() { return id; }
    public ClassSchedule getSchedule() { return schedule; }
    public void setSchedule(ClassSchedule schedule) { this.schedule = schedule; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public ClassRegistration getRegistration() { return registration; }
    public void setRegistration(ClassRegistration registration) { this.registration = registration; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public User getMarkedBy() { return markedBy; }
    public void setMarkedBy(User markedBy) { this.markedBy = markedBy; }
    public Instant getMarkedAt() { return markedAt; }
    public void setMarkedAt(Instant markedAt) { this.markedAt = markedAt; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public long getVersion() { return version; }
}
