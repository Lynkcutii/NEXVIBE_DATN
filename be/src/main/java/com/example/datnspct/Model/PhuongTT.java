package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PhuongTT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhuongTT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPTT")
    private Integer idPTT;

    @Column(name = "Ten", length = 50)
    private String ten;
} 