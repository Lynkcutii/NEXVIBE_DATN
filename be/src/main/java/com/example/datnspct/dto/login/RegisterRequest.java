package com.example.datnspct.dto.login;

import lombok.Data;

@Data
public class RegisterRequest {
    private String taiKhoan;
    private String matKhau;
    private String maKH;
    private String tenKH;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private String email;
}
