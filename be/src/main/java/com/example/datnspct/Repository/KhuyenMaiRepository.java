package com.example.datnspct.Repository;

import com.example.datnspct.Model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    // Lấy tất cả KM đang active trong thời gian hợp lệ
    List<KhuyenMai> findByTrangThaiTrueAndNgayBatDauBeforeAndNgayKetThucAfter(
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc
    );
    // ✅ Dùng JPQL để join qua bảng trung gian KhuyenMai_KhachHang
    @Query("SELECT km FROM KhuyenMai km " +
            "JOIN km.khachHangs kh " +
            "WHERE (kh IS NULL OR kh.idKH = :idKH) " +
            "AND km.trangThai = true " +
            "AND km.ngayBatDau <= :now " +
            "AND km.ngayKetThuc >= :now")
    List<KhuyenMai> findApplicableByKhachHang(
            @Param("idKH") Integer idKH,
            @Param("now") LocalDateTime now
    );
}
