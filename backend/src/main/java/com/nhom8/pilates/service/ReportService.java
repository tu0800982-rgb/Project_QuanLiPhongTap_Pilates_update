package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.ClassSchedule;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.List;

@Service
public class ReportService {
    private static final ZoneId ZONE=ZoneId.of("Asia/Ho_Chi_Minh");
    private final MemberRepository members; private final EmployeeRepository employees;
    private final PilatesPackageRepository packages; private final PilatesClassRepository classes;
    private final ClassScheduleRepository schedules; private final ClassRegistrationRepository registrations;
    private final AttendanceRepository attendance; private final PaymentRepository payments;
    public ReportService(MemberRepository members,EmployeeRepository employees,PilatesPackageRepository packages,
            PilatesClassRepository classes,ClassScheduleRepository schedules,ClassRegistrationRepository registrations,
            AttendanceRepository attendance,PaymentRepository payments) {
        this.members=members;this.employees=employees;this.packages=packages;this.classes=classes;
        this.schedules=schedules;this.registrations=registrations;this.attendance=attendance;this.payments=payments;
    }

    @Transactional(readOnly=true)
    public AdminReportResponse overview(LocalDate from,LocalDate to) {
        Range r=range(from,to); long active=members.countByStatus(RecordStatus.ACTIVE);
        long inactive=members.countByStatus(RecordStatus.INACTIVE);
        List<ClassSchedule> periodSchedules=schedules.findForReport(r.start,r.endExclusive);
        long occupied=0,capacity=0;
        for(ClassSchedule schedule:periodSchedules){
            occupied+=registrations.countByScheduleIdAndStatus(schedule.getId(),RegistrationStatus.REGISTERED);
            capacity+=schedule.getCapacity();
        }
        double fill=capacity==0?0:BigDecimal.valueOf(occupied*100.0/capacity).setScale(2,RoundingMode.HALF_UP).doubleValue();
        return new AdminReportResponse(r.from,r.to,active+inactive,active,inactive,
                employees.countByStatus(RecordStatus.ACTIVE),packages.countByStatus(RecordStatus.ACTIVE),
                classes.countByStatus(RecordStatus.ACTIVE),periodSchedules.size(),
                registrations.countByRegisteredAtGreaterThanEqualAndRegisteredAtLessThan(r.start,r.endExclusive),
                attendance.countByMarkedAtGreaterThanEqualAndMarkedAtLessThan(r.start,r.endExclusive),
                payments.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(r.start,r.endExclusive),
                payments.sumAmountByStatusAndPaidAt(PaymentStatus.PAID,r.start,r.endExclusive),fill);
    }

    @Transactional(readOnly=true)
    public StaffDashboardResponse staffDashboard(){
        LocalDate today=LocalDate.now(ZONE);Range r=range(today,today);
        return new StaffDashboardResponse(today,members.countByStatus(RecordStatus.ACTIVE),
                schedules.findForReport(r.start,r.endExclusive).size(),
                registrations.countByRegisteredAtGreaterThanEqualAndRegisteredAtLessThan(r.start,r.endExclusive),
                attendance.countByMarkedAtGreaterThanEqualAndMarkedAtLessThan(r.start,r.endExclusive),
                payments.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(r.start,r.endExclusive));
    }

    private Range range(LocalDate from,LocalDate to){
        LocalDate start=from==null?LocalDate.now(ZONE).withDayOfMonth(1):from;
        LocalDate end=to==null?LocalDate.now(ZONE):to;
        if(end.isBefore(start))throw new BusinessException("INVALID_DATE_RANGE","Ngày kết thúc phải từ ngày bắt đầu trở đi",HttpStatus.BAD_REQUEST);
        if(start.plusYears(2).isBefore(end))throw new BusinessException("DATE_RANGE_TOO_LARGE","Khoảng báo cáo tối đa là 2 năm",HttpStatus.BAD_REQUEST);
        return new Range(start,end,start.atStartOfDay(ZONE).toInstant(),end.plusDays(1).atStartOfDay(ZONE).toInstant());
    }
    private record Range(LocalDate from,LocalDate to,Instant start,Instant endExclusive){}
}
