package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "TaiKhoan")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdTK")
    private Integer idTK;

    @Column(name = "TaiKhoan", length = 100)
    private String taiKhoan;

    @Column(name = "MatKhau", length = 100)
    private String matKhau;

    @Column(name = "ChucVu", length = 50)
    private String chucVu;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
