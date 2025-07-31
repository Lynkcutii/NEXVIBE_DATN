package com.example.datnspct.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SanPhamChiTietDTO {
    private Integer id;
    private Integer idSP; // IdSP
    private String maSPCT;
    private String tenSP;
    private BigDecimal gia;
    private Integer soLuong;
    private String moTa;
    private Boolean trangThai;
    private String tenDanhMuc;
    private String tenThuongHieu;
    private String tenMauSac;
    private String tenChatLieu;
    private String tenSize;
    private String link; // Trường ảnh
}