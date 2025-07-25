package com.example.datnspct.Controller;

import com.example.datnspct.dto.ThuongHieuDTO;
import com.example.datnspct.Service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thuonghieu")
public class ThuongHieuController {
    @Autowired
    private ThuongHieuService service;

    // Tạo mới
    @PostMapping
    public ResponseEntity<ThuongHieuDTO> create(@RequestBody ThuongHieuDTO dto) {
        ThuongHieuDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> getById(@PathVariable Integer id) {
        ThuongHieuDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<ThuongHieuDTO>> getAll() {
        List<ThuongHieuDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> update(@PathVariable Integer id, @RequestBody ThuongHieuDTO dto) {
        ThuongHieuDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
