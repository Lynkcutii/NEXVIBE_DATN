package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Voucher_SP")
@Getter
@Setter
public class VoucherSP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdVoucher_SP")
    private Integer idVoucherSP;

    @ManyToOne
    @JoinColumn(name = "IdVoucher")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "IdSPCT")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "TrangThai")
    private Byte trangThai;
}
