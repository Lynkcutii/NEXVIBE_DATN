package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private Integer idKH;
    private String maKH;
    private String tenKH;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String email;
    private String sdt;
    private Integer idTK;
    private Boolean trangThai;
    private List<DiaChiKhachHangDTO> diaChiList;
    private String taiKhoan;
    private String chucVu;
}
