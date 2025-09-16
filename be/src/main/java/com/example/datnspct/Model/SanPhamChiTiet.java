package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPhamCT")
@Getter
@Setter
@Data // Lưu ý: Dùng @Getter @Setter rồi thì @Data là không cần thiết, nhưng để cũng
      // không sao
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSPCT")
    private Integer id;

    @Column(name = "MaSPCT")
    private String maSPCT;

    @ManyToOne
    @JoinColumn(name = "IdSP")
    private SanPham sanPham;

    @Column(name = "Gia")
    private BigDecimal gia;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "IdThuongHieu")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "IdMauSac")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "IdChatLieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "IdSize")
    private Size size;

    // Chỉ giữ lại một khai báo duy nhất và kết hợp các thuộc tính
    @OneToMany(mappedBy = "sanPhamChiTiet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Img> images;
}