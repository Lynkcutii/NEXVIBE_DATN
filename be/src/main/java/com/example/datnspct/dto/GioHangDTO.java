package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangDTO {
    private Integer idGH;
    private String maGH;
    private Integer idKH;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private Integer idTK;
} 