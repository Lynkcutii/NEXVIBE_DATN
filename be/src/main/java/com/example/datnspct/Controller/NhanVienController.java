package com.example.datnspct.Controller;

import com.example.datnspct.dto.NhanVienDTO;
import com.example.datnspct.Service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<NhanVienDTO> create(@RequestBody NhanVienDTO dto) {
        NhanVienDTO created = nhanVienService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<NhanVienDTO> getById(@PathVariable Integer id) {
        NhanVienDTO dto = nhanVienService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<NhanVienDTO>> getAll() {
        List<NhanVienDTO> list = nhanVienService.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<NhanVienDTO> update(@PathVariable Integer id, @RequestBody NhanVienDTO dto) {
        NhanVienDTO updated = nhanVienService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        nhanVienService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

