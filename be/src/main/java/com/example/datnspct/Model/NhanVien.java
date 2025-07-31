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
@Table(name = "NhanVien")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdNV")
    private Integer idNV;

    @Column(name = "MaNV", length = 50)
    private String maNV;

    @Column(name = "TenNV", length = 100)
    private String tenNV;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "SDT", length = 20)
    private String sdt;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "IdTK", insertable = false, updatable = false)
    private Integer idTK;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan;
}