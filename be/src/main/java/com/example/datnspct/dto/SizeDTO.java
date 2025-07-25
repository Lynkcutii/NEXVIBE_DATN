package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeDTO {
    private Integer idSize;
    private String maSize;
    private String tenSize;
    private Boolean trangThai;
} 