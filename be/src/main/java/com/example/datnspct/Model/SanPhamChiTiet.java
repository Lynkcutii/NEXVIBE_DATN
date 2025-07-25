package com.example.datnspct.Model;

import com.example.datnspct.support.enums.TrangThai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPhamCT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSPCT")
    private Integer id;

    @Column(name = "MaSPCT", length = 50)
    private String maSPCT;

    @ManyToOne
    @JoinColumn(name = "IdSP")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "IdChatLieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "IdThuongHieu")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "IdSize")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "IdMauSac")
    private MauSac mauSac;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "Gia", precision = 18, scale = 2)
    private BigDecimal gia;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "sanPhamChiTiet")
    private List<Img> images;


}
