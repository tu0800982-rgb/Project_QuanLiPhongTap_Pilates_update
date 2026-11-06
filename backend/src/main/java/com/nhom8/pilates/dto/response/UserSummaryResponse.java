package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.User;

public class UserSummaryResponse {
    private final Long id;
    private final String email;
    private final String fullName;
    private final String role;

    public UserSummaryResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.role = user.getRole().getCode().name();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
