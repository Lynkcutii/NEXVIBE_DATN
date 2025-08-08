package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.KhuyenMaiService;
import com.example.datnspct.dto.KhuyenMaiDTO;
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

    // Lấy danh sách khuyến mãi áp dụng theo IdKH
    @GetMapping("/applicable/{idKH}")
    public ResponseEntity<List<KhuyenMaiDTO>> getApplicableVouchers(@PathVariable Integer idKH) {
        List<KhuyenMaiDTO> vouchers = khuyenMaiService.getApplicableVouchers(idKH);
        return ResponseEntity.ok(vouchers);
    }

    // Lấy danh sách khuyến mãi theo IdKH
    @GetMapping("/byCustomer/{idKH}")
    public ResponseEntity<List<KhuyenMaiDTO>> getVouchersByCustomerId(@PathVariable Integer idKH) {
        List<KhuyenMaiDTO> vouchers = khuyenMaiService.getVouchersByCustomerId(idKH);
        return ResponseEntity.ok(vouchers);
    }
}