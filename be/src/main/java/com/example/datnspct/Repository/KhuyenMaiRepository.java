package com.example.datnspct.Repository;

import com.example.datnspct.Model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    List<KhuyenMai> findByTrangThaiTrueAndNgayBatDauBeforeAndNgayKetThucAfter(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    List<KhuyenMai> findByKhachHangIdKHAndTrangThaiTrueAndNgayBatDauBeforeAndNgayKetThucAfter(Integer idKH, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);
}
