package com.example.datnspct.Controller;

import com.example.datnspct.dto.SanPhamDTO;
import com.example.datnspct.Service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<SanPhamDTO> taoSanPham(@RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO sanPhamDaTao = sanPhamService.taoSanPham(sanPhamDTO);
        return ResponseEntity.ok(sanPhamDaTao);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> laySanPhamTheoId(@PathVariable Integer id) {
        SanPhamDTO sanPhamDTO = sanPhamService.laySanPhamTheoId(id);
        return ResponseEntity.ok(sanPhamDTO);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<SanPhamDTO>> layTatCaSanPham() {
        List<SanPhamDTO> sanPhamDTOs = sanPhamService.layTatCaSanPham();
        return ResponseEntity.ok(sanPhamDTOs);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<SanPhamDTO> capNhatSanPham(@PathVariable Integer id, @RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO sanPhamDaCapNhat = sanPhamService.capNhatSanPham(id, sanPhamDTO);
        return ResponseEntity.ok(sanPhamDaCapNhat);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPham(@PathVariable Integer id) {
        sanPhamService.xoaSanPham(id);
        return ResponseEntity.noContent().build();
    }
}