package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.ClassSchedule;
import java.time.Instant;

public class ScheduleResponse {
    private final Long id; private final Long classId; private final String classCode; private final String className;
    private final String classType; private final Long trainerId; private final String trainerName;
    private final Instant startsAt; private final Instant endsAt; private final String location;
    private final int capacity; private final long registered; private final long available; private final String status;
    public ScheduleResponse(ClassSchedule s, long registered) {
        id=s.getId();classId=s.getPilatesClass().getId(); classCode=s.getPilatesClass().getClassCode(); className=s.getPilatesClass().getName();
        classType=s.getPilatesClass().getClassType().getName(); trainerId=s.getTrainer().getId();
        trainerName=s.getTrainer().getFullName(); startsAt=s.getStartsAt(); endsAt=s.getEndsAt();
        location=s.getPilatesClass().getLocation(); capacity=s.getCapacity(); this.registered=registered;
        available=Math.max(0, capacity-registered); status=s.getStatus().name();
    }
    public Long getId(){return id;} public String getClassCode(){return classCode;}
    public Long getClassId(){return classId;}
    public String getClassName(){return className;} public String getClassType(){return classType;}
    public Long getTrainerId(){return trainerId;} public String getTrainerName(){return trainerName;}
    public Instant getStartsAt(){return startsAt;} public Instant getEndsAt(){return endsAt;}
    public String getLocation(){return location;} public int getCapacity(){return capacity;}
    public long getRegistered(){return registered;} public long getAvailable(){return available;}
    public String getStatus(){return status;}
}
