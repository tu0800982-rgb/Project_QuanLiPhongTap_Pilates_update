package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.AuditLog;
import java.time.Instant;

public class AuditLogResponse {
    private final Long id; private final Long actorId; private final String actorEmail;
    private final String action; private final String entityType; private final String entityId;
    private final String beforeJson; private final String afterJson; private final Instant createdAt;
    public AuditLogResponse(AuditLog log) {
        id=log.getId();actorId=log.getActor()==null?null:log.getActor().getId();
        actorEmail=log.getActor()==null?null:log.getActor().getEmail(); action=log.getAction();
        entityType=log.getEntityType();entityId=log.getEntityId();beforeJson=log.getBeforeJson();
        afterJson=log.getAfterJson();createdAt=log.getCreatedAt();
    }
    public Long getId(){return id;} public Long getActorId(){return actorId;} public String getActorEmail(){return actorEmail;}
    public String getAction(){return action;} public String getEntityType(){return entityType;}
    public String getEntityId(){return entityId;} public String getBeforeJson(){return beforeJson;}
    public String getAfterJson(){return afterJson;} public Instant getCreatedAt(){return createdAt;}
}
