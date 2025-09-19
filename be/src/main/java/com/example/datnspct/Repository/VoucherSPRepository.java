package com.example.datnspct.Repository;

import com.example.datnspct.Model.VoucherSP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherSPRepository extends JpaRepository<VoucherSP, Integer> {
    List<VoucherSP> findByVoucherIdVoucherAndTrangThai(Integer idVoucher, Byte trangThai);

    // Xóa tất cả VoucherSP theo idVoucher
    void deleteByVoucherIdVoucher(Integer idVoucher);
}