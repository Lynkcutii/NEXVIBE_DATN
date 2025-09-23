package com.example.datnspct.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHD;

    @Column(name = "MaHD", length = 50)
    private String maHD;

    // Mapping NhanVien (nhân viên lập hóa đơn)
    @ManyToOne
    @JoinColumn(name = "IdNV", referencedColumnName = "IdNV")
    private NhanVien nhanVien;

    @Column(name = "IdNV",insertable = false, updatable = false)
    private Integer idNhanVien;

    // Mapping KhachHang (khách hàng)
    @ManyToOne
    @JoinColumn(name = "IdKH", referencedColumnName = "IdKH")
    private KhachHang khachHang;

    // Nếu muốn lấy idKhachHang nhanh, dùng thêm field này (chỉ đọc)
    @Column(name = "IdKH", insertable = false, updatable = false)
    private Integer idKhachHang;

    // Mapping KhuyenMai (khuyến mãi)
    @ManyToOne
    @JoinColumn(name = "IdKM", referencedColumnName = "IdKM")
    private KhuyenMai khuyenMai;

    @Column(name = "IdKM", insertable = false, updatable = false)
    private Integer idKM;

    // Mapping phương thức thanh toán
    @ManyToOne
    @JoinColumn(name = "IdPT", referencedColumnName = "IdPT")
    private PhuongTT phuongThucThanhToan;

    @Column(name = "LoaiHoaDon")
    private String loaiHoaDon;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TrangThai")
    private String trangThai;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> chiTietSanPham = new ArrayList<>();
}