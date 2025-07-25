package com.example.datnspct.Repository;


import com.example.datnspct.Model.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    // Thêm phương thức tùy chỉnh nếu cần
    Page<KhachHang> findByTenKHContainingIgnoreCaseOrSdtContaining(String tenKH, String sdt, Pageable pageable);
}
