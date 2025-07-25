package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IdHD;

    @Column(name = "MaHD", length = 50)
    private String maHD;

    @Column(name = "IdKH")
    private Integer idKhachHang;

    @Column(name = "IdNV")
    private Integer idNhanVien;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdKH", referencedColumnName = "IdKH", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "IdNV", referencedColumnName = "IdNV", insertable = false, updatable = false)
    private NhanVien nhanVien;

}
