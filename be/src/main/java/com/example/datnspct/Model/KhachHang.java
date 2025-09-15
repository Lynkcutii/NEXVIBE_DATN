package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "KhachHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "SDT", length = 20)
    private String sdt;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    // Chỉ giữ lại mối quan hệ @ManyToOne, bỏ trường idTK riêng lẻ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdTK", referencedColumnName = "IdTK")
    private TaiKhoan taiKhoan;
}