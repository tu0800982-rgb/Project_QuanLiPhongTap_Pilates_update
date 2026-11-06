package com.nhom8.pilates.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdminReportResponse {
    private final LocalDate from; private final LocalDate to;
    private final long totalMembers; private final long activeMembers; private final long inactiveMembers;
    private final long activeEmployees; private final long activePackages; private final long activeClasses;
    private final long schedules; private final long registrations; private final long attendanceRecords;
    private final long transactions; private final BigDecimal paidRevenue; private final double fillRatePercent;
    public AdminReportResponse(LocalDate from,LocalDate to,long totalMembers,long activeMembers,long inactiveMembers,
            long activeEmployees,long activePackages,long activeClasses,long schedules,long registrations,
            long attendanceRecords,long transactions,BigDecimal paidRevenue,double fillRatePercent) {
        this.from=from;this.to=to;this.totalMembers=totalMembers;this.activeMembers=activeMembers;
        this.inactiveMembers=inactiveMembers;this.activeEmployees=activeEmployees;this.activePackages=activePackages;
        this.activeClasses=activeClasses;this.schedules=schedules;this.registrations=registrations;
        this.attendanceRecords=attendanceRecords;this.transactions=transactions;this.paidRevenue=paidRevenue;
        this.fillRatePercent=fillRatePercent;
    }
    public LocalDate getFrom(){return from;} public LocalDate getTo(){return to;}
    public long getTotalMembers(){return totalMembers;} public long getActiveMembers(){return activeMembers;}
    public long getInactiveMembers(){return inactiveMembers;} public long getActiveEmployees(){return activeEmployees;}
    public long getActivePackages(){return activePackages;} public long getActiveClasses(){return activeClasses;}
    public long getSchedules(){return schedules;} public long getRegistrations(){return registrations;}
    public long getAttendanceRecords(){return attendanceRecords;} public long getTransactions(){return transactions;}
    public BigDecimal getPaidRevenue(){return paidRevenue;} public double getFillRatePercent(){return fillRatePercent;}
}
