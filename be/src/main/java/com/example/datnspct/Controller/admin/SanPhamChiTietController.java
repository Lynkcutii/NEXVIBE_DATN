package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SanPhamChiTietService;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/sanphamchitiet")
public class SanPhamChiTietController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    @Autowired
    private ObjectMapper objectMapper; // Để parse JSON

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SanPhamChiTietDTO> createSanPhamChiTiet(
            @RequestPart("data") SanPhamChiTietDTO dto,
            @RequestPart(value = "imageFiles", required = false) MultipartFile[] imageFiles) throws IOException {
        validateSanPhamChiTietDTO(dto);
        SanPhamChiTietDTO created = sanPhamChiTietService.taoSanPhamChiTiet(dto, imageFiles);
        return ResponseEntity.ok(created);
    }

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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SanPhamChiTietDTO> capNhatSanPhamChiTiet(
            @PathVariable Integer id,
            @RequestPart("data") SanPhamChiTietDTO dto,
            @RequestPart(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @RequestParam(value = "deletedImages", required = false) String deletedImagesJson) {
        try {
            // Parse deletedImagesJson an toàn
            List<String> deletedImages = new ArrayList<>();
            if (deletedImagesJson != null && !deletedImagesJson.isEmpty()) {
                try {
                    deletedImages = objectMapper.readValue(deletedImagesJson, new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    throw new IllegalArgumentException("Không thể parse deletedImagesJson: " + e.getMessage());
                }
            }

            // Gọi service với danh sách deletedImages
            SanPhamChiTietDTO updated = sanPhamChiTietService.capNhatSanPhamChiTiet(id, dto, imageFiles, deletedImages);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPhamChiTiet(@PathVariable Integer id) {
        sanPhamChiTietService.xoaSanPhamChiTiet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<SanPhamChiTietDTO>> findWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String mauSac,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        try {
            Page<SanPhamChiTietDTO> result = sanPhamChiTietService.findWithFilters(
                    keyword, mauSac, size, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void validateSanPhamChiTietDTO(SanPhamChiTietDTO dto) {
        if (dto.getIdSP() == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }
        if (dto.getIdMauSac() == null) {
            throw new IllegalArgumentException("Màu sắc không được để trống");
        }
        if (dto.getIdSize() == null) {
            throw new IllegalArgumentException("Kích thước không được để trống");
        }
        if (dto.getGia() == null || dto.getGia().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá phải lớn hơn hoặc bằng 0");
        }
        if (dto.getSoLuong() == null || dto.getSoLuong() < 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 0");
        }
    }
}