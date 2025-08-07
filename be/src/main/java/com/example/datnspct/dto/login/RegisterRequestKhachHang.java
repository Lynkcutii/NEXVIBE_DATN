package com.example.datnspct.dto.login;


import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestKhachHang {
    private String taiKhoan;
    private String matKhau;
    private String tenKH;
    private String gioiTinh;
    private String sdt;
    private String email;
    private String diaChiCuThe;
    private String tinhThanh;
    private String phuongXa;
    private String soDienThoai;
    private String ghiChu;
    private LocalDate ngaySinh;
}
