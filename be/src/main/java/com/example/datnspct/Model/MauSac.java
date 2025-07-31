package com.example.datnspct.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MauSac")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdMauSac")
    private Integer idMauSac;

    @Column(name = "MaMauSac", length = 50)
    private String maMauSac;

    @Column(name = "TenMauSac", length = 100)
    private String tenMauSac;

    @Column(name = "TrangThai")
    private Boolean trangThai;
} 