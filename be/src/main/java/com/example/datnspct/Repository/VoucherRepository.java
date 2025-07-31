package com.example.datnspct.Repository;

import com.example.datnspct.Model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    @Query("SELECT v FROM Voucher v " +
            "JOIN VoucherSP vs ON v.idVoucher = vs.voucher.idVoucher " +
            "WHERE vs.sanPhamChiTiet.id = :idSPCT " +
            "AND v.trangThai = 1 " +
            "AND v.ngayBatDau <= :currentDate " +
            "AND v.ngayKetThuc >= :currentDate")
    List<Voucher> findApplicableVouchersBySanPhamChiTietId(@Param("idSPCT") Integer idSPCT, @Param("currentDate") Date currentDate);
}