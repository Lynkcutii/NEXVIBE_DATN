package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "IdTK", insertable = false, updatable = false)
    private Integer idTK; // Read-only mapping for IdTK

    @Column(name = "Email")
    private String email;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan; // Handles insert/update for IdTK
}