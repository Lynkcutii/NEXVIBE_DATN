package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Integer id; // IdVoucher
    private String code; // MaVoucher
    private String name; // TenVoucher
    private String description; // Mô tả ngắn
    private String type; // "percentage" hoặc "fixed"
    private BigDecimal value; // Giá trị giảm (MucGiam hoặc GiaGiam)
    private BigDecimal minOrder; // Giá trị đơn hàng tối thiểu
    private Date endDate; // NgayKetThuc
}