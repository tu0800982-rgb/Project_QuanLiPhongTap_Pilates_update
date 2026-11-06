package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.CreateMemberRequest;
import com.nhom8.pilates.dto.request.UpdateMemberRequest;
import com.nhom8.pilates.dto.response.MemberResponse;
import com.nhom8.pilates.dto.response.PageResponse;
import com.nhom8.pilates.entity.Member;
import com.nhom8.pilates.entity.Role;
import com.nhom8.pilates.entity.User;
import com.nhom8.pilates.enums.AccountStatus;
import com.nhom8.pilates.enums.RecordStatus;
import com.nhom8.pilates.enums.RoleCode;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.MemberRepository;
import com.nhom8.pilates.repository.RoleRepository;
import com.nhom8.pilates.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository,
                         RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> search(String keyword, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by("createdAt").descending());
        String term = keyword == null ? "" : keyword.trim();
        Page<MemberResponse> result = memberRepository
                .findByFullNameContainingIgnoreCaseOrMemberCodeContainingIgnoreCaseOrPhoneContaining(
                        term, term, term, pageable)
                .map(MemberResponse::new);
        return new PageResponse<>(result);
    }

    @Transactional(readOnly = true)
    public MemberResponse find(Long id) {
        return new MemberResponse(memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("MEMBER_NOT_FOUND", "Không tìm thấy hội viên",
                        HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public MemberResponse create(CreateMemberRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("EMAIL_EXISTS", "Email đã được sử dụng", HttpStatus.CONFLICT);
        }
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("PHONE_EXISTS", "Số điện thoại đã được sử dụng", HttpStatus.CONFLICT);
        }
        Role role = roleRepository.findByCode(RoleCode.MEMBER)
                .orElseThrow(() -> new BusinessException("ROLE_NOT_CONFIGURED", "Vai trò Member chưa được cấu hình",
                        HttpStatus.INTERNAL_SERVER_ERROR));
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(role);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        Member member = new Member();
        member.setUser(user);
        member.setMemberCode(nextMemberCode());
        member.setFullName(request.getFullName().trim());
        member.setPhone(request.getPhone());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());
        member.setEmergencyContact(request.getEmergencyContact());
        member.setStatus(RecordStatus.ACTIVE);
        return new MemberResponse(memberRepository.save(member));
    }

    @Transactional
    public MemberResponse update(Long id,UpdateMemberRequest request) {
        Member member=memberRepository.findById(id).orElseThrow(() -> new BusinessException(
                "MEMBER_NOT_FOUND","Không tìm thấy hội viên",HttpStatus.NOT_FOUND));
        String email=request.getEmail().trim().toLowerCase();
        if(member.getUser()!=null && userRepository.existsByEmailIgnoreCaseAndIdNot(email,member.getUser().getId()))
            throw new BusinessException("EMAIL_EXISTS","Email Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng",HttpStatus.CONFLICT);
        if(memberRepository.existsByPhoneAndIdNot(request.getPhone(),id))
            throw new BusinessException("PHONE_EXISTS","Số điện thoại đã được sử dụng",HttpStatus.CONFLICT);
        member.setFullName(request.getFullName().trim());member.setPhone(request.getPhone());
        member.setDateOfBirth(request.getDateOfBirth());member.setGender(request.getGender());
        member.setAddress(request.getAddress());member.setEmergencyContact(request.getEmergencyContact());
        if(member.getUser()!=null){member.getUser().setFullName(member.getFullName());member.getUser().setEmail(email);userRepository.save(member.getUser());}
        return new MemberResponse(memberRepository.save(member));
    }

    @Transactional
    public MemberResponse updateStatus(Long id,RecordStatus status) {
        Member member=memberRepository.findById(id).orElseThrow(() -> new BusinessException(
                "MEMBER_NOT_FOUND","Không tìm thấy hội viên",HttpStatus.NOT_FOUND));
        member.setStatus(status);
        if(member.getUser()!=null)member.getUser().setStatus(status==RecordStatus.ACTIVE?AccountStatus.ACTIVE:AccountStatus.INACTIVE);
        return new MemberResponse(memberRepository.save(member));
    }

    private String nextMemberCode() {
        long candidate = memberRepository.count() + 1;
        String code;
        do {
            code = "MB" + String.format("%05d", candidate++);
        } while (memberRepository.existsByMemberCode(code));
        return code;
    }
}
