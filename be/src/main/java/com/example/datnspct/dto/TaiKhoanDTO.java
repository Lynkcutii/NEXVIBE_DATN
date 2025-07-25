package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDTO {
    private Integer idTK;
    private String taiKhoan;
    private String matKhau;
    private String chucVu;
    private Boolean trangThai;
}
