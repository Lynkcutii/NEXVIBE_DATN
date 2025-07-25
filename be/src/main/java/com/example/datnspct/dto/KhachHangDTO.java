package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private Integer idKH; // ID Khách Hàng
    private String maKH; // Mã Khách Hàng
    private String tenKH; // Tên Khách Hàng
    private String gioiTinh; // Giới Tính
    private String sdt; // Số Điện Thoại
    private String diaChi; // Địa Chỉ
    private Integer idTK; // ID Tài Khoản
    private Boolean trangThai; // Trạng Thái
}
