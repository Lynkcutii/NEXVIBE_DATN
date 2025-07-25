package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangCTDTO {
    private Integer idGHCT;
    private Integer idGH;
    private Integer idSPCT;
    private Integer soLuong;
    private Double donGia;
} 