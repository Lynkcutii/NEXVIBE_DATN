package com.example.datnspct.Repository;

import com.example.datnspct.Model.PhuongTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongTTRepository extends JpaRepository<PhuongTT, Integer> {
} 