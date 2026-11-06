package com.nhom8.pilates.entity;

import com.nhom8.pilates.enums.RecordStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "classes")
public class PilatesClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "class_code", nullable = false, unique = true, length = 30)
    private String classCode;
    @Column(nullable = false, length = 150)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_type_id", nullable = false)
    private ClassType classType;
    @Column(nullable = false, length = 150)
    private String location;
    @Column(name = "default_capacity", nullable = false)
    private int defaultCapacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecordStatus status = RecordStatus.ACTIVE;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
    @Version private long version;

    public PilatesClass() { }
    public Long getId() { return id; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ClassType getClassType() { return classType; }
    public void setClassType(ClassType classType) { this.classType = classType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getDefaultCapacity() { return defaultCapacity; }
    public void setDefaultCapacity(int defaultCapacity) { this.defaultCapacity = defaultCapacity; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
