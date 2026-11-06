package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.ScheduleStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "class_schedules")
public class ClassSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private PilatesClass pilatesClass;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Employee trainer;
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;
    @Column(nullable = false)
    private int capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleStatus status = ScheduleStatus.OPEN;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
    @Version private long version;

    public ClassSchedule() { }
    public Long getId() { return id; }
    public PilatesClass getPilatesClass() { return pilatesClass; }
    public void setPilatesClass(PilatesClass pilatesClass) { this.pilatesClass = pilatesClass; }
    public Employee getTrainer() { return trainer; }
    public void setTrainer(Employee trainer) { this.trainer = trainer; }
    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
