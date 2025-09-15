package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietDTO {
    private Integer id;
    private Integer idSP; // IdSP
    private String maSPCT;
    private String tenSP;
    private BigDecimal gia;
    private Integer soLuong;
    private Boolean trangThai;
    private String tenMauSac;
    private String tenSize;
    private String linkAnhDauTien; // Link ảnh đầu tiên
    private List<String> imageLinks; // Danh sách link ảnh
    private Integer idMauSac;
    private Integer idSize;
}