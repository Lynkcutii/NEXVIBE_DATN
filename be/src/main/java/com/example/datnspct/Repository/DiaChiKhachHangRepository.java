package com.example.datnspct.Repository;

import com.example.datnspct.Model.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, Integer> {
    List<DiaChiKhachHang> findByKhachHangIdKH(Integer idKH);
    void deleteByKhachHangIdKH(Integer idKH);
}
