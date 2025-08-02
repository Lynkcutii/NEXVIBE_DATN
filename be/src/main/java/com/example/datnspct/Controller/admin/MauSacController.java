package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Service.MauSacService;
import com.example.datnspct.dto.MauSacDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mausac")
public class MauSacController {

    @Autowired
    private MauSacService mauSacService;

    // Lấy tất cả màu sắc
    @GetMapping
    public ResponseEntity<List<MauSacDTO>> getAllMauSac() {
        List<MauSacDTO> mauSacList = mauSacService.getAll();
        return ResponseEntity.ok(mauSacList);
    }

    // Lấy màu sắc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MauSacDTO> getById(@PathVariable Integer id) {
        MauSacDTO mauSac = mauSacService.getById(id);
        return ResponseEntity.ok(mauSac);
    }

    // Thêm màu sắc mới
    @PostMapping
    public ResponseEntity<MauSacDTO> create(@RequestBody MauSacDTO dto) {
        if (dto.getTenMauSac() == null || dto.getTenMauSac().isEmpty() ||
                dto.getMaMauSac() == null || dto.getMaMauSac().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        MauSacDTO created = mauSacService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật màu sắc
    @PutMapping("/{id}")
    public ResponseEntity<MauSacDTO> update(@PathVariable Integer id, @RequestBody MauSacDTO dto) {
        if (dto.getTenMauSac() == null || dto.getTenMauSac().isEmpty() ||
                dto.getMaMauSac() == null || dto.getMaMauSac().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        MauSacDTO updated = mauSacService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa màu sắc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        mauSacService.delete(id);
        return ResponseEntity.noContent().build();
    }
}