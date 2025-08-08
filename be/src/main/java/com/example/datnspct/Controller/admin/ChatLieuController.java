package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.ChatLieuService;
import com.example.datnspct.dto.ChatLieuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatlieu")
public class ChatLieuController {

    @Autowired
    private ChatLieuService chatLieuService;

    // Lấy tất cả chất liệu
    @GetMapping
    public ResponseEntity<List<ChatLieuDTO>> getAllChatLieu() {
        List<ChatLieuDTO> chatLieuList = chatLieuService.getAll();
        return ResponseEntity.ok(chatLieuList);
    }

    // Lấy chất liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> getById(@PathVariable Integer id) {
        ChatLieuDTO chatLieu = chatLieuService.getById(id);
        return ResponseEntity.ok(chatLieu);
    }

    // Thêm chất liệu mới
    @PostMapping
    public ResponseEntity<ChatLieuDTO> create(@RequestBody ChatLieuDTO dto) {
        if (dto.getTenChatLieu() == null || dto.getTenChatLieu().isEmpty() ||
                dto.getMaChatLieu() == null || dto.getMaChatLieu().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ChatLieuDTO created = chatLieuService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật chất liệu
    @PutMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> update(@PathVariable Integer id, @RequestBody ChatLieuDTO dto) {
        if (dto.getTenChatLieu() == null || dto.getTenChatLieu().isEmpty() ||
                dto.getMaChatLieu() == null || dto.getMaChatLieu().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ChatLieuDTO updated = chatLieuService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa chất liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        chatLieuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}