package com.example.datnspct.Controller;

import com.example.datnspct.dto.KhuyenMaiDTO;
import com.example.datnspct.Service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/khuyenmai")
public class KhuyenMaiController {
    @Autowired
    private KhuyenMaiService khuyenMaiService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<KhuyenMaiDTO> create(@RequestBody KhuyenMaiDTO dto) {
        KhuyenMaiDTO created = khuyenMaiService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiDTO> getById(@PathVariable Integer id) {
        KhuyenMaiDTO dto = khuyenMaiService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<KhuyenMaiDTO>> getAll() {
        List<KhuyenMaiDTO> list = khuyenMaiService.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiDTO> update(@PathVariable Integer id, @RequestBody KhuyenMaiDTO dto) {
        KhuyenMaiDTO updated = khuyenMaiService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        khuyenMaiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
