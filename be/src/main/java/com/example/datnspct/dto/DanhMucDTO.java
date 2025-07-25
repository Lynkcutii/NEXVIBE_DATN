package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMucDTO {
    private Integer idDM;

    private String tenDM;

    private Boolean trangThai;
}
