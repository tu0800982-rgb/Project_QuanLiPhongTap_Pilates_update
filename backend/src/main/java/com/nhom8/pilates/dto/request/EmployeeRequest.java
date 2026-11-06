package com.nhom8.pilates.dto.request;

import com.nhom8.pilates.enums.EmployeeType;
import jakarta.validation.constraints.*;

public class EmployeeRequest {
    @NotBlank @Size(max=30) private String employeeCode;
    @NotBlank @Size(max=150) private String fullName;
    @NotBlank @Email private String email;
    @Size(min=6,max=72) private String password;
    @NotBlank @Pattern(regexp="^[0-9+]{9,15}$") private String phone;
    @NotNull private EmployeeType employeeType;
    private String specialization;private String biography;
    public EmployeeRequest() { }
    public String getEmployeeCode(){return employeeCode;}public void setEmployeeCode(String v){employeeCode=v;}
    public String getFullName(){return fullName;}public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;}public void setEmail(String v){email=v;}
    public String getPassword(){return password;}public void setPassword(String v){password=v;}
    public String getPhone(){return phone;}public void setPhone(String v){phone=v;}
    public EmployeeType getEmployeeType(){return employeeType;}public void setEmployeeType(EmployeeType v){employeeType=v;}
    public String getSpecialization(){return specialization;}public void setSpecialization(String v){specialization=v;}
    public String getBiography(){return biography;}public void setBiography(String v){biography=v;}
}
