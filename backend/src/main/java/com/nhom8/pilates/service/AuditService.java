package com.nhom8.pilates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
    private final AuditLogRepository repository; private final ObjectMapper objectMapper;
    public AuditService(AuditLogRepository repository,ObjectMapper objectMapper){
        this.repository=repository;this.objectMapper=objectMapper;
    }
    @Transactional
    public void record(User actor,String action,String entityType,Object entityId,Object before,Object after){
        AuditLog log=new AuditLog();log.setActor(actor);log.setAction(action);log.setEntityType(entityType);
        log.setEntityId(entityId==null?null:String.valueOf(entityId));log.setBeforeJson(json(before));log.setAfterJson(json(after));
        repository.save(log);
    }
    @Transactional(readOnly=true)
    public PageResponse<AuditLogResponse> list(int page,int size){
        int safeSize=Math.min(Math.max(size,1),100);
        return new PageResponse<>(repository.findAllByOrderByCreatedAtDesc(PageRequest.of(Math.max(page,0),safeSize))
                .map(AuditLogResponse::new));
    }
    private String json(Object value){
        if(value==null)return null;
        try{return objectMapper.writeValueAsString(value);}
        catch(JsonProcessingException exception){throw new IllegalStateException("Không thể tạo dữ liệu audit",exception);}
    }
}
