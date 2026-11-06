package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.ClassRegistration;
import com.nhom8.pilates.enums.AttendanceStatus;

public class AttendanceRosterItemResponse {
    private final Long registrationId; private final Long memberId; private final String memberCode;
    private final String memberName; private final AttendanceStatus attendanceStatus;
    public AttendanceRosterItemResponse(ClassRegistration r, AttendanceStatus attendanceStatus) {
        registrationId=r.getId(); memberId=r.getMember().getId(); memberCode=r.getMember().getMemberCode();
        memberName=r.getMember().getFullName(); this.attendanceStatus=attendanceStatus;
    }
    public Long getRegistrationId(){return registrationId;} public Long getMemberId(){return memberId;}
    public String getMemberCode(){return memberCode;} public String getMemberName(){return memberName;}
    public AttendanceStatus getAttendanceStatus(){return attendanceStatus;}
}
