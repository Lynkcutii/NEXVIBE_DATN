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

import java.time.LocalDate;

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

    @Column(name = "MaKH", length = 50, nullable = false)
    private String maKH;

    @Column(name = "TenKH", length = 100, nullable = false)
    private String tenKH;

    @Column(name = "GioiTinh", length = 10, nullable = false)
    private String gioiTinh;

    @Column(name = "NgaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "Email", length = 100, nullable = false)
    private String email;

    @Column(name = "SDT", length = 20, nullable = false)
    private String sdt;

    @Column(name = "IdTK", insertable = false, updatable = false)
    private Integer idTK;

    @Column(name = "TrangThai", nullable = false)
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan;
}