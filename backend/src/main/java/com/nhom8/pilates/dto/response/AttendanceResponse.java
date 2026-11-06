package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.Attendance;
import java.time.Instant;

public class AttendanceResponse {
    private final Long id; private final Long scheduleId; private final Long memberId;
    private final String memberName; private final String className; private final Instant startsAt;
    private final String status; private final Instant markedAt; private final Instant updatedAt;
    public AttendanceResponse(Attendance a) {
        id=a.getId(); scheduleId=a.getSchedule().getId(); memberId=a.getMember().getId();
        memberName=a.getMember().getFullName(); className=a.getSchedule().getPilatesClass().getName();
        startsAt=a.getSchedule().getStartsAt(); status=a.getStatus().name();
        markedAt=a.getMarkedAt(); updatedAt=a.getUpdatedAt();
    }
    public Long getId(){return id;} public Long getScheduleId(){return scheduleId;}
    public Long getMemberId(){return memberId;} public String getMemberName(){return memberName;}
    public String getClassName(){return className;} public Instant getStartsAt(){return startsAt;}
    public String getStatus(){return status;} public Instant getMarkedAt(){return markedAt;}
    public Instant getUpdatedAt(){return updatedAt;}
}
