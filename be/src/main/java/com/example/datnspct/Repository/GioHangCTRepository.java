package com.example.datnspct.Repository;

import com.example.datnspct.Model.GioHangCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangCTRepository extends JpaRepository<GioHangCT, Integer> {

    Optional<GioHangCT> findByGioHangIdGHAndSanPhamChiTietId(Integer idGH, Integer idSpct);

    List<GioHangCT> findByGioHangIdGH(Integer idGH);
}