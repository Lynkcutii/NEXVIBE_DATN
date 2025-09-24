package com.example.datnspct.Repository;

import com.example.datnspct.Model.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    // Tìm kiếm khách hàng theo tên, số điện thoại và trạng thái
    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "(:keyword IS NULL OR kh.tenKH LIKE %:keyword% OR kh.sdt LIKE %:keyword%) AND " +
            "(:trangThai IS NULL OR kh.trangThai = :trangThai)")
    Page<KhachHang> findByTenKHContainingIgnoreCaseOrSdtContainingAndTrangThai(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);

    Optional<KhachHang> findByTaiKhoanIdTK(Integer idTK);

    Optional<KhachHang> findByIdTK(Integer idTK);

    // Đếm khách hàng có hóa đơn
    @Query("SELECT COUNT(DISTINCT kh.idKH) FROM KhachHang kh WHERE EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.khachHang.idKH = kh.idKH)")
    long countKhachHangCoHoaDon();

    // Đếm khách hàng có hóa đơn trong khoảng thời gian
    @Query("SELECT COUNT(DISTINCT kh.idKH) FROM KhachHang kh WHERE EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.khachHang.idKH = kh.idKH AND hd.ngayTao BETWEEN :startDate AND :endDate)")
    long countKhachHangCoHoaDonTrongKhoang(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Khách hàng mới (dựa trên tài khoản được tạo gần đây)
    @Query("SELECT kh.idKH, kh.tenKH, kh.email, kh.idKH " +
            "FROM KhachHang kh " +
            "ORDER BY kh.idKH DESC")
    List<Object[]> findKhachHangMoi(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate);

    // Khách hàng VIP (chi tiêu nhiều)
    @Query("SELECT kh.idKH, kh.tenKH, COUNT(hd.idHD), COALESCE(SUM(hd.tongTien), 0) " +
            "FROM KhachHang kh " +
            "LEFT JOIN HoaDon hd ON hd.khachHang.idKH = kh.idKH AND hd.trangThai = 'Hoàn thành' " +
            "GROUP BY kh.idKH, kh.tenKH " +
            "HAVING COUNT(hd.idHD) > 0 " +
            "ORDER BY SUM(hd.tongTien) DESC")
    List<Object[]> findKhachHangVIP();

    @Query("SELECT COUNT(kh) FROM KhachHang kh WHERE kh.maKH LIKE :prefix%")
    long countByMaKHStartingWith(@Param("prefix") String prefix);
}