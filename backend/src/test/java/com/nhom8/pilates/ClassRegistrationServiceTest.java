package com.nhom8.pilates;

import com.nhom8.pilates.dto.request.ClassRegistrationRequest;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import com.nhom8.pilates.service.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassRegistrationServiceTest {
    @Test
    void fullClassMustRejectRegistrationBeforeSaving() throws Exception {
        ClassScheduleRepository scheduleRepository=mock(ClassScheduleRepository.class);
        ClassRegistrationRepository registrationRepository=mock(ClassRegistrationRepository.class);
        MemberPackageRepository memberPackageRepository=mock(MemberPackageRepository.class);
        MemberRepository memberRepository=mock(MemberRepository.class);
        SessionUsageLedgerRepository ledgerRepository=mock(SessionUsageLedgerRepository.class);
        CurrentUserService currentUserService=mock(CurrentUserService.class);
        ClassRegistrationService service=new ClassRegistrationService(scheduleRepository,registrationRepository,
                memberPackageRepository,memberRepository,ledgerRepository,currentUserService,12,mock(AuditService.class));

        Role role=new Role(RoleCode.STAFF,"Nhân viên");
        User staff=new User(); staff.setId(10L); staff.setRole(role);
        when(currentUserService.requireUser()).thenReturn(staff);
        Member member=new Member(); setId(member,20L);
        when(memberRepository.findById(20L)).thenReturn(Optional.of(member));
        ClassSchedule schedule=new ClassSchedule(); setId(schedule,30L); schedule.setCapacity(1);
        schedule.setStartsAt(Instant.now().plusSeconds(7200)); schedule.setStatus(ScheduleStatus.OPEN);
        when(scheduleRepository.findByIdForUpdate(30L)).thenReturn(Optional.of(schedule));
        when(registrationRepository.countByScheduleIdAndStatus(30L,RegistrationStatus.REGISTERED)).thenReturn(1L);
        ClassRegistrationRequest request=new ClassRegistrationRequest(); request.setScheduleId(30L);
        request.setMemberId(20L); request.setMemberPackageId(40L);

        BusinessException error=assertThrows(BusinessException.class,() -> service.register(request));
        assertEquals("CLASS_FULL",error.getCode());
        verify(registrationRepository,never()).save(any());
    }

    private void setId(Object target, Long id) throws Exception {
        Field field=target.getClass().getDeclaredField("id"); field.setAccessible(true); field.set(target,id);
    }
}
