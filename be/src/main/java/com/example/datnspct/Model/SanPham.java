package com.example.datnspct.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "SanPham")
@Getter
@Setter
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSP")
    private Integer id;

    @Column(name = "MaSP")
    private String maSP;

    @Column(name = "TenSP")
    private String tenSP;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdDM")
    private DanhMuc danhMuc;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    @JsonIgnore // Ngăn serialize danh sách SanPhamChiTiet
    private List<SanPhamChiTiet> sanPhamChiTiets;
}