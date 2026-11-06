package com.nhom8.pilates.dto.response;

import java.util.List;

public class MemberDashboardResponse {
    private final MemberResponse member; private final List<MemberPackageResponse> activePackages;
    private final List<ClassRegistrationResponse> upcomingClasses; private final List<AttendanceResponse> recentAttendance;
    private final List<PaymentResponse> recentPayments;
    public MemberDashboardResponse(MemberResponse member,List<MemberPackageResponse> activePackages,
            List<ClassRegistrationResponse> upcomingClasses,List<AttendanceResponse> recentAttendance,
            List<PaymentResponse> recentPayments) {
        this.member=member;this.activePackages=activePackages;this.upcomingClasses=upcomingClasses;
        this.recentAttendance=recentAttendance;this.recentPayments=recentPayments;
    }
    public MemberResponse getMember(){return member;} public List<MemberPackageResponse> getActivePackages(){return activePackages;}
    public List<ClassRegistrationResponse> getUpcomingClasses(){return upcomingClasses;}
    public List<AttendanceResponse> getRecentAttendance(){return recentAttendance;}
    public List<PaymentResponse> getRecentPayments(){return recentPayments;}
}
