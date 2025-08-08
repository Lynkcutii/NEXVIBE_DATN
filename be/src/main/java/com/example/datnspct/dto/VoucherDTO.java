package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Integer id;
    private String maVoucher;
    private String tenVoucher;
    private String hinhThucGiam; // "%" hoặc "VND"
    private BigDecimal mucGiam;
    private BigDecimal giamToiDa;
    private BigDecimal donGiaKhiGiam;
    private BigDecimal giaGiam;
    private BigDecimal giaTriDonHangToiThieu;
    private Integer soLuong;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai;
    private List<Integer> applicableProductIds; // Danh sách idSPCT từ Voucher_SP
}