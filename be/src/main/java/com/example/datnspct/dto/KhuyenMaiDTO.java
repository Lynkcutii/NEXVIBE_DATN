package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO {
    private Integer idKM;
    private String maKM;
    private String tenKM;
    private String hinhThucGiam;
    private BigDecimal mucGiam;
    private BigDecimal giaTriDonHangToiThieu;
    private BigDecimal giamToiDa;
    private Integer soLuong;
    private Integer daSuDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Boolean trangThai;

    private List<Integer> idKHs; // nhiều khách hàng
}
