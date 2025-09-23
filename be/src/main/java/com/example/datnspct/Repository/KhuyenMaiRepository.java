package com.example.datnspct.Repository;

import com.example.datnspct.Model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    @Query("SELECT km FROM KhuyenMai km JOIN km.customers c " +
            "WHERE c.idKH = :idKH " +
            "AND km.trangThai = true " +
            "AND km.ngayBatDau <= :now " +
            "AND km.ngayKetThuc >= :now")
    List<KhuyenMai> findByCustomer(@Param("idKH") Integer idKH, @Param("now") LocalDateTime now);

    @Query("SELECT km FROM KhuyenMai km " +
            "WHERE SIZE(km.customers) = 0 " +
            "AND km.trangThai = true " +
            "AND km.ngayBatDau <= :now " +
            "AND km.ngayKetThuc >= :now")
    List<KhuyenMai> findGlobal(@Param("now") LocalDateTime now);
}
