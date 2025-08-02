package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.ThuongHieuService;
import com.example.datnspct.dto.ThuongHieuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thuonghieu")
public class ThuongHieuController {

    @Autowired
    private ThuongHieuService thuongHieuService;

    // Lấy tất cả thương hiệu
    @GetMapping
    public ResponseEntity<List<ThuongHieuDTO>> getAllThuongHieu() {
        List<ThuongHieuDTO> thuongHieuList = thuongHieuService.getAll();
        return ResponseEntity.ok(thuongHieuList);
    }

    // Lấy thương hiệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> getById(@PathVariable Integer id) {
        ThuongHieuDTO thuongHieu = thuongHieuService.getById(id);
        return ResponseEntity.ok(thuongHieu);
    }

    // Thêm thương hiệu mới
    @PostMapping
    public ResponseEntity<ThuongHieuDTO> create(@RequestBody ThuongHieuDTO dto) {
        if (dto.getTenThuongHieu() == null || dto.getTenThuongHieu().isEmpty() ||
                dto.getMaThuongHieu() == null || dto.getMaThuongHieu().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ThuongHieuDTO created = thuongHieuService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật thương hiệu
    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> update(@PathVariable Integer id, @RequestBody ThuongHieuDTO dto) {
        if (dto.getTenThuongHieu() == null || dto.getTenThuongHieu().isEmpty() ||
                dto.getMaThuongHieu() == null || dto.getMaThuongHieu().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ThuongHieuDTO updated = thuongHieuService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa thương hiệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        thuongHieuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}