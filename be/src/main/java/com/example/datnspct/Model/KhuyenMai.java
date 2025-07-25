package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "MaVoucher", length = 50, unique = true, nullable = false)
    private String maVoucher;

    @Column(name = "TenVoucher", length = 255, nullable = false)
    private String tenVoucher;

    @Column(name = "HinhThucGiam", length = 20, nullable = false)
    private String hinhThucGiam; // 'percentage' hoặc 'fixed'

    @Column(name = "MucGiam", precision = 18, scale = 2, nullable = false)
    private BigDecimal mucGiam;

    @Column(name = "GiaTriDonHangToiThieu", precision = 18, scale = 2)
    private BigDecimal giaTriDonHangToiThieu = BigDecimal.ZERO;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DaSuDung")
    private Integer daSuDung = 0;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;

    @Column(name = "IdNV")
    private Integer idNV; // Có thể chuyển sang @ManyToOne nếu cần ánh xạ sang entity NhanVien
}
