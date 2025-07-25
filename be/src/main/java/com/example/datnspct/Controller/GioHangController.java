package com.example.datnspct.Controller;

import com.example.datnspct.dto.GioHangDTO;
import com.example.datnspct.Service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/giohang")
public class GioHangController {
    @Autowired
    private GioHangService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<GioHangDTO> create(@RequestBody GioHangDTO dto) {
        GioHangDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<GioHangDTO> getById(@PathVariable Integer id) {
        GioHangDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<GioHangDTO>> getAll() {
        List<GioHangDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<GioHangDTO> update(@PathVariable Integer id, @RequestBody GioHangDTO dto) {
        GioHangDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 