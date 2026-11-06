package com.nhom8.pilates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.repository.AuditLogRepository;
import com.nhom8.pilates.service.AuditService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditServiceTest {
    @Test
    void auditMustSerializeBeforeAndAfterData() {
        AuditLogRepository repository=mock(AuditLogRepository.class);
        AuditService service=new AuditService(repository,new ObjectMapper());
        User actor=new User();actor.setId(10L);actor.setEmail("staff@pilates.com");
        service.record(actor,"PAYMENT_STATUS_CHANGED","Payment",25L,
                Map.of("status","PENDING"),Map.of("status","PAID"));
        ArgumentCaptor<AuditLog> captor=ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());
        assertEquals("PAYMENT_STATUS_CHANGED",captor.getValue().getAction());
        assertTrue(captor.getValue().getBeforeJson().contains("PENDING"));
        assertTrue(captor.getValue().getAfterJson().contains("PAID"));
    }
}
