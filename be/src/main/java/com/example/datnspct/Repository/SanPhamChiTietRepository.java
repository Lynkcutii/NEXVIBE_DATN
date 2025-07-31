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
            "LEFT JOIN spct.danhMuc dm " +
            "LEFT JOIN spct.thuongHieu th " +
            "LEFT JOIN spct.mauSac ms " +
            "LEFT JOIN spct.chatLieu cl " +
            "LEFT JOIN spct.size s " +
            "WHERE (:keyword IS NULL OR sp.tenSP LIKE %:keyword% OR spct.maSPCT LIKE %:keyword%) " +
            "AND (:danhMucIds IS NULL OR spct.danhMuc.idDM IN :danhMucIds) " +
            "AND (:thuongHieu IS NULL OR th.tenThuongHieu = :thuongHieu) " +
            "AND (:mauSac IS NULL OR ms.tenMauSac = :mauSac) " +
            "AND (:chatLieu IS NULL OR cl.tenChatLieu = :chatLieu) " +
            "AND (:size IS NULL OR s.tenSize = :size) " +
            "AND (:minPrice IS NULL OR spct.gia >= :minPrice) " +
            "AND (:maxPrice IS NULL OR spct.gia <= :maxPrice)")
    Page<SanPhamChiTiet> findByFilters(
            @Param("keyword") String keyword,
            @Param("danhMucIds") List<Integer> danhMucIds,
            @Param("thuongHieu") String thuongHieu,
            @Param("mauSac") String mauSac,
            @Param("chatLieu") String chatLieu,
            @Param("size") String size,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}