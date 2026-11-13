package com.queuematrix.backend.dto.response;

import com.queuematrix.backend.entity.OrganizationType;
import com.queuematrix.backend.entity.Role;
import com.queuematrix.backend.entity.User;
import com.queuematrix.backend.entity.UserStatus;

public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
    private OrganizationType preferredOrgType;
    private String organizationName;

    public UserSummaryDto() {}

    public static UserSummaryDto fromEntity(User user) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setPreferredOrgType(user.getPreferredOrgType());
        if (user.getOrganization() != null) {
            dto.setOrganizationName(user.getOrganization().getName());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public OrganizationType getPreferredOrgType() {
        return preferredOrgType;
    }

    public void setPreferredOrgType(OrganizationType preferredOrgType) {
        this.preferredOrgType = preferredOrgType;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
