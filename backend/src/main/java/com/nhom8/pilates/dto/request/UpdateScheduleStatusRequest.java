package com.nhom8.pilates.dto.request;
import com.nhom8.pilates.enums.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
public class UpdateScheduleStatusRequest {
    @NotNull private ScheduleStatus status;
    public UpdateScheduleStatusRequest() { }
    public ScheduleStatus getStatus(){return status;} public void setStatus(ScheduleStatus value){status=value;}
}
