package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.PilatesClass;

public class ClassResponse {
    private final Long id; private final String classCode; private final String name;
    private final Long classTypeId; private final String classType; private final String location; private final int defaultCapacity;
    private final String status;
    public ClassResponse(PilatesClass source) {
        id=source.getId(); classCode=source.getClassCode(); name=source.getName();
        classTypeId=source.getClassType().getId();classType=source.getClassType().getName(); location=source.getLocation();
        defaultCapacity=source.getDefaultCapacity(); status=source.getStatus().name();
    }
    public Long getId(){return id;} public String getClassCode(){return classCode;}
    public String getName(){return name;} public String getClassType(){return classType;}
    public Long getClassTypeId(){return classTypeId;}
    public String getLocation(){return location;} public int getDefaultCapacity(){return defaultCapacity;}
    public String getStatus(){return status;}
}
