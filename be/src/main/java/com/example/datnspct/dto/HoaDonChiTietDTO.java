package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietDTO {
    private Integer idHDCT;
    private Integer idSP;
    private Integer idHD;
    private Integer idKM;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String tenSanPham; // Lấy từ SanPhamCT
    private String tenSize;
    private String tenMauSac;


    private Integer idCtSanPham;


}
