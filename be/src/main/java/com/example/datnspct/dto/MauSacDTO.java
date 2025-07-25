package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MauSacDTO {
    private Integer idMauSac;
    private String maMauSac;
    private String tenMauSac;
    private Boolean trangThai;
} 