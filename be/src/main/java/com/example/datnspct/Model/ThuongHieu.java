package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ThuongHieu")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdThuongHieu")
    private Integer idThuongHieu;

    @Column(name = "MaThuongHieu", length = 50)
    private String maThuongHieu;

    @Column(name = "TenThuongHieu", length = 100)
    private String tenThuongHieu;

    @Column(name = "TrangThai")
    private Boolean trangThai;

}
