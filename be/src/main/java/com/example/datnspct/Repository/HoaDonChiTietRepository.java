package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.IdHD = :idHD")
    java.util.List<HoaDonChiTiet> findByHoaDonId(@org.springframework.data.repository.query.Param("idHD") Integer idHD);
    
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(hdct.soLuong), 0) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.IdHD = :idHD")
    int getTotalProductsByHoaDonId(@org.springframework.data.repository.query.Param("idHD") Integer idHD);
}
