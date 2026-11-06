package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.UpdatePaymentStatusRequest;
import com.nhom8.pilates.dto.response.PaymentResponse;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberPackageRepository memberPackageRepository;
    private final MemberRepository memberRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public PaymentService(PaymentRepository paymentRepository, MemberPackageRepository memberPackageRepository,
                          MemberRepository memberRepository, CurrentUserService currentUserService,
                          AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.memberPackageRepository = memberPackageRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.auditService=auditService;
    }

    @Transactional
    public PaymentResponse updateStatus(Long paymentId, UpdatePaymentStatusRequest request) {
        User actor = currentUserService.requireUser();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Không tìm thấy giao dịch",
                        HttpStatus.NOT_FOUND));
        PaymentStatus target = request.getStatus();
        PaymentStatus previous=payment.getStatus();
        validateTransition(payment.getStatus(), target, request.getReason());
        MemberPackage memberPackage = payment.getMemberPackage();
        Instant now = Instant.now();

        if (target == PaymentStatus.PAID) {
            payment.setPaidAt(now);
            payment.setPerformedBy(actor);
            LocalDate today = LocalDate.now();
            memberPackage.setStartDate(today);
            memberPackage.setEndDate(today.plusDays(memberPackage.getPilatesPackage().getDurationDays() - 1L));
            memberPackage.setStatus(MemberPackageStatus.ACTIVE);
        } else if (target == PaymentStatus.FAILED) {
            payment.setPerformedBy(actor);
            memberPackage.setStatus(MemberPackageStatus.CANCELLED);
        } else if (target == PaymentStatus.REFUNDED) {
            payment.setRefundedAt(now);
            payment.setRefundReason(request.getReason().trim());
            payment.setPerformedBy(actor);
            memberPackage.setStatus(MemberPackageStatus.CANCELLED);
        }
        payment.setStatus(target);
        memberPackageRepository.save(memberPackage);
        Payment saved=paymentRepository.save(payment);
        if(auditService!=null)auditService.record(actor,"PAYMENT_STATUS_CHANGED","Payment",saved.getId(),
                Map.of("status",previous.name()),Map.of("status",target.name()));
        return new PaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> list(int page, int size) {
        return paymentRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> myPayments() {
        User actor = currentUserService.requireUser();
        Member member = memberRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new BusinessException("MEMBER_PROFILE_NOT_FOUND", "Tài khoản chưa có hồ sơ hội viên",
                        HttpStatus.NOT_FOUND));
        return paymentRepository.findByMemberIdOrderByCreatedAtDesc(member.getId()).stream()
                .map(PaymentResponse::new).toList();
    }

    private void validateTransition(PaymentStatus current, PaymentStatus target, String reason) {
        if (current == PaymentStatus.PENDING && (target == PaymentStatus.PAID || target == PaymentStatus.FAILED)) return;
        if (current == PaymentStatus.PAID && target == PaymentStatus.REFUNDED) {
            if (reason == null || reason.isBlank()) {
                throw new BusinessException("REFUND_REASON_REQUIRED", "Hoàn tiền phải có lý do", HttpStatus.BAD_REQUEST);
            }
            return;
        }
        throw new BusinessException("INVALID_PAYMENT_TRANSITION",
                "Không thể chuyển giao dịch từ " + current + " sang " + target, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
