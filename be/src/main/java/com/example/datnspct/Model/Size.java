package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Size")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSize")
    private Integer idSize;

    @Column(name = "MaSize", length = 50)
    private String maSize;

    @Column(name = "TenSize", length = 50)
    private String tenSize;

    @Column(name = "TrangThai")
    private Boolean trangThai;
} 