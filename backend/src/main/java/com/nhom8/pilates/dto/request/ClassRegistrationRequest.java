package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.NotNull;

public class ClassRegistrationRequest {
    @NotNull private Long scheduleId;
    private Long memberId;
    @NotNull private Long memberPackageId;
    public ClassRegistrationRequest() { }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberPackageId() { return memberPackageId; }
    public void setMemberPackageId(Long memberPackageId) { this.memberPackageId = memberPackageId; }
}
