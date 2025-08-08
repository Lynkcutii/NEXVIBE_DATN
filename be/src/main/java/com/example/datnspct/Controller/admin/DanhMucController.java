package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.DanhMuc;
import com.example.datnspct.Service.DanhMucService;
import com.example.datnspct.dto.DanhMucDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danhmuc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    // Lấy tất cả danh mục
    @GetMapping
    public ResponseEntity<List<DanhMucDTO>> getAllDanhMuc() {
        List<DanhMucDTO> danhMucList = danhMucService.getAll();
        return ResponseEntity.ok(danhMucList);
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DanhMucDTO> getById(@PathVariable Integer id) {
        DanhMucDTO danhMuc = danhMucService.getById(id);
        return ResponseEntity.ok(danhMuc);
    }

    // Thêm danh mục mới
    @PostMapping
    public ResponseEntity<DanhMucDTO> create(@RequestBody DanhMucDTO dto) {
        if (dto.getTenDM() == null || dto.getTenDM().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        DanhMucDTO created = danhMucService.create(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    public ResponseEntity<DanhMucDTO> update(@PathVariable Integer id, @RequestBody DanhMucDTO dto) {
        if (dto.getTenDM() == null || dto.getTenDM().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        DanhMucDTO updated = danhMucService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        danhMucService.delete(id);
        return ResponseEntity.noContent().build();
    }
}