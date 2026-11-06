package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.*;
import java.time.Instant;

public class CreateScheduleRequest {
    @NotNull private Long classId;
    @NotNull private Long trainerId;
    @NotNull @Future private Instant startsAt;
    @NotNull @Future private Instant endsAt;
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private int capacity;
    public CreateScheduleRequest() { }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }
    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
