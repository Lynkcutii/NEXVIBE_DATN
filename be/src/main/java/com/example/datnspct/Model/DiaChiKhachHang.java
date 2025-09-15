package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DiaChiKhachHang")
@Data
public class DiaChiKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDiaChi;

    @ManyToOne
    @JoinColumn(name = "IdKH", nullable = false)
    private KhachHang khachHang;

    @Column(name = "DiaChiCuThe", nullable = false)
    private String diaChiCuThe;

    @Column(name = "TinhThanh", nullable = false)
    private String tinhThanh;

    @Column(name = "PhuongXa", nullable = false)
    private String phuongXa;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "TrangThai")
    private Integer trangThai;

}
