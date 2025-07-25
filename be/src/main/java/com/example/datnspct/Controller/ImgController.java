package com.example.datnspct.Controller;

import com.example.datnspct.dto.ImgDTO;
import com.example.datnspct.Service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/img")
public class ImgController {
    @Autowired
    private ImgService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<ImgDTO> create(@RequestBody ImgDTO dto) {
        ImgDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ImgDTO> getById(@PathVariable Integer id) {
        ImgDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<ImgDTO>> getAll() {
        List<ImgDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<ImgDTO> update(@PathVariable Integer id, @RequestBody ImgDTO dto) {
        ImgDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 