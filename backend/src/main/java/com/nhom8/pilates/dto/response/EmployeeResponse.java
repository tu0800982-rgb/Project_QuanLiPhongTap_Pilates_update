package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.Employee;

public class EmployeeResponse {
    private final Long id;private final String employeeCode;private final String fullName;private final String email;
    private final String phone;private final String employeeType;private final String specialization;private final String biography;private final String status;
    public EmployeeResponse(Employee e){id=e.getId();employeeCode=e.getEmployeeCode();fullName=e.getFullName();email=e.getUser().getEmail();phone=e.getPhone();employeeType=e.getEmployeeType().name();specialization=e.getSpecialization();biography=e.getBiography();status=e.getStatus().name();}
    public Long getId(){return id;}public String getEmployeeCode(){return employeeCode;}public String getFullName(){return fullName;}
    public String getEmail(){return email;}public String getPhone(){return phone;}public String getEmployeeType(){return employeeType;}
    public String getSpecialization(){return specialization;}public String getBiography(){return biography;}public String getStatus(){return status;}
}
