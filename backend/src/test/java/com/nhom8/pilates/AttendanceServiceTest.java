package com.nhom8.pilates;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.repository.*;
import com.nhom8.pilates.service.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {
    @Test
    void savingSameAttendanceTwiceMustNotDeductAnotherSession() throws Exception {
        AttendanceRepository attendanceRepository=mock(AttendanceRepository.class);
        ClassScheduleRepository scheduleRepository=mock(ClassScheduleRepository.class);
        ClassRegistrationRepository registrationRepository=mock(ClassRegistrationRepository.class);
        MemberPackageRepository memberPackageRepository=mock(MemberPackageRepository.class);
        MemberRepository memberRepository=mock(MemberRepository.class);
        EmployeeRepository employeeRepository=mock(EmployeeRepository.class);
        SessionUsageLedgerRepository ledgerRepository=mock(SessionUsageLedgerRepository.class);
        CurrentUserService currentUserService=mock(CurrentUserService.class);
        AttendanceService service=new AttendanceService(attendanceRepository,scheduleRepository,
                registrationRepository,memberPackageRepository,memberRepository,employeeRepository,
                ledgerRepository,currentUserService,1,1,0,mock(AuditService.class));

        User staff=new User(); staff.setId(1L); staff.setRole(new Role(RoleCode.STAFF,"Nhân viên"));
        when(currentUserService.requireUser()).thenReturn(staff);
        Member member=new Member(); setId(member,2L); member.setFullName("Hội viên test");
        PilatesClass pilatesClass=new PilatesClass(); setId(pilatesClass,3L); pilatesClass.setName("Reformer test");
        ClassSchedule schedule=new ClassSchedule(); setId(schedule,4L); schedule.setPilatesClass(pilatesClass);
        schedule.setStartsAt(Instant.now().minusSeconds(600));
        when(scheduleRepository.findDetailedById(4L)).thenReturn(Optional.of(schedule));
        MemberPackage memberPackage=new MemberPackage(); setId(memberPackage,5L);
        memberPackage.setRemainingSessions(5); memberPackage.setTotalSessions(12);
        ClassRegistration registration=new ClassRegistration(); setId(registration,6L);
        registration.setMember(member); registration.setMemberPackage(memberPackage); registration.setSchedule(schedule);
        when(registrationRepository.findAttendanceRoster(4L)).thenReturn(List.of(registration));
        Attendance attendance=new Attendance(); setId(attendance,7L); attendance.setMember(member);
        attendance.setSchedule(schedule); attendance.setRegistration(registration); attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setMarkedAt(Instant.now().minusSeconds(300));
        when(attendanceRepository.findForUpdate(4L,2L)).thenReturn(Optional.of(attendance));
        when(memberPackageRepository.findEligibleByIdForUpdate(5L)).thenReturn(Optional.of(memberPackage));
        when(ledgerRepository.sumDeltaByAttendanceId(7L)).thenReturn(-1);
        when(attendanceRepository.save(attendance)).thenReturn(attendance);
        AttendanceItemRequest item=new AttendanceItemRequest(); item.setMemberId(2L); item.setStatus(AttendanceStatus.PRESENT);
        SaveAttendanceRequest request=new SaveAttendanceRequest(); request.setItems(List.of(item));

        service.save(4L,request);

        assertEquals(5,memberPackage.getRemainingSessions());
        verify(ledgerRepository,never()).save(any());
        verify(memberPackageRepository,never()).save(any());
    }

    private void setId(Object target,Long id) throws Exception {
        Field field=target.getClass().getDeclaredField("id"); field.setAccessible(true); field.set(target,id);
    }
}
