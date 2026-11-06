package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.dto.response.ClassRegistrationResponse;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Map;

@Service
public class ClassRegistrationService {
    private final ClassScheduleRepository scheduleRepository;
    private final ClassRegistrationRepository registrationRepository;
    private final MemberPackageRepository memberPackageRepository;
    private final MemberRepository memberRepository;
    private final SessionUsageLedgerRepository ledgerRepository;
    private final CurrentUserService currentUserService;
    private final long cancellationHours;
    private final AuditService auditService;

    public ClassRegistrationService(ClassScheduleRepository scheduleRepository,
                                    ClassRegistrationRepository registrationRepository,
                                    MemberPackageRepository memberPackageRepository,
                                    MemberRepository memberRepository,
                                    SessionUsageLedgerRepository ledgerRepository,
                                    CurrentUserService currentUserService,
                                    @Value("${app.business.cancellation-hours}") long cancellationHours,
                                    AuditService auditService) {
        this.scheduleRepository=scheduleRepository; this.registrationRepository=registrationRepository;
        this.memberPackageRepository=memberPackageRepository; this.memberRepository=memberRepository;
        this.ledgerRepository=ledgerRepository; this.currentUserService=currentUserService;
        this.cancellationHours=cancellationHours;
        this.auditService=auditService;
    }

    @Transactional
    public ClassRegistrationResponse register(ClassRegistrationRequest request) {
        User actor=currentUserService.requireUser();
        Member member=resolveMember(actor,request.getMemberId());
        ClassSchedule schedule=scheduleRepository.findByIdForUpdate(request.getScheduleId())
                .orElseThrow(() -> new BusinessException("SCHEDULE_NOT_FOUND", "Không tìm thấy lịch học", HttpStatus.NOT_FOUND));
        if (schedule.getStatus()!=ScheduleStatus.OPEN || !schedule.getStartsAt().isAfter(Instant.now()))
            throw new BusinessException("SCHEDULE_NOT_OPEN", "Lịch học không còn mở đăng ký", HttpStatus.CONFLICT);
        long occupied=registrationRepository.countByScheduleIdAndStatus(schedule.getId(),RegistrationStatus.REGISTERED);
        if (occupied>=schedule.getCapacity())
            throw new BusinessException("CLASS_FULL", "Lớp học đã đủ số lượng", HttpStatus.CONFLICT);
        if (registrationRepository.existsByScheduleIdAndMemberIdAndStatus(schedule.getId(),member.getId(),RegistrationStatus.REGISTERED))
            throw new BusinessException("DUPLICATE_REGISTRATION", "Hội viên đã đăng ký lịch học này", HttpStatus.CONFLICT);
        if (registrationRepository.countMemberConflicts(member.getId(),schedule.getStartsAt(),schedule.getEndsAt())>0)
            throw new BusinessException("SCHEDULE_CONFLICT", "Hội viên bị trùng lịch học", HttpStatus.CONFLICT);

        MemberPackage memberPackage=memberPackageRepository.findEligibleByIdForUpdate(request.getMemberPackageId())
                .orElseThrow(() -> new BusinessException("MEMBER_PACKAGE_NOT_FOUND", "Không tìm thấy gói hội viên", HttpStatus.NOT_FOUND));
        validatePackage(memberPackage,member,schedule);
        ClassRegistration registration=new ClassRegistration(); registration.setSchedule(schedule);
        registration.setMember(member); registration.setMemberPackage(memberPackage);
        registration.setStatus(RegistrationStatus.REGISTERED); registration.setRegisteredBy(actor);
        ClassRegistration saved=registrationRepository.save(registration);
        memberPackage.setRemainingSessions(memberPackage.getRemainingSessions()-1);
        memberPackageRepository.save(memberPackage);
        SessionUsageLedger reservation=new SessionUsageLedger(); reservation.setMemberPackage(memberPackage);
        reservation.setRegistration(saved); reservation.setDelta(-1); reservation.setReason("REGISTRATION_RESERVED");
        reservation.setCreatedBy(actor); ledgerRepository.save(reservation);
        auditService.record(actor,"CLASS_REGISTERED","ClassRegistration",saved.getId(),null,
                Map.of("scheduleId",schedule.getId(),"memberId",member.getId(),"status",saved.getStatus().name()));
        return new ClassRegistrationResponse(saved);
    }

