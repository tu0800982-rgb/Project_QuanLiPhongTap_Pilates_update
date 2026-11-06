package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.MemberPackage;
import java.time.LocalDate;

public class MemberPackageResponse {
    private final Long id;
    private final Long memberId;
    private final String memberName;
    private final Long packageId;
    private final String packageName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int totalSessions;
    private final int remainingSessions;
    private final String status;

    public MemberPackageResponse(MemberPackage source) {
        id = source.getId(); memberId = source.getMember().getId(); memberName = source.getMember().getFullName();
        packageId = source.getPilatesPackage().getId(); packageName = source.getPilatesPackage().getName();
        startDate = source.getStartDate(); endDate = source.getEndDate();
        totalSessions = source.getTotalSessions(); remainingSessions = source.getRemainingSessions();
        status = source.getStatus().name();
    }
    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public Long getPackageId() { return packageId; }
    public String getPackageName() { return packageName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTotalSessions() { return totalSessions; }
    public int getRemainingSessions() { return remainingSessions; }
    public String getStatus() { return status; }
}
