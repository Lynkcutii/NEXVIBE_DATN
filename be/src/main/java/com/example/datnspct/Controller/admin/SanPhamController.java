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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/admin/api/san-pham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<SanPhamDTO> taoSanPham(@RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO sanPhamDaTao = sanPhamService.taoSanPham(sanPhamDTO);
        return ResponseEntity.ok(sanPhamDaTao);
    }

    // Lấy tất cả với phân trang và bộ lọc
    @GetMapping
    public ResponseEntity<Page<SanPhamDTO>> getAllSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tenSP,
            @RequestParam(required = false) Boolean status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamDTO> sanPhamPage = sanPhamService.findAll(tenSP, status, pageable);
        return ResponseEntity.ok(sanPhamPage);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> laySanPhamTheoId(@PathVariable Integer id) {
        SanPhamDTO sanPhamDTO = sanPhamService.laySanPhamTheoId(id);
        return ResponseEntity.ok(sanPhamDTO);
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