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
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN spct.sanPham sp " +
            "WHERE (:keyword IS NULL OR LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(spct.maSPCT) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:trangThai IS NULL OR spct.trangThai = :trangThai)")
    Page<SanPhamChiTiet> searchByKeywordAndTrangThai(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "LEFT JOIN spct.sanPham sp " +
            "LEFT JOIN spct.mauSac ms " +
            "LEFT JOIN spct.size s " +
            "WHERE (:keyword IS NULL OR LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(spct.maSPCT) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:mauSac IS NULL OR LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', :mauSac, '%'))) " +
            "AND (:size IS NULL OR LOWER(s.tenSize) LIKE LOWER(CONCAT('%', :size, '%'))) " +
            "AND (:minPrice IS NULL OR spct.gia >= :minPrice) " +
            "AND (:maxPrice IS NULL OR spct.gia <= :maxPrice) " +
            "AND spct.trangThai = true")
    Page<SanPhamChiTiet> findByFilters(
            @Param("keyword") String keyword,
            @Param("mauSac") String mauSac,
            @Param("size") String size,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    Optional<SanPhamChiTiet> findById(Integer idSPCT);

    @Modifying
    @Query("UPDATE SanPhamChiTiet spct SET spct.soLuong = spct.soLuong - :soLuong WHERE spct.id = :idSPCT AND spct.soLuong >= :soLuong")
    int updateSoLuongTon(@Param("idSPCT") Integer idSPCT, @Param("soLuong") Integer soLuong);

    List<SanPhamChiTiet> findBySanPhamId(Integer idSP);

    Optional<SanPhamChiTiet> findByMaSPCT(String maSPCT);

    long countBySoLuongGreaterThan(int soLuong);
    long countBySoLuong(int soLuong);
    long countBySoLuongBetween(int minSoLuong, int maxSoLuong);
}