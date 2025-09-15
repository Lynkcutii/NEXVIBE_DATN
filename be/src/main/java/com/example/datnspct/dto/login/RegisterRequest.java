package com.example.datnspct.dto.login;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String taiKhoan;
    private String matKhau;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private String email;
}
