package com.example.datnspct.Repository;

import com.example.datnspct.Model.Img;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<Img, Integer> {
    @Query("SELECT i FROM Img i WHERE i.sanPhamChiTiet.id = :idSPCT ORDER BY i.idImg ASC")
    List<Img> findBySanPhamChiTietId(@Param("idSPCT") Integer idSPCT);

    @Query("SELECT i FROM Img i WHERE i.sanPhamChiTiet.id = :idSPCT ORDER BY i.idImg ASC LIMIT 1")
    Img findFirstBySanPhamChiTietId(@Param("idSPCT") Integer idSPCT);

    void deleteBySanPhamChiTietId(Integer idSPCT);
}