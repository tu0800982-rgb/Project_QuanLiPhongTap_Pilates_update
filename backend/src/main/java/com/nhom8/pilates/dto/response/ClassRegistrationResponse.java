package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.ClassRegistration;
import java.time.Instant;

public class ClassRegistrationResponse {
    private final Long id; private final Long scheduleId; private final Long memberId;
    private final String memberName; private final String className; private final Instant startsAt;
    private final String status; private final boolean lateCancellation;
    public ClassRegistrationResponse(ClassRegistration r) {
        id=r.getId(); scheduleId=r.getSchedule().getId(); memberId=r.getMember().getId();
        memberName=r.getMember().getFullName(); className=r.getSchedule().getPilatesClass().getName();
        startsAt=r.getSchedule().getStartsAt(); status=r.getStatus().name(); lateCancellation=r.isLateCancellation();
    }
    public Long getId(){return id;} public Long getScheduleId(){return scheduleId;}
    public Long getMemberId(){return memberId;} public String getMemberName(){return memberName;}
    public String getClassName(){return className;} public Instant getStartsAt(){return startsAt;}
    public String getStatus(){return status;} public boolean isLateCancellation(){return lateCancellation;}
}
