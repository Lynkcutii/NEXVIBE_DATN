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
    private Integer idHD;
    private Integer idCtSanPham;
    private Integer idSP;
    private String maSPCT;
    private String tenSanPham;
    private String tenSize;
    private String tenMauSac;
    private String tenThuongHieu;
    private String tenChatLieu;
    private String anhGiay;
    private Integer soLuongTonKho;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private Integer idVoucher;
}