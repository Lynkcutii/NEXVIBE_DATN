package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SizeService;
import com.example.datnspct.dto.SizeDTO;
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
@RequestMapping("/api/size")
public class SizeController {
    @Autowired
    private SizeService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<SizeDTO> create(@RequestBody SizeDTO dto) {
        SizeDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SizeDTO> getById(@PathVariable Integer id) {
        SizeDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<SizeDTO>> getAll() {
        List<SizeDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<SizeDTO> update(@PathVariable Integer id, @RequestBody SizeDTO dto) {
        SizeDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 