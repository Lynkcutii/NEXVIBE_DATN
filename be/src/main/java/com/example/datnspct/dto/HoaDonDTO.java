package com.example.datnspct.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
public class HoaDonDTO {
    private Integer idHD;
    private String maHD;
    private Integer idKhachHang;
    private String customerName;
    private Integer idNhanVien;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private BigDecimal tongTien;
    private Boolean trangThai;
}
