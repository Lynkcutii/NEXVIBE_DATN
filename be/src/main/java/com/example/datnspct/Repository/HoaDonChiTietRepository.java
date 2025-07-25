package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
//    List<HoaDonChiTiet> findByHoaDon_IdHD(Integer idHD);
}
