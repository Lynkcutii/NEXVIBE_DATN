package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "GioHang")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGH")
    private Integer idGH;

    @Column(name = "MaGH", length = 50)
    private String maGH;

    @ManyToOne
    @JoinColumn(name = "IdKH", referencedColumnName = "IdKH")
    private KhachHang khachHang;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan;
} 