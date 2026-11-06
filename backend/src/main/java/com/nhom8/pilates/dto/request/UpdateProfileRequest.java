package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UpdateProfileRequest {
    @NotBlank @Size(max = 150) private String fullName;
    @NotBlank @Pattern(regexp = "^[0-9+]{9,15}$") private String phone;
    private LocalDate dateOfBirth;
    @Size(max = 20) private String gender;
    @Size(max = 255) private String address;
    @Size(max = 150) private String emergencyContact;
    @Size(max = 255) private String specialization;
    @Size(max = 2000) private String biography;

    public UpdateProfileRequest() { }
    public String getFullName(){return fullName;} public void setFullName(String value){fullName=value;}
    public String getPhone(){return phone;} public void setPhone(String value){phone=value;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate value){dateOfBirth=value;}
    public String getGender(){return gender;} public void setGender(String value){gender=value;}
    public String getAddress(){return address;} public void setAddress(String value){address=value;}
    public String getEmergencyContact(){return emergencyContact;} public void setEmergencyContact(String value){emergencyContact=value;}
    public String getSpecialization(){return specialization;} public void setSpecialization(String value){specialization=value;}
    public String getBiography(){return biography;} public void setBiography(String value){biography=value;}
}
