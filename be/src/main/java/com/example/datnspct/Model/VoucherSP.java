package com.example.datnspct.Model;

import jakarta.persistence.*;
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
    private SanPhamChiTiet sanPhamCT;

    @Column(name = "TrangThai")
    private Byte trangThai;
}