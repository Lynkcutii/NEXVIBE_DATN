package com.example.datnspct.Controller;

import com.example.datnspct.Service.DanhMucService;
import com.example.datnspct.dto.DanhMucDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danhmuc")
@CrossOrigin(origins = "*")
public class DanhMucController {
    @Autowired
    private DanhMucService danhMucService;

    @PostMapping
    public ResponseEntity<DanhMucDTO> create(@RequestBody DanhMucDTO dto) {
        return ResponseEntity.ok(danhMucService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DanhMucDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(danhMucService.getById(id));
    }

    // Lấy theo slug
//    @GetMapping("/slug/{slug}")
//    public ResponseEntity<DanhMucDTO> getBySlug(@PathVariable String slug) {
//        return ResponseEntity.ok(danhMucService.getBySlug(slug));
//    }

    @GetMapping
    public ResponseEntity<List<DanhMucDTO>> getAll() {
        return ResponseEntity.ok(danhMucService.getAll());
    }

    // Lấy danh mục cha
//    @GetMapping("/parents")
//    public ResponseEntity<List<DanhMucDTO>> getParents() {
//        return ResponseEntity.ok(danhMucService.getParents());
//    }
//
//    // Lấy danh mục con
//    @GetMapping("/{parentId}/children")
//    public ResponseEntity<List<DanhMucDTO>> getChildren(@PathVariable Integer parentId) {
//        return ResponseEntity.ok(danhMucService.getChildren(parentId));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<DanhMucDTO> update(@PathVariable Integer id, @RequestBody DanhMucDTO dto) {
        return ResponseEntity.ok(danhMucService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        danhMucService.delete(id);
        return ResponseEntity.noContent().build();
    }
}