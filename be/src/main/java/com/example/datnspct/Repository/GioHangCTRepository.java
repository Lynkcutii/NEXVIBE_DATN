package com.example.datnspct.Repository;

import com.example.datnspct.Model.GioHangCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangCTRepository extends JpaRepository<GioHangCT, Integer> {

    Optional<GioHangCT> findByGioHangIdGHAndSanPhamChiTietId(Integer idGH, Integer idSpct);

    List<GioHangCT> findByGioHangIdGH(Integer idGH);

    @Modifying
    @Transactional
    @Query("DELETE FROM GioHangCT g WHERE g.idGHCT = :idGHCT")
    void deleteByIdCustom(@Param("idGHCT") Integer idGHCT);

    @Query("SELECT COUNT(g) > 0 FROM GioHangCT g WHERE g.idGHCT = :idGHCT")
    boolean existsByIdCustom(@Param("idGHCT") Integer idGHCT);
}