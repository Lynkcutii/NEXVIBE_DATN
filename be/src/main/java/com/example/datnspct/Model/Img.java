package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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