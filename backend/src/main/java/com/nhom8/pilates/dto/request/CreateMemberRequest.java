package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CreateMemberRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 150, message = "Họ tên không được quá 150 ký tự")
    private String fullName;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 72, message = "Mật khẩu phải từ 6 đến 72 ký tự")
    private String password;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9+]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Past(message = "Ngày sinh phải ở trong quá khứ")
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String emergencyContact;

    public CreateMemberRequest() { }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}
