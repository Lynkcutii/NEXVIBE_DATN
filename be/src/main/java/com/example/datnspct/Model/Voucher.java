package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Voucher")
@Getter
@Setter
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdVoucher")
    private Integer idVoucher;

    @Column(name = "MaVoucher", nullable = false)
    private String maVoucher;

    @Column(name = "TenVoucher", nullable = false)
    private String tenVoucher;

    @Column(name = "NgayBatDau", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "HinhThucGiam", nullable = false)
    private String hinhThucGiam;

    @Column(name = "GiamToiDa")
    private BigDecimal giamToiDa;

    @Column(name = "MucGiam")
    private BigDecimal mucGiam;

    @Column(name = "DonGiaKhiGiam")
    private BigDecimal donGiaKhiGiam;

    @Column(name = "GiaGiam")
    private BigDecimal giaGiam;

    @Column(name = "TrangThai")
    private Byte trangThai;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    private List<VoucherSP> voucherSPs;
}