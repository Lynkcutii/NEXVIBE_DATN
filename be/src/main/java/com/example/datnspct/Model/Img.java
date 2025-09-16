package com.example.datnspct.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Img")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Img {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdImg")
    private Integer idImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdSPCT")
    @JsonIgnore
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "link")
    private String link;

    // ----- CÁC TRƯỜNG BỊ LOẠI BỎ VÌ KHÔNG CÓ TRONG CSDL -----
    // private String name;
    // private String size;
    // --------------------------------------------------------
}