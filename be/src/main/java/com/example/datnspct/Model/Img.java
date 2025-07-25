package com.example.datnspct.Model;

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

    @ManyToOne
    @JoinColumn(name = "IdSPCT", referencedColumnName = "IdSPCT")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "link")
    private String link;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private String size;
} 