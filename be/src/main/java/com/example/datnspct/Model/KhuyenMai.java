package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KhuyenMai")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdKM")
    private Integer idKM;

    @Column(name = "MaKM", length = 50, nullable = false, unique = true)
    private String maKM;

    @Column(name = "TenKM", length = 255, nullable = false)
    private String tenKM;

    @Column(name = "HinhThucGiam", length = 20, nullable = false)
    private String hinhThucGiam; // 'percentage' hoặc 'fixed'

    @Column(name = "MucGiam", precision = 18, scale = 2, nullable = false)
    private BigDecimal mucGiam;

    @Column(name = "GiamToiDa", precision = 15, scale = 2, nullable = true)
    private BigDecimal giamToiDa;

    @Column(name = "GiaTriDonHangToiThieu", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriDonHangToiThieu = BigDecimal.ZERO;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DaSuDung", nullable = false)
    private Integer daSuDung = 0;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "TrangThai", nullable = false)
    private Boolean trangThai = true;

    @ManyToMany
    @JoinTable(
            name = "KhuyenMai_KhachHang",
            joinColumns = @JoinColumn(name = "IdKM"),
            inverseJoinColumns = @JoinColumn(name = "IdKH")
    )
    private List<KhachHang> customers = new ArrayList<>();
}