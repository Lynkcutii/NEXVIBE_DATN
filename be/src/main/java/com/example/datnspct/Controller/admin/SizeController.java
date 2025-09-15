package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SizeService;
import com.example.datnspct.dto.SizeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/size")
public class SizeController {

    @Autowired
    private SizeService sizeService;

    // Thêm size mới
    @PostMapping
    public ResponseEntity<SizeDTO> create(@RequestBody SizeDTO dto) {
        if (dto.getTenSize() == null || dto.getTenSize().isEmpty() ||
                dto.getMaSize() == null || dto.getMaSize().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        SizeDTO created = sizeService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy size theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SizeDTO> getById(@PathVariable Integer id) {
        SizeDTO dto = sizeService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // Lấy tất cả size
    @GetMapping
    public ResponseEntity<List<SizeDTO>> getAll() {
        List<SizeDTO> list = sizeService.getAll();
        return ResponseEntity.ok(list);
    }

    // Cập nhật size
    @PutMapping("/{id}")
    public ResponseEntity<SizeDTO> update(@PathVariable Integer id, @RequestBody SizeDTO dto) {
        if (dto.getTenSize() == null || dto.getTenSize().isEmpty() ||
                dto.getMaSize() == null || dto.getMaSize().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        SizeDTO updated = sizeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa size
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sizeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}