    @Transactional
    public ClassRegistrationResponse cancel(Long id, CancelRegistrationRequest request) {
        User actor=currentUserService.requireUser();
        ClassRegistration registration=registrationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("REGISTRATION_NOT_FOUND", "Không tìm thấy đăng ký", HttpStatus.NOT_FOUND));
        if (actor.getRole().getCode()==RoleCode.MEMBER) {
            Member own=memberRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new BusinessException("MEMBER_PROFILE_NOT_FOUND", "Không có hồ sơ hội viên", HttpStatus.NOT_FOUND));
            if (!own.getId().equals(registration.getMember().getId()))
                throw new BusinessException("ACCESS_DENIED", "Bạn chỉ được hủy đăng ký của bản thân", HttpStatus.FORBIDDEN);
        }
        if (registration.getStatus()!=RegistrationStatus.REGISTERED)
            throw new BusinessException("REGISTRATION_ALREADY_CANCELLED", "Đăng ký đã được hủy", HttpStatus.CONFLICT);
        Instant now=Instant.now();
        if (!registration.getSchedule().getStartsAt().isAfter(now))
            throw new BusinessException("CLASS_ALREADY_STARTED", "Không thể hủy khi lớp đã bắt đầu", HttpStatus.CONFLICT);
        boolean late=Duration.between(now,registration.getSchedule().getStartsAt()).toHours()<cancellationHours;
        registration.setStatus(RegistrationStatus.CANCELLED); registration.setCancelledBy(actor);
        registration.setCancelledAt(now); registration.setCancellationReason(request.getReason().trim());
        registration.setLateCancellation(late);
        registrationRepository.save(registration);
        if (!late) refundRegistration(registration,actor);
        auditService.record(actor,"CLASS_REGISTRATION_CANCELLED","ClassRegistration",registration.getId(),
                Map.of("status",RegistrationStatus.REGISTERED.name()),
                Map.of("status",RegistrationStatus.CANCELLED.name(),"lateCancellation",late));
        return new ClassRegistrationResponse(registration);
    }

    @Transactional(readOnly=true)
    public List<ClassRegistrationResponse> mine() {
        User actor=currentUserService.requireUser();
        Member member=memberRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new BusinessException("MEMBER_PROFILE_NOT_FOUND", "Không có hồ sơ hội viên", HttpStatus.NOT_FOUND));
        return registrationRepository.findByMemberIdOrderByScheduleStartsAtDesc(member.getId()).stream()
                .map(ClassRegistrationResponse::new).toList();
    }

    @Transactional(readOnly=true)
    public List<ClassRegistrationResponse> all(){
        return registrationRepository.findAllByOrderByRegisteredAtDesc().stream().map(ClassRegistrationResponse::new).toList();
    }

    private void validatePackage(MemberPackage mp, Member member, ClassSchedule schedule) {
        LocalDate classDate=schedule.getStartsAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
        if (!mp.getMember().getId().equals(member.getId()))
            throw new BusinessException("PACKAGE_OWNERSHIP_MISMATCH", "Gói tập không thuộc hội viên", HttpStatus.FORBIDDEN);
        if (mp.getStatus()!=MemberPackageStatus.ACTIVE || classDate.isBefore(mp.getStartDate()) || classDate.isAfter(mp.getEndDate()))
            throw new BusinessException("PACKAGE_EXPIRED", "Gói tập không có hiệu lực vào ngày học", HttpStatus.CONFLICT);
        if (mp.getRemainingSessions()<=0)
            throw new BusinessException("NO_SESSIONS_LEFT", "Gói tập không còn buổi", HttpStatus.CONFLICT);
        boolean eligible=mp.getPilatesPackage().getClassTypes().stream()
                .anyMatch(type -> type.getId().equals(schedule.getPilatesClass().getClassType().getId()));
        if (!eligible)
            throw new BusinessException("PACKAGE_NOT_ELIGIBLE", "Gói tập không áp dụng cho loại lớp này", HttpStatus.CONFLICT);
    }

    private void refundRegistration(ClassRegistration registration, User actor) {
        if (ledgerRepository.existsByRegistrationIdAndReason(registration.getId(),"REGISTRATION_REFUND")) return;
        MemberPackage mp=registration.getMemberPackage();
        if (mp.getRemainingSessions()>=mp.getTotalSessions()) return;
        mp.setRemainingSessions(mp.getRemainingSessions()+1); memberPackageRepository.save(mp);
        SessionUsageLedger ledger=new SessionUsageLedger(); ledger.setMemberPackage(mp);
        ledger.setRegistration(registration); ledger.setDelta(1); ledger.setReason("REGISTRATION_REFUND");
        ledger.setCreatedBy(actor); ledgerRepository.save(ledger);
    }

    private Member resolveMember(User actor, Long memberId) {
        if (actor.getRole().getCode()==RoleCode.MEMBER) {
            Member own=memberRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new BusinessException("MEMBER_PROFILE_NOT_FOUND", "Không có hồ sơ hội viên", HttpStatus.NOT_FOUND));
            if (memberId!=null && !memberId.equals(own.getId()))
                throw new BusinessException("ACCESS_DENIED", "Bạn chỉ được đăng ký cho bản thân", HttpStatus.FORBIDDEN);
            return own;
        }
        if (memberId==null) throw new BusinessException("MEMBER_REQUIRED", "Vui lòng chọn hội viên", HttpStatus.BAD_REQUEST);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("MEMBER_NOT_FOUND", "Không tìm thấy hội viên", HttpStatus.NOT_FOUND));
    }
}
