package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO {
    private Integer idKM;
    private String maKM;
    private String tenKM;
    private String hinhThucGiam; // 'PERCENTAGE' hoặc 'FIXED'
    private BigDecimal mucGiam;
    private BigDecimal giaTriDonHangToiThieu;
    private BigDecimal giamToiDa;
    private Integer soLuong;
    private Integer daSuDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Boolean trangThai;
    private Integer idKH;
}