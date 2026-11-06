package com.nhom8.pilates.dto.request;

import com.nhom8.pilates.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public class AttendanceItemRequest {
    @NotNull private Long memberId;
    @NotNull private AttendanceStatus status;
    public AttendanceItemRequest() { }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}
