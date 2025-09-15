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
    private Integer idSP; // ID Sản Phẩm
    private String maSP; // Mã Sản Phẩm
    private String tenSP; // Tên Sản Phẩm
    private Date ngayTao; // Ngày Tạo
    private Integer tongSoLuongSanPham; // Tổng Số Lượng Sản Phẩm
    private Boolean trangThai; // Trạng Thái
    private BigDecimal minPrice; // Giá thấp nhất
    private BigDecimal maxPrice; // Giá cao nhất
}
