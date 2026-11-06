package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.Employee;
import com.nhom8.pilates.entity.Member;
import com.nhom8.pilates.entity.User;
import java.time.LocalDate;

public class ProfileResponse {
    private final Long userId; private final String email; private final String role;
    private final String fullName; private final String phone; private final String profileCode;
    private final LocalDate dateOfBirth; private final String gender; private final String address;
    private final String emergencyContact; private final String specialization; private final String biography;

    public ProfileResponse(User user, Member member, Employee employee) {
        userId=user.getId();email=user.getEmail();role=user.getRole().getCode().name();
        if(member!=null){fullName=member.getFullName();phone=member.getPhone();profileCode=member.getMemberCode();
            dateOfBirth=member.getDateOfBirth();gender=member.getGender();address=member.getAddress();
            emergencyContact=member.getEmergencyContact();specialization=null;biography=null;}
        else if(employee!=null){fullName=employee.getFullName();phone=employee.getPhone();profileCode=employee.getEmployeeCode();
            dateOfBirth=null;gender=null;address=null;emergencyContact=null;
            specialization=employee.getSpecialization();biography=employee.getBiography();}
        else {fullName=user.getFullName();phone=null;profileCode=null;dateOfBirth=null;gender=null;address=null;
            emergencyContact=null;specialization=null;biography=null;}
    }
    public Long getUserId(){return userId;} public String getEmail(){return email;} public String getRole(){return role;}
    public String getFullName(){return fullName;} public String getPhone(){return phone;} public String getProfileCode(){return profileCode;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public String getGender(){return gender;} public String getAddress(){return address;}
    public String getEmergencyContact(){return emergencyContact;} public String getSpecialization(){return specialization;}
    public String getBiography(){return biography;}
}
