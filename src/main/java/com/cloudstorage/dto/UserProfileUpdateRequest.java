package com.cloudstorage.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
}
