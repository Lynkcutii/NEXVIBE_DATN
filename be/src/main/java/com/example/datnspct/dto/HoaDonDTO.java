package com.example.datnspct.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
//@Builder
public class HoaDonDTO {
    private Integer idHD;
    private String maHD;
    private Integer idKhachHang;
    private String customerName;
    private Integer idNhanVien;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private BigDecimal tongTien;
    private String trangThai;
    private String loaiHoaDon;
    private int totalProducts;

//    private Integer idHD;
//    private String maHD;
//    private Integer idKhachHang;
//    private Integer idNhanVien;
    private Integer idKM;
//    private String loaiHoaDon;
//    private LocalDateTime ngayTao;
//    private LocalDateTime ngaySua;
//    private BigDecimal tongTien;
//    private String trangThai;
    private Integer idPT;
    private String maGiaoDich;
    private List<HoaDonChiTietDTO> chiTietSanPham;
}
