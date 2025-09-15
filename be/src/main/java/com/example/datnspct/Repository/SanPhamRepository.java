package com.example.datnspct.Repository;

import com.example.datnspct.Model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE (:keyword IS NULL OR LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSP) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR sp.trangThai = :status) " +
            "AND (:danhMuc IS NULL OR sp.danhMuc.idDM IN :danhMuc) " +
            "AND (:thuongHieu IS NULL OR sp.thuongHieu.idThuongHieu IN :thuongHieu) " +
            "AND (:chatLieu IS NULL OR sp.chatLieu.idChatLieu IN :chatLieu) " +
            "AND (:minPrice IS NULL OR sp.gia >= :minPrice) " +
            "AND (:maxPrice IS NULL OR sp.gia <= :maxPrice)")
    Page<SanPham> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") Boolean status,
            @Param("danhMuc") Integer[] danhMuc,
            @Param("thuongHieu") Integer[] thuongHieu,
            @Param("chatLieu") Integer[] chatLieu,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT s FROM SanPham s WHERE (:tenSP IS NULL OR LOWER(s.tenSP) LIKE LOWER(CONCAT('%', :tenSP, '%'))) AND (:status IS NULL OR s.trangThai = :status)")
    Page<SanPham> findByTenSPContainingAndTrangThai(@Param("tenSP") String tenSP, @Param("status") Boolean status, Pageable pageable);
}