package com.nhom8.pilates;

import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import com.nhom8.pilates.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.lang.reflect.Field;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
    @Test
    void systemAdminCannotBeDeactivated() throws Exception {
        EmployeeRepository employees=mock(EmployeeRepository.class);
        EmployeeService service=new EmployeeService(employees,mock(UserRepository.class),mock(RoleRepository.class),mock(PasswordEncoder.class));
        Employee admin=new Employee();setId(admin,1L);admin.setEmployeeType(EmployeeType.ADMIN);admin.setStatus(RecordStatus.ACTIVE);
        when(employees.findById(1L)).thenReturn(Optional.of(admin));
        BusinessException error=assertThrows(BusinessException.class,()->service.status(1L,RecordStatus.INACTIVE));
        assertEquals("ADMIN_IMMUTABLE",error.getCode());verify(employees,never()).save(any());
    }
    private void setId(Object target,Long id)throws Exception{Field f=target.getClass().getDeclaredField("id");f.setAccessible(true);f.set(target,id);}
}
