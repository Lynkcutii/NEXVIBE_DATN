package com.example.datnspct.Repository;

import com.example.datnspct.Model.Img;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgRepository extends JpaRepository<Img, Integer> {
} 