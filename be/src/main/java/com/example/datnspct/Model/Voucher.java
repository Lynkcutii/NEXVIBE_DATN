package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

    @Column(name = "TenVoucher")
    private String tenVoucher;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "TrangThai")
    private Byte trangThai;

    @Column(name = "MucGiam")
    private BigDecimal mucGiam;

    @Column(name = "DonGiaKhiGiam")
    private BigDecimal donGiaKhiGiam;

    @Column(name = "GiaGiam")
    private BigDecimal giaGiam;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    private List<VoucherSP> voucherSPs;
}