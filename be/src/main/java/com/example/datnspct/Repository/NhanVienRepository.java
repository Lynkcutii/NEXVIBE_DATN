package com.example.datnspct.Repository;

import com.example.datnspct.Model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByTaiKhoanIdTK(Integer idTK);
} 