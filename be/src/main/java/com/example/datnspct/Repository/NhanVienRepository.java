package com.example.datnspct.Repository;

import com.example.datnspct.Model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByTaiKhoanIdTK(Integer idTK);

    Optional<Object> findByMaNV(String maNV);

    @Query("SELECT n FROM NhanVien n WHERE " +
            "(:trangThai IS NULL OR n.trangThai = :trangThai) AND " +
            "(:keyword IS NULL OR n.tenNV LIKE %:keyword% OR n.sdt LIKE %:keyword%)")
    List<NhanVien> findByTrangThaiAndTenNVOrSdt(@Param("trangThai") Boolean trangThai,
                                                @Param("keyword") String keyword);
} 