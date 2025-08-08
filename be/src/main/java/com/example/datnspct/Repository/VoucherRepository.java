package com.example.datnspct.Repository;

import com.example.datnspct.Model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    List<Voucher> findAllByTrangThaiAndSoLuongGreaterThan(Byte trangThai, Integer soLuong);
    List<Voucher> findAllByTrangThai(Byte trangThai);
}