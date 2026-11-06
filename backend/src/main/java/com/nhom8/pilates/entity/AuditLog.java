package com.nhom8.pilates.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;
    @Column(nullable = false, length = 100)
    private String action;
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;
    @Column(name = "entity_id", length = 100)
    private String entityId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_json", columnDefinition = "json")
    private String beforeJson;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_json", columnDefinition = "json")
    private String afterJson;
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    public AuditLog() { }
    public Long getId(){return id;} public User getActor(){return actor;} public void setActor(User actor){this.actor=actor;}
    public String getAction(){return action;} public void setAction(String action){this.action=action;}
    public String getEntityType(){return entityType;} public void setEntityType(String entityType){this.entityType=entityType;}
    public String getEntityId(){return entityId;} public void setEntityId(String entityId){this.entityId=entityId;}
    public String getBeforeJson(){return beforeJson;} public void setBeforeJson(String beforeJson){this.beforeJson=beforeJson;}
    public String getAfterJson(){return afterJson;} public void setAfterJson(String afterJson){this.afterJson=afterJson;}
    public String getIpAddress(){return ipAddress;} public void setIpAddress(String ipAddress){this.ipAddress=ipAddress;}
    public Instant getCreatedAt(){return createdAt;}
}
