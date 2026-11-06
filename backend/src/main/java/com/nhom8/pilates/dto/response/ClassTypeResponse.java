package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.ClassType;

public class ClassTypeResponse {
    private final Long id;private final String code;private final String name;private final String status;
    public ClassTypeResponse(ClassType source){id=source.getId();code=source.getCode();name=source.getName();status=source.getStatus().name();}
    public Long getId(){return id;}public String getCode(){return code;}public String getName(){return name;}public String getStatus(){return status;}
}
