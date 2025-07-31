package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mausac")
public class MauSacController {

    @Autowired
    private MauSacRepository mauSacRepository;

    @GetMapping
    public ResponseEntity<List<MauSac>> layTatCaMauSac() {
        List<MauSac> mauSacs = mauSacRepository.findAll();
        return ResponseEntity.ok(mauSacs);
    }
}