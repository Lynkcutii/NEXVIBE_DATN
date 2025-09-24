package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private Integer idSP;
    private String maSP;
    private String tenSP;
    private Date ngayTao;
    private Integer tongSoLuongSanPham;
    private Boolean trangThai;
    private String imageLink;
    private String moTa;
    private BigDecimal gia;
    private Integer idDanhMuc;
    private Integer idThuongHieu;
    private Integer idChatLieu;
}
