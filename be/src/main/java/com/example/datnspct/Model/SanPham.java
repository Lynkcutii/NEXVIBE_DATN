package com.example.datnspct.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SanPham")
@Data
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSP")
    private Integer id;

    @Column(name = "MaSP")
    private String maSP;

    @Column(name = "TenSP")
    private String tenSP;

    // ----- CÁC TRƯỜNG ĐƯỢC THÊM VÀO CHO ĐÚNG VỚI CSDL -----
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdThuongHieu")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdDM")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdChatLieu")
    private ChatLieu chatLieu;

    @Column(name = "Img")
    private String img; // Ảnh đại diện cho sản phẩm
    // --------------------------------------------------------

    @Column(name = "NgayTao")
    private Date ngayTao;

    @Column(name = "TongSoLuongSanPham")
    private Integer tongSoLuongSanPham;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @Column(name = "Gia")
    private BigDecimal gia;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    @JsonIgnore // Ngăn serialize lặp vô hạn
    private List<SanPhamChiTiet> sanPhamChiTiets;

}