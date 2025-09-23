package com.example.datnspct.Repository;

import com.example.datnspct.Model.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    Optional<SanPhamChiTiet> findById(Integer idSPCT);

    @Modifying
    @Query("UPDATE SanPhamChiTiet spct SET spct.soLuong = spct.soLuong - :soLuong WHERE spct.id = :idSPCT AND spct.soLuong >= :soLuong")
    int updateSoLuongTon(Integer idSPCT, Integer soLuong);

    List<SanPhamChiTiet> findBySanPhamId(Integer idSP);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN spct.sanPham sp " +
            "LEFT JOIN sp.danhMuc dm " +
            "LEFT JOIN sp.thuongHieu th " +
            "LEFT JOIN sp.chatLieu cl " +
            "LEFT JOIN spct.mauSac ms " +
            "LEFT JOIN spct.size s " +
            "WHERE (:keyword IS NULL OR LOWER(sp.tenSP) LIKE LOWER(:keyword) OR LOWER(spct.maSPCT) LIKE LOWER(:keyword)) " +
            "AND (:minPrice IS NULL OR spct.gia >= :minPrice) " +
            "AND (:maxPrice IS NULL OR spct.gia <= :maxPrice)")
    Page<SanPhamChiTiet> findByFilters(
            @Param("keyword") String keyword,
            @Param("danhMucIds") List<Integer> danhMucIds,
            @Param("thuongHieu") String thuongHieu,
            @Param("chatLieu") String chatLieu,
            @Param("mauSac") String mauSac,
            @Param("size") String size,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    // Đếm sản phẩm theo số lượng tồn kho
    long countBySoLuongGreaterThan(int soLuong);
    long countBySoLuong(int soLuong);
    long countBySoLuongBetween(int minSoLuong, int maxSoLuong);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE (:minPrice IS NULL OR spct.gia >= :minPrice) " +
            "AND (:maxPrice IS NULL OR spct.gia <= :maxPrice)")
    Page<SanPhamChiTiet> testFilterGia(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

}