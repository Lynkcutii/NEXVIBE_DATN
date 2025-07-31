package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.ImgService;
import com.example.datnspct.dto.ImgDTO;
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