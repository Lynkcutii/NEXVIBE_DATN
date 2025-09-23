package com.example.datnspct.dto;

import lombok.Data;

@Data
public class DiaChiKhachHangDTO {
    private Integer idDiaChi;
    private Integer idKH;
    private String diaChiCuThe;
    private String tinhThanh;
    private String phuongXa;
    private String soDienThoai;
    private String ghiChu;
    private Boolean trangThai;
}
