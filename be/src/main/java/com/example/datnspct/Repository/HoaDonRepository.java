package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    boolean existsByMaHD(String maHD);
    List<HoaDon> findByIdKhachHang(Integer idKH);
}

