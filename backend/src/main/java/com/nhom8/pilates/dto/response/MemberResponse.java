package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.Member;
import java.time.LocalDate;

public class MemberResponse {
    private final Long id;
    private final String memberCode;
    private final String fullName;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final String gender;
    private final String address;
    private final String emergencyContact;
    private final String status;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.memberCode = member.getMemberCode();
        this.fullName = member.getFullName();
        this.email = member.getUser() == null ? null : member.getUser().getEmail();
        this.phone = member.getPhone();
        this.dateOfBirth = member.getDateOfBirth();
        this.gender = member.getGender();
        this.address = member.getAddress();
        this.emergencyContact = member.getEmergencyContact();
        this.status = member.getStatus().name();
    }

    public Long getId() { return id; }
    public String getMemberCode() { return memberCode; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getStatus() { return status; }
}
