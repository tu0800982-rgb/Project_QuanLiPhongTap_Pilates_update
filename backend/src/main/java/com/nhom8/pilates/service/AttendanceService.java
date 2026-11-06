package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final ClassRegistrationRepository registrationRepository;
    private final MemberPackageRepository memberPackageRepository;
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final SessionUsageLedgerRepository ledgerRepository;
    private final CurrentUserService currentUserService;
    private final int presentCost;
    private final int absentCost;
    private final int excusedCost;
    private final AuditService auditService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             ClassScheduleRepository scheduleRepository,
                             ClassRegistrationRepository registrationRepository,
                             MemberPackageRepository memberPackageRepository,
                             MemberRepository memberRepository, EmployeeRepository employeeRepository,
                             SessionUsageLedgerRepository ledgerRepository, CurrentUserService currentUserService,
                             @Value("${app.business.present-session-cost}") int presentCost,
                             @Value("${app.business.absent-session-cost}") int absentCost,
                             @Value("${app.business.excused-session-cost}") int excusedCost,
                             AuditService auditService) {
        this.attendanceRepository=attendanceRepository; this.scheduleRepository=scheduleRepository;
        this.registrationRepository=registrationRepository; this.memberPackageRepository=memberPackageRepository;
        this.memberRepository=memberRepository; this.employeeRepository=employeeRepository;
        this.ledgerRepository=ledgerRepository; this.currentUserService=currentUserService;
        this.presentCost=presentCost; this.absentCost=absentCost; this.excusedCost=excusedCost;
        this.auditService=auditService;
    }

    @Transactional(readOnly=true)
    public List<AttendanceRosterItemResponse> roster(Long scheduleId) {
        User actor=currentUserService.requireUser();
        ClassSchedule schedule=requireAuthorizedSchedule(actor,scheduleId);
        List<Attendance> existing=attendanceRepository.findByScheduleId(schedule.getId());
        Map<Long,AttendanceStatus> statusByMember=existing.stream().collect(Collectors.toMap(
                a -> a.getMember().getId(),Attendance::getStatus));
        return registrationRepository.findAttendanceRoster(scheduleId).stream()
                .map(r -> new AttendanceRosterItemResponse(r,statusByMember.get(r.getMember().getId()))).toList();
    }

    @Transactional
    public List<AttendanceResponse> save(Long scheduleId, SaveAttendanceRequest request) {
        User actor=currentUserService.requireUser();
        ClassSchedule schedule=requireAuthorizedSchedule(actor,scheduleId);
        if (schedule.getStartsAt().isAfter(Instant.now()) && actor.getRole().getCode()==RoleCode.TRAINER)
            throw new BusinessException("ATTENDANCE_TOO_EARLY", "Chưa đến giờ bắt đầu lớp học", HttpStatus.CONFLICT);
        Set<Long> uniqueMembers=new HashSet<>();
        for (AttendanceItemRequest item:request.getItems()) {
            if (!uniqueMembers.add(item.getMemberId()))
                throw new BusinessException("DUPLICATE_ATTENDANCE_ITEM", "Danh sách có hội viên bị lặp", HttpStatus.BAD_REQUEST);
        }
        Map<Long,ClassRegistration> registrations=registrationRepository.findAttendanceRoster(scheduleId).stream()
                .collect(Collectors.toMap(r -> r.getMember().getId(),Function.identity()));
        List<AttendanceResponse> results=new ArrayList<>();
        for (AttendanceItemRequest item:request.getItems()) {
            ClassRegistration registration=registrations.get(item.getMemberId());
            if (registration==null)
                throw new BusinessException("INVALID_ATTENDANCE_MEMBER", "Hội viên chưa đăng ký hợp lệ cho lớp", HttpStatus.CONFLICT);
            results.add(new AttendanceResponse(saveOne(schedule,registration,item.getStatus(),actor)));
        }
        return results;
    }

    @Transactional(readOnly=true)
    public List<AttendanceResponse> mine() {
        User actor=currentUserService.requireUser();
        Member member=memberRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new BusinessException("MEMBER_PROFILE_NOT_FOUND", "Không có hồ sơ hội viên", HttpStatus.NOT_FOUND));
        return attendanceRepository.findByMemberIdOrderByScheduleStartsAtDesc(member.getId()).stream()
                .map(AttendanceResponse::new).toList();
    }

    private Attendance saveOne(ClassSchedule schedule, ClassRegistration registration,
                               AttendanceStatus targetStatus, User actor) {
        Optional<Attendance> existing=attendanceRepository.findForUpdate(schedule.getId(),registration.getMember().getId());
        AttendanceStatus previousStatus=existing.map(Attendance::getStatus).orElse(null);
        Attendance attendance;
        if (existing.isPresent()) {
            attendance=existing.get(); attendance.setStatus(targetStatus);
            attendance.setUpdatedBy(actor); attendance.setUpdatedAt(Instant.now());
        } else {
            attendance=new Attendance(); attendance.setSchedule(schedule); attendance.setMember(registration.getMember());
            attendance.setRegistration(registration); attendance.setStatus(targetStatus);
            attendance.setMarkedBy(actor); attendance.setMarkedAt(Instant.now());
            attendanceRepository.save(attendance);
        }
        Attendance saved=attendanceRepository.save(attendance);
        auditService.record(actor,existing.isPresent()?"ATTENDANCE_UPDATED":"ATTENDANCE_CREATED","Attendance",saved.getId(),
                previousStatus==null?null:Map.of("status",previousStatus.name()),Map.of("status",targetStatus.name()));
        return saved;
    }

    private ClassSchedule requireAuthorizedSchedule(User actor,Long scheduleId) {
        ClassSchedule schedule=scheduleRepository.findDetailedById(scheduleId)
                .orElseThrow(() -> new BusinessException("SCHEDULE_NOT_FOUND", "Không tìm thấy lịch học", HttpStatus.NOT_FOUND));
        if (actor.getRole().getCode()==RoleCode.TRAINER) {
            Employee trainer=employeeRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new BusinessException("TRAINER_PROFILE_NOT_FOUND", "Không có hồ sơ huấn luyện viên", HttpStatus.NOT_FOUND));
            if (!schedule.getTrainer().getId().equals(trainer.getId()))
                throw new BusinessException("TRAINER_NOT_ASSIGNED", "Bạn không được phân công lớp này", HttpStatus.FORBIDDEN);
        }
        return schedule;
    }
}
