package com.nhom8.pilates.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class SaveAttendanceRequest {
    @NotEmpty(message = "Danh sách điểm danh không được để trống")
    @Valid
    private List<AttendanceItemRequest> items;
    public SaveAttendanceRequest() { }
    public List<AttendanceItemRequest> getItems() { return items; }
    public void setItems(List<AttendanceItemRequest> items) { this.items = items; }
}
