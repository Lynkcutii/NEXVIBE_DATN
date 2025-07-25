package com.example.datnspct.Controller;

import com.example.datnspct.dto.SanPhamChiTietDTO;
import com.example.datnspct.Service.SanPhamChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanphamchitiet")
public class SanPhamChiTietController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<SanPhamChiTietDTO> taoSanPhamChiTiet(@RequestBody SanPhamChiTietDTO sanPhamChiTietDTO) {
        SanPhamChiTietDTO sanPhamChiTietDaTao = sanPhamChiTietService.taoSanPhamChiTiet(sanPhamChiTietDTO);
        return ResponseEntity.ok(sanPhamChiTietDaTao);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamChiTietDTO> laySanPhamChiTietTheoId(@PathVariable Integer id) {
        SanPhamChiTietDTO sanPhamChiTietDTO = sanPhamChiTietService.laySanPhamChiTietTheoId(id);
        return ResponseEntity.ok(sanPhamChiTietDTO);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<SanPhamChiTietDTO>> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTietDTO> sanPhamChiTietDTOs = sanPhamChiTietService.layTatCaSanPhamChiTiet();
        return ResponseEntity.ok(sanPhamChiTietDTOs);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<SanPhamChiTietDTO> capNhatSanPhamChiTiet(@PathVariable Integer id, @RequestBody SanPhamChiTietDTO sanPhamChiTietDTO) {
        SanPhamChiTietDTO sanPhamChiTietDaCapNhat = sanPhamChiTietService.capNhatSanPhamChiTiet(id, sanPhamChiTietDTO);
        return ResponseEntity.ok(sanPhamChiTietDaCapNhat);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPhamChiTiet(@PathVariable Integer id) {
        sanPhamChiTietService.xoaSanPhamChiTiet(id);
        return ResponseEntity.noContent().build();
    }
}