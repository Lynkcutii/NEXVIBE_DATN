package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SanPhamChiTietService;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/api/sanphamchitiet")
public class SanPhamChiTietController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    // Tạo mới
    @PostMapping
    public ResponseEntity<SanPhamChiTietDTO> taoSanPhamChiTiet(@RequestBody SanPhamChiTietDTO sanPhamChiTietDTO) {
        SanPhamChiTietDTO sanPhamChiTietDaTao = sanPhamChiTietService.taoSanPhamChiTiet(sanPhamChiTietDTO);
        return ResponseEntity.ok(sanPhamChiTietDaTao);
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamChiTietDTO> laySanPhamChiTietTheoId(@PathVariable Integer id) {
        SanPhamChiTietDTO sanPhamChiTietDTO = sanPhamChiTietService.laySanPhamChiTietTheoId(id);
        return ResponseEntity.ok(sanPhamChiTietDTO);
    }

    @GetMapping("/bySanPham/{idSP}")
    public ResponseEntity<List<SanPhamChiTietDTO>> laySanPhamChiTietTheoSanPham(@PathVariable Integer idSP) {
        List<SanPhamChiTietDTO> variants = sanPhamChiTietService.laySanPhamChiTietTheoSanPham(idSP);
        return ResponseEntity.ok(variants);
    }

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<SanPhamChiTietDTO>> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTietDTO> sanPhamChiTietDTOs = sanPhamChiTietService.layTatCaSanPhamChiTiet();
        return ResponseEntity.ok(sanPhamChiTietDTOs);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<SanPhamChiTietDTO> capNhatSanPhamChiTiet(@PathVariable Integer id, @RequestBody SanPhamChiTietDTO sanPhamChiTietDTO) {
        SanPhamChiTietDTO sanPhamChiTietDaCapNhat = sanPhamChiTietService.capNhatSanPhamChiTiet(id, sanPhamChiTietDTO);
        return ResponseEntity.ok(sanPhamChiTietDaCapNhat);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPhamChiTiet(@PathVariable Integer id) {
        sanPhamChiTietService.xoaSanPhamChiTiet(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/filter")
    public ResponseEntity<Page<SanPhamChiTietDTO>> findWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String danhMuc,
            @RequestParam(required = false) String thuongHieu,
            @RequestParam(required = false) String mauSac,
            @RequestParam(required = false) String chatLieu,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        try {
            Page<SanPhamChiTietDTO> result = sanPhamChiTietService.findWithFilters(
                    keyword, danhMuc, thuongHieu, mauSac, chatLieu, size, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}