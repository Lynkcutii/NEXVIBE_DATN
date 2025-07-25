package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDonCT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHDCT")
    private Integer idHDCT;

    @ManyToOne
    @JoinColumn(name = "IdSP", referencedColumnName = "IdSPCT")
    private SanPhamChiTiet sanPhamct;

    @ManyToOne
    @JoinColumn(name = "IdHD", referencedColumnName = "IdHD")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "IdKM", referencedColumnName = "IdKM")
    private KhuyenMai khuyenMai;

    @ManyToOne
    @JoinColumn(name = "IdPTT", referencedColumnName = "IdPTT")
    private PhuongTT phuongThucThanhToan;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;


}
