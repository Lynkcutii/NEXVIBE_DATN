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
    private String maVoucher;
    private String tenVoucher;
    private String hinhThucGiam;
    private BigDecimal mucGiam;
    private BigDecimal giaTriDonHangToiThieu;
    private Integer soLuong;
    private Integer daSuDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Boolean trangThai;
    private Integer idNV;
}
