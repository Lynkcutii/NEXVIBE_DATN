package com.example.datnspct.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private Integer idSP; // ID Sản Phẩm
    private String maSP; // Mã Sản Phẩm
    private String tenSP; // Tên Sản Phẩm
    private Boolean trangThai; // Trạng Thái
}
