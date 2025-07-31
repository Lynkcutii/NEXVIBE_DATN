package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.DanhMucService;
import com.example.datnspct.dto.DanhMucDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/danhmuc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public ResponseEntity<List<DanhMucDTO>> layTatCaDanhMuc() {
        List<DanhMucDTO> danhMucs = danhMucService.getAll();
        return ResponseEntity.ok(danhMucs);
    }
}