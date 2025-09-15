package com.example.datnspct.Repository;


import com.example.datnspct.Model.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    // Thêm phương thức tùy chỉnh nếu cần
    Page<KhachHang> findByTenKHContainingIgnoreCaseOrSdtContaining(String tenKH, String sdt, Pageable pageable);

    Optional<KhachHang> findByTaiKhoanIdTK(Integer idTK);

    @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan.idTK = :idTK")
    Optional<KhachHang> findByIdTK(@Param("idTK") Integer idTK);

}
