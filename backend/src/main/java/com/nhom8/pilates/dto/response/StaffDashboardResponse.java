package com.nhom8.pilates.dto.response;

import java.time.LocalDate;

public class StaffDashboardResponse {
    private final LocalDate date; private final long activeMembers; private final long classesToday;
    private final long registrationsToday; private final long attendanceToday; private final long transactionsToday;
    public StaffDashboardResponse(LocalDate date,long activeMembers,long classesToday,long registrationsToday,
                                  long attendanceToday,long transactionsToday) {
        this.date=date;this.activeMembers=activeMembers;this.classesToday=classesToday;
        this.registrationsToday=registrationsToday;this.attendanceToday=attendanceToday;
        this.transactionsToday=transactionsToday;
    }
    public LocalDate getDate(){return date;} public long getActiveMembers(){return activeMembers;}
    public long getClassesToday(){return classesToday;} public long getRegistrationsToday(){return registrationsToday;}
    public long getAttendanceToday(){return attendanceToday;} public long getTransactionsToday(){return transactionsToday;}
}
