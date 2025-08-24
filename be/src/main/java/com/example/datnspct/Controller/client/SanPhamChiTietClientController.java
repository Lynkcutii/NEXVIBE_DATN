package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.SanPhamChiTietService;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sanphamchitiet")
public class SanPhamChiTietClientController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

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

    @GetMapping
    public ResponseEntity<List<SanPhamChiTietDTO>> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTietDTO> sanPhamChiTietDTOs = sanPhamChiTietService.layTatCaSanPhamChiTiet();
        return ResponseEntity.ok(sanPhamChiTietDTOs);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<SanPhamChiTietDTO>> getSanPhamChiTiet(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String danhMuc,
            @RequestParam(required = false) String thuongHieu,
            @RequestParam(required = false) String mauSac,
            @RequestParam(required = false) String chatLieu,
            @RequestParam(required = false) String tenSize,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<SanPhamChiTietDTO> result = sanPhamChiTietService.findWithFilters(
                keyword, mauSac, tenSize, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(result);
    }
}