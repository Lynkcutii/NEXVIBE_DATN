package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.KhachHangService;
import com.example.datnspct.dto.KhachHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/admin/api/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<KhachHangDTO> taoKhachHang(@RequestBody KhachHangDTO khachHangDTO) {
        KhachHangDTO khachHangDaTao = khachHangService.taoKhachHang(khachHangDTO);
        return ResponseEntity.ok(khachHangDaTao);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<KhachHangDTO> layKhachHangTheoId(@PathVariable Integer id) {
        KhachHangDTO khachHangDTO = khachHangService.layKhachHangTheoId(id);
        return ResponseEntity.ok(khachHangDTO);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<Page<KhachHangDTO>> layTatCaKhachHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHangDTO> khachHangPage;
        if (search != null && !search.isEmpty()) {
            khachHangPage = khachHangService.searchKhachHang(search, pageable);
        } else {
            khachHangPage = khachHangService.layTatCaKhachHang(pageable);
        }
        return ResponseEntity.ok(khachHangPage);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<KhachHangDTO> capNhatKhachHang(@PathVariable Integer id, @RequestBody KhachHangDTO khachHangDTO) {
        KhachHangDTO khachHangDaCapNhat = khachHangService.capNhatKhachHang(id, khachHangDTO);
        return ResponseEntity.ok(khachHangDaCapNhat);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaKhachHang(@PathVariable Integer id) {
        khachHangService.xoaKhachHang(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dia-chi")
    public ResponseEntity<String> layDiaChiKhachHang(@PathVariable Integer id) {
        KhachHangDTO khachHang = khachHangService.layKhachHangTheoId(id);
        return ResponseEntity.ok(khachHang.getDiaChi());
    }
}