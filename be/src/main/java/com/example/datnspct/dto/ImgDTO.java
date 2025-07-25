package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgDTO {
    private Integer idImg;
    private Integer idSPCT;
    private String link;
    private String name;
    private String size;
} 