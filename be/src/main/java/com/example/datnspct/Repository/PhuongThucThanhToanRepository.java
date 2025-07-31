package com.example.datnspct.Repository;

import com.example.datnspct.Model.PhuongTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongTT, Integer> {
    Optional<PhuongTT> findByTen(String ten);

    Optional<PhuongTT> findByIdPTT(Integer idPTT);
}
