package com.example.datnspct.Repository;

import com.example.datnspct.Model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    @Query("SELECT sp FROM SanPham sp " +
            "WHERE (:keyword IS NULL OR sp.tenSP LIKE %:keyword% OR sp.maSP LIKE %:keyword%) " +
            "AND (:status IS NULL OR sp.trangThai = :status)")
    Page<SanPham> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") Boolean status,
            Pageable pageable);

    @Query("SELECT s FROM SanPham s WHERE (:tenSP IS NULL OR s.tenSP LIKE %:tenSP%) AND (:status IS NULL OR s.trangThai = :status)")
    Page<SanPham> findByTenSPContainingAndTrangThai(@Param("tenSP") String tenSP, @Param("status") Boolean status, Pageable pageable);
}
