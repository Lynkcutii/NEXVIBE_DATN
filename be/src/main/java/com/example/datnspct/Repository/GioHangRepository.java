package com.example.datnspct.Repository;

import com.example.datnspct.Model.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    Optional<GioHang> findByTaiKhoanIdTK(Integer idTK);
} 