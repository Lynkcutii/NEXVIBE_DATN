package com.example.datnspct.Controller.client;

import com.example.datnspct.Model.DanhMuc;
import com.example.datnspct.Repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/danhmuc")
public class DanhMucClientController {

//    @Autowired
//    private DanhMucRepository danhMucRepository;
//
//    @GetMapping
//    public ResponseEntity<List<DanhMuc>> getAllCategories() {
//        List<DanhMuc> danhMucList = danhMucRepository.findAll();
//        return ResponseEntity.ok(danhMucList);
//    }
}