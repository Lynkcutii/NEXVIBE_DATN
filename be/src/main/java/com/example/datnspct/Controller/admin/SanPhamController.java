package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SanPhamService;
import com.example.datnspct.dto.SanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/sanpham")
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