package com.example.datnspct.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPhamCT")
@Getter
@Setter
@Data
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSPCT")
    private Integer id;

    @Column(name = "MaSPCT")
    private String maSPCT;

    @ManyToOne
    @JoinColumn(name = "IdSP")
    private SanPham sanPham;

    // ----- CÁC TRƯỜNG BỊ LOẠI BỎ VÌ KHÔNG CÓ TRONG BẢNG SanPhamCT -----
    // private DanhMuc danhMuc;
    // private ThuongHieu thuongHieu;
    // private ChatLieu chatLieu;
    // -----------------------------------------------------------------

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdMauSac")
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdSize")
    private Size size;

    @Column(name = "Gia")
    private BigDecimal gia;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    // Thêm quan hệ một-nhiều tới bảng Img
    @OneToMany(mappedBy = "sanPhamChiTiet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Img> images;
}