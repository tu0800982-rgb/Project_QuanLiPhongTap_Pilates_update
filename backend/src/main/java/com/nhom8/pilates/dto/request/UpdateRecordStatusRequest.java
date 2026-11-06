package com.nhom8.pilates.dto.request;

import com.nhom8.pilates.enums.RecordStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateRecordStatusRequest {
    @NotNull private RecordStatus status;
    public UpdateRecordStatusRequest() { }
    public RecordStatus getStatus(){return status;} public void setStatus(RecordStatus status){this.status=status;}
}
