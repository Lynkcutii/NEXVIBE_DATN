package com.example.datnspct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatLieuDTO {
    private Integer idChatLieu;
    private String maChatLieu;
    private String tenChatLieu;
    private Boolean trangThai;
} 