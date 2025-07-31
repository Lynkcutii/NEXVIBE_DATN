package com.example.datnspct.Repository.login;

import com.example.datnspct.Model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan,Integer> {
    Optional<TaiKhoan> findByTaiKhoan(String taiKhoan);
}
