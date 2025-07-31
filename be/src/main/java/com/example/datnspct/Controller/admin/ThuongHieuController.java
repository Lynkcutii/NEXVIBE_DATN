package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.ThuongHieu;
import com.example.datnspct.Repository.ThuongHieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/thuonghieu")
public class ThuongHieuController {

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @GetMapping
    public ResponseEntity<List<ThuongHieu>> layTatCaThuongHieu() {
        List<ThuongHieu> thuongHieus = thuongHieuRepository.findAll();
        return ResponseEntity.ok(thuongHieus);
    }
}