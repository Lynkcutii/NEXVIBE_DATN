package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    boolean existsByMaHD(String maHD);
}
