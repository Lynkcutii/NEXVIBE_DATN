package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ChatLieu")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdChatLieu")
    private Integer idChatLieu;

    @Column(name = "MaChatLieu", length = 50)
    private String maChatLieu;

    @Column(name = "TenChatLieu", length = 100)
    private String tenChatLieu;

    @Column(name = "TrangThai")
    private Boolean trangThai;
} 