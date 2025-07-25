package com.example.datnspct.Controller;

import com.example.datnspct.dto.ChatLieuDTO;
import com.example.datnspct.Service.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatlieu")
public class ChatLieuController {
    @Autowired
    private ChatLieuService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<ChatLieuDTO> create(@RequestBody ChatLieuDTO dto) {
        ChatLieuDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> getById(@PathVariable Integer id) {
        ChatLieuDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<ChatLieuDTO>> getAll() {
        List<ChatLieuDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> update(@PathVariable Integer id, @RequestBody ChatLieuDTO dto) {
        ChatLieuDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 