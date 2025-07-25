package com.example.datnspct.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThuongHieuDTO {
    private Integer idThuongHieu;
    private String maThuongHieu;
    private String tenThuongHieu;
    private Boolean trangThai;
}
