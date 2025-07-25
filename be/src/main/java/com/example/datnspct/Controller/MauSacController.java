package com.example.datnspct.Controller;

import com.example.datnspct.dto.MauSacDTO;
import com.example.datnspct.Service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mausac")
public class MauSacController {
    @Autowired
    private MauSacService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<MauSacDTO> create(@RequestBody MauSacDTO dto) {
        MauSacDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MauSacDTO> getById(@PathVariable Integer id) {
        MauSacDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<MauSacDTO>> getAll() {
        List<MauSacDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<MauSacDTO> update(@PathVariable Integer id, @RequestBody MauSacDTO dto) {
        MauSacDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 