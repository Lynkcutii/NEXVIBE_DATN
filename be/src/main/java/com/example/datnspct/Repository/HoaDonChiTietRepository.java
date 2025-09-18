package com.example.datnspct.Repository;

import com.example.datnspct.Model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.IdHD = :idHD")
    List<HoaDonChiTiet> findByHoaDonId(@Param("idHD") Integer idHD);
    
    @Query("SELECT COALESCE(SUM(hdct.soLuong), 0) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.IdHD = :idHD")
    int getTotalProductsByHoaDonId(@Param("idHD") Integer idHD);
    
    // Top sản phẩm bán chạy (tổng)
    @Query("SELECT sp.tenSP, SUM(hdct.soLuong), SUM(hdct.thanhTien) " +
           "FROM HoaDonChiTiet hdct " +
           "JOIN hdct.sanPhamct spct " +
           "JOIN spct.sanPham sp " +
           "JOIN hdct.hoaDon hd " +
           "WHERE hd.trangThai = 'Hoàn thành' " +
           "GROUP BY sp.tenSP, sp.id " +
           "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> findTopSanPhamBanChay(@Param("limit") int limit);
    
    // Sản phẩm bán chạy trong khoảng thời gian
    @Query("SELECT sp.tenSP, SUM(hdct.soLuong), SUM(hdct.thanhTien) " +
           "FROM HoaDonChiTiet hdct " +
           "JOIN hdct.sanPhamct spct " +
           "JOIN spct.sanPham sp " +
           "JOIN hdct.hoaDon hd " +
           "WHERE hd.trangThai = 'Hoàn thành' " +
           "AND hd.ngayTao BETWEEN :fromDate AND :toDate " +
           "GROUP BY sp.tenSP, sp.id " +
           "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> findSanPhamBanChayTrongKhoang(@Param("fromDate") LocalDateTime fromDate, 
                                                 @Param("toDate") LocalDateTime toDate);
    
    // Thống kê theo danh mục
    @Query("SELECT dm.tenDM, SUM(hdct.soLuong), SUM(hdct.thanhTien) " +
           "FROM HoaDonChiTiet hdct " +
           "JOIN hdct.sanPhamct spct " +
           "JOIN spct.danhMuc dm " +
           "JOIN hdct.hoaDon hd " +
           "WHERE hd.trangThai = 'Hoàn thành' " +
           "AND hd.ngayTao BETWEEN :fromDate AND :toDate " +
           "GROUP BY dm.tenDM, dm.idDM " +
           "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> findThongKeTheoDanhMuc(@Param("fromDate") LocalDateTime fromDate, 
                                          @Param("toDate") LocalDateTime toDate);
}
