package com.example.datnspct.dto;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    private String currentPassword; // Mật khẩu hiện tại
    private String newPassword;     // Mật khẩu mới
    private String confirmPassword; // Xác nhận mật khẩu mới
}
