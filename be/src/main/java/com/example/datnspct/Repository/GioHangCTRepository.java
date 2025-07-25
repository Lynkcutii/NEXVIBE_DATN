package com.example.datnspct.Repository;

import com.example.datnspct.Model.GioHangCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GioHangCTRepository extends JpaRepository<GioHangCT, Integer> {
} 