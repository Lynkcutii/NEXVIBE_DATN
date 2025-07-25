package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "KhachHang")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdKH")
    private Integer idKH;

    @Column(name = "MaKH", length = 50)
    private String maKH;

    @Column(name = "TenKH", length = 100)
    private String tenKH;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "SDT", length = 20)
    private String sdt;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @ManyToOne
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
