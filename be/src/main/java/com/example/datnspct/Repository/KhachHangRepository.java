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

    // Phương thức tìm kiếm khách hàng theo tên hoặc sdt (cho việc phân trang)
    Page<KhachHang> findByTenKHContainingIgnoreCaseOrSdtContaining(String tenKH, String sdt, Pageable pageable);

    // Tìm kiếm khách hàng theo tên, số điện thoại và trạng thái
    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "(:keyword IS NULL OR kh.tenKH LIKE %:keyword% OR kh.sdt LIKE %:keyword%) AND " +
            "(:trangThai IS NULL OR kh.trangThai = :trangThai)")
    Page<KhachHang> findByTenKHContainingIgnoreCaseOrSdtContainingAndTrangThai(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);

    // Phương thức tìm khách hàng bằng ID của tài khoản liên kết
    // (Đây là cách chuẩn của Spring Data JPA, không cần @Query)
    Optional<KhachHang> findByTaiKhoanId(Integer idTK);

    // Đếm khách hàng có ít nhất một hóa đơn
    @Query("SELECT COUNT(DISTINCT kh) FROM KhachHang kh WHERE kh.hoaDons IS NOT EMPTY")

    Optional<KhachHang> findByIdTK(Integer idTK);

    // Đếm khách hàng có hóa đơn
    @Query("SELECT COUNT(DISTINCT kh.idKH) FROM KhachHang kh WHERE EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.khachHang.idKH = kh.idKH)")

    long countKhachHangCoHoaDon();

    // Đếm khách hàng có hóa đơn trong một khoảng thời gian
    @Query("SELECT COUNT(DISTINCT kh) FROM KhachHang kh JOIN kh.hoaDons hd WHERE hd.ngayTao BETWEEN :startDate AND :endDate")
    long countKhachHangCoHoaDonTrongKhoang(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Tìm khách hàng mới dựa trên ngày tạo tài khoản
    // Giả định KhachHang có quan hệ tới TaiKhoan và TaiKhoan có thuộc tính
    // 'ngayTao'
    @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan.ngayTao BETWEEN :fromDate AND :toDate ORDER BY kh.taiKhoan.ngayTao DESC")
    List<KhachHang> findKhachHangMoi(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    // Tìm khách hàng VIP (chi tiêu nhiều nhất)
    // Giả định HoaDon có thuộc tính trangThai là String và có giá trị 'Hoàn thành'
    @Query("SELECT kh, SUM(hd.tongTien) as totalSpending " +
            "FROM KhachHang kh JOIN kh.hoaDons hd " +
            "WHERE hd.trangThai = 'Hoàn thành' " +
            "GROUP BY kh " +
            "ORDER BY totalSpending DESC")
    List<Object[]> findKhachHangVIP();

    // Nếu bạn cần tìm khách hàng theo ID tài khoản bằng @Query
    // @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan.id = :idTK")
    // Optional<KhachHang> findKhachHangByTaiKhoanId(@Param("idTK") Integer idTK);

    @Query("SELECT COUNT(kh) FROM KhachHang kh WHERE kh.maKH LIKE :prefix%")

    long countByMaKHStartingWith(@Param("prefix") String prefix);
}