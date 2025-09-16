package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.SanPhamService;
import com.example.datnspct.dto.SanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/sanpham")
public class SanPhamClientController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/filter")
    public ResponseEntity<Page<SanPhamDTO>> filterProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "danhMuc", required = false) Integer[] danhMuc,
            @RequestParam(value = "thuongHieu", required = false) Integer[] thuongHieu,
            @RequestParam(value = "chatLieu", required = false) Integer[] chatLieu,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Boolean status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamDTO> products = sanPhamService.findByFilters(pageable, keyword, status, danhMuc, thuongHieu, chatLieu, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> getProductById(@PathVariable Integer id) {
        SanPhamDTO product = sanPhamService.laySanPhamTheoId(id);
        return ResponseEntity.ok(product);
    }
}