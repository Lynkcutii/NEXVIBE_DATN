package com.example.datnspct.Controller;

import com.example.datnspct.dto.GioHangCTDTO;
import com.example.datnspct.Service.GioHangCTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/giohangct")
public class GioHangCTController {
    @Autowired
    private GioHangCTService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<GioHangCTDTO> create(@RequestBody GioHangCTDTO dto) {
        GioHangCTDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<GioHangCTDTO> getById(@PathVariable Integer id) {
        GioHangCTDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<GioHangCTDTO>> getAll() {
        List<GioHangCTDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<GioHangCTDTO> update(@PathVariable Integer id, @RequestBody GioHangCTDTO dto) {
        GioHangCTDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 