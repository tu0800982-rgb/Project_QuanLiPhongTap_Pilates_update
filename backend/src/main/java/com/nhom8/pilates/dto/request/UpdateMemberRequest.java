package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UpdateMemberRequest {
    @NotBlank @Size(max=150) private String fullName;
    @NotBlank @Email @Size(max=191) private String email;
    @NotBlank @Pattern(regexp="^[0-9+]{9,15}$") private String phone;
    @Past private LocalDate dateOfBirth;
    private String gender; private String address; private String emergencyContact;
    public UpdateMemberRequest() { }
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getEmergencyContact(){return emergencyContact;} public void setEmergencyContact(String v){emergencyContact=v;}
}
