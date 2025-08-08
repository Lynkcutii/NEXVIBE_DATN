package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.NhanVienService;
import com.example.datnspct.dto.NhanVienDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/nhanvien")
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
    public ResponseEntity<List<NhanVienDTO>> getAll(
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false) String keyword) {
        List<NhanVienDTO> nhanViens = nhanVienService.getAll(trangThai, keyword);
        return ResponseEntity.ok(nhanViens);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<NhanVienDTO> update(@PathVariable Integer id, @RequestBody NhanVienDTO dto) {
        NhanVienDTO updated = nhanVienService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Chuyển đổi trạng thái
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<NhanVienDTO> toggleStatus(@PathVariable Integer id) {
        NhanVienDTO updated = nhanVienService.toggleStatus(id);
        return ResponseEntity.ok(updated);
    }
}

