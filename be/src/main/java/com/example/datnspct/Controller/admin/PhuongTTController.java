package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.PhuongTTService;
import com.example.datnspct.dto.PhuongTTDTO;
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
@RequestMapping("/admin/api/phuongtt")
public class PhuongTTController {
    @Autowired
    private PhuongTTService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<PhuongTTDTO> create(@RequestBody PhuongTTDTO dto) {
        PhuongTTDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PhuongTTDTO> getById(@PathVariable Integer id) {
        PhuongTTDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<PhuongTTDTO>> getAll() {
        List<PhuongTTDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<PhuongTTDTO> update(@PathVariable Integer id, @RequestBody PhuongTTDTO dto) {
        PhuongTTDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 