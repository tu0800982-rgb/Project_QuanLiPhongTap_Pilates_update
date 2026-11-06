package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.UpdateProfileRequest;
import com.nhom8.pilates.dto.response.ProfileResponse;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final CurrentUserService currentUser; private final UserRepository users;
    private final MemberRepository members; private final EmployeeRepository employees;
    public ProfileService(CurrentUserService currentUser,UserRepository users,MemberRepository members,EmployeeRepository employees){
        this.currentUser=currentUser;this.users=users;this.members=members;this.employees=employees;}

    @Transactional(readOnly=true)
    public ProfileResponse get(){User user=currentUser.requireUser();return response(user);}

    @Transactional
    public ProfileResponse update(UpdateProfileRequest request){
        User user=currentUser.requireUser();Member member=members.findByUserId(user.getId()).orElse(null);
        Employee employee=employees.findByUserId(user.getId()).orElse(null);
        String name=request.getFullName().trim();String phone=request.getPhone();
        if(member!=null){
            if(members.existsByPhoneAndIdNot(phone,member.getId()))throw new BusinessException("PHONE_EXISTS","Số điện thoại đã tồn tại",HttpStatus.CONFLICT);
            member.setFullName(name);member.setPhone(phone);member.setDateOfBirth(request.getDateOfBirth());member.setGender(request.getGender());
            member.setAddress(request.getAddress());member.setEmergencyContact(request.getEmergencyContact());members.save(member);
        }else if(employee!=null){
            if(employees.existsByPhoneAndIdNot(phone,employee.getId()))throw new BusinessException("PHONE_EXISTS","Số điện thoại đã tồn tại",HttpStatus.CONFLICT);
            employee.setFullName(name);employee.setPhone(phone);employee.setSpecialization(request.getSpecialization());
            employee.setBiography(request.getBiography());employees.save(employee);
        }else throw new BusinessException("PROFILE_NOT_FOUND","Tài khoản chưa có hồ sơ nghiệp vụ",HttpStatus.NOT_FOUND);
        user.setFullName(name);users.save(user);return response(user);
    }
    private ProfileResponse response(User user){return new ProfileResponse(user,members.findByUserId(user.getId()).orElse(null),employees.findByUserId(user.getId()).orElse(null));}
}
