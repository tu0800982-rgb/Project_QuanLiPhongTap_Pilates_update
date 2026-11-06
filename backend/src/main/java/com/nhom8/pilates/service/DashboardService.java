package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

@Service
public class DashboardService {
    private static final ZoneId ZONE=ZoneId.of("Asia/Ho_Chi_Minh");
    private final ReportService reportService; private final PaymentRepository payments;
    private final ClassManagementService classManagementService; private final CurrentUserService currentUserService;
    private final MemberRepository members; private final MemberPackageRepository memberPackages;
    private final ClassRegistrationRepository registrations; private final AttendanceRepository attendance;
    public DashboardService(ReportService reportService,PaymentRepository payments,
            ClassManagementService classManagementService,CurrentUserService currentUserService,
            MemberRepository members,MemberPackageRepository memberPackages,
            ClassRegistrationRepository registrations,AttendanceRepository attendance) {
        this.reportService=reportService;this.payments=payments;this.classManagementService=classManagementService;
        this.currentUserService=currentUserService;this.members=members;this.memberPackages=memberPackages;
        this.registrations=registrations;this.attendance=attendance;
    }

    @Transactional(readOnly=true)
    public AdminDashboardResponse admin(){
        LocalDate today=LocalDate.now(ZONE);
        List<PaymentResponse> recent=payments.findTop5ByOrderByCreatedAtDesc().stream().map(PaymentResponse::new).toList();
        return new AdminDashboardResponse(reportService.overview(today.withDayOfMonth(1),today),recent);
    }

    @Transactional(readOnly=true)
    public TrainerDashboardResponse trainer(){
        LocalDate today=LocalDate.now(ZONE);List<ScheduleResponse> upcoming=classManagementService.upcoming();
        long todayCount=upcoming.stream().filter(s -> s.getStartsAt().atZone(ZONE).toLocalDate().equals(today)).count();
        return new TrainerDashboardResponse(today,todayCount,upcoming);
    }

    @Transactional(readOnly=true)
    public MemberDashboardResponse member(){
        User actor=currentUserService.requireUser();
        Member member=members.findByUserId(actor.getId()).orElseThrow(() ->
                new BusinessException("MEMBER_PROFILE_NOT_FOUND","Không có hồ sơ hội viên",HttpStatus.NOT_FOUND));
        Instant now=Instant.now();
        List<MemberPackageResponse> active=memberPackages
                .findByMemberIdAndStatusOrderByEndDateDesc(member.getId(),MemberPackageStatus.ACTIVE)
                .stream().map(MemberPackageResponse::new).toList();
        List<ClassRegistrationResponse> upcoming=registrations.findByMemberIdOrderByScheduleStartsAtDesc(member.getId())
                .stream().filter(r -> r.getStatus()==RegistrationStatus.REGISTERED && r.getSchedule().getStartsAt().isAfter(now))
                .sorted((a,b) -> a.getSchedule().getStartsAt().compareTo(b.getSchedule().getStartsAt()))
                .limit(5).map(ClassRegistrationResponse::new).toList();
        List<AttendanceResponse> recentAttendance=attendance.findByMemberIdOrderByScheduleStartsAtDesc(member.getId())
                .stream().limit(5).map(AttendanceResponse::new).toList();
        List<PaymentResponse> recentPayments=payments.findByMemberIdOrderByCreatedAtDesc(member.getId())
                .stream().limit(5).map(PaymentResponse::new).toList();
        return new MemberDashboardResponse(new MemberResponse(member),active,upcoming,recentAttendance,recentPayments);
    }
}
