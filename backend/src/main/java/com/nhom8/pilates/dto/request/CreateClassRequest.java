package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.*;

public class CreateClassRequest {
    @NotBlank private String classCode;
    @NotBlank private String name;
    @NotNull private Long classTypeId;
    @NotBlank private String location;
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private int defaultCapacity;
    public CreateClassRequest() { }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getClassTypeId() { return classTypeId; }
    public void setClassTypeId(Long classTypeId) { this.classTypeId = classTypeId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getDefaultCapacity() { return defaultCapacity; }
    public void setDefaultCapacity(int defaultCapacity) { this.defaultCapacity = defaultCapacity; }
}
