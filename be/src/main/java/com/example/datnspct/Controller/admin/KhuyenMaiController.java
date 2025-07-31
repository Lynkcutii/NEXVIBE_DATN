package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.KhuyenMaiService;
import com.example.datnspct.dto.KhuyenMaiDTO;
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
@RequestMapping("/admin/api/khuyenmai")
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
