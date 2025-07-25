package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GioHangCT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GioHangCT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGHCT")
    private Integer idGHCT;

    @ManyToOne
    @JoinColumn(name = "IdGH", referencedColumnName = "IdGH")
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "IdSPCT", referencedColumnName = "IdSPCT")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia")
    private Double donGia;
} 