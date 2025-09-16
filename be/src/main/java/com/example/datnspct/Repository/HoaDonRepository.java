package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    boolean existsByMaHD(String maHD);

    List<HoaDon> findByIdKhachHang(Integer idKH);

    // Đếm hóa đơn theo khoảng thời gian
    long countByNgayTaoBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tính tổng doanh thu theo khoảng thời gian
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.ngayTao BETWEEN :startDate AND :endDate AND h.trangThai = 'Hoàn thành'")
    BigDecimal sumTongTienByNgayTaoBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<HoaDon> findByKhachHangIdOrderByNgayTaoDesc(Integer idKH);

}
