package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SanPhamService;
import com.example.datnspct.Service.SanPhamChiTietService;
import com.example.datnspct.dto.SanPhamDTO;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/san-pham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    @Autowired
    private Cloudinary cloudinary;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @PostMapping
    public ResponseEntity<SanPhamDTO> taoSanPham(
            @RequestPart("data") SanPhamDTO sanPhamDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        // Validation các trường bắt buộc
        if (sanPhamDTO.getTenSP() == null || sanPhamDTO.getTenSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (sanPhamDTO.getIdDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (sanPhamDTO.getIdThuongHieu() == null) {
            throw new IllegalArgumentException("Thương hiệu không được để trống");
        }
        if (sanPhamDTO.getIdChatLieu() == null) {
            throw new IllegalArgumentException("Chất liệu không được để trống");
        }

        // Không cho phép nhập gia trực tiếp, để giá mặc định là null hoặc 0
        sanPhamDTO.setGia(null); // Đảm bảo giá không được gửi từ frontend

        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "san_pham"
            ));
            sanPhamDTO.setImageLink(uploadResult.get("secure_url").toString());
        }
        SanPhamDTO sanPhamDaTao = sanPhamService.taoSanPham(sanPhamDTO);
        return ResponseEntity.ok(sanPhamDaTao);
    }

//    @PostMapping("/chitiet")
//    public ResponseEntity<SanPhamChiTietDTO> taoSanPhamChiTiet(
//            @RequestPart("data") SanPhamChiTietDTO sanPhamChiTietDTO,
//            @RequestPart(value = "imageFile", required = false) MultipartFile[] imageFiles) throws IOException {
//        // Validation các trường bắt buộc
//        if (sanPhamChiTietDTO.getIdSP() == null) {
//            throw new IllegalArgumentException("ID sản phẩm không được để trống");
//        }
//        if (sanPhamChiTietDTO.getIdMauSac() == null) {
//            throw new IllegalArgumentException("Màu sắc không được để trống");
//        }
//        if (sanPhamChiTietDTO.getIdSize() == null) {
//            throw new IllegalArgumentException("Kích thước không được để trống");
//        }
//        if (sanPhamChiTietDTO.getGia() == null) {
//            throw new IllegalArgumentException("Giá chi tiết không được để trống");
//        }
//        if (sanPhamChiTietDTO.getSoLuong() == null) {
//            throw new IllegalArgumentException("Số lượng không được để trống");
//        }
//
//        SanPhamChiTietDTO chiTietDaTao = sanPhamChiTietService.taoSanPhamChiTiet(sanPhamChiTietDTO, imageFiles);
//        return ResponseEntity.ok(chiTietDaTao);
//    }

    @GetMapping
    public ResponseEntity<Page<SanPhamDTO>> getAllSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tenSP,
            @RequestParam(required = false) Boolean status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamDTO> sanPhamPage = sanPhamService.findAll(tenSP, status, pageable);
        return ResponseEntity.ok(sanPhamPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> laySanPhamTheoId(@PathVariable Integer id) {
        SanPhamDTO sanPhamDTO = sanPhamService.laySanPhamTheoId(id);
        return ResponseEntity.ok(sanPhamDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamDTO> capNhatSanPham(
            @PathVariable Integer id,
            @RequestPart("data") SanPhamDTO sanPhamDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        // Validation các trường bắt buộc
        if (sanPhamDTO.getTenSP() == null || sanPhamDTO.getTenSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (sanPhamDTO.getIdDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (sanPhamDTO.getIdThuongHieu() == null) {
            throw new IllegalArgumentException("Thương hiệu không được để trống");
        }
        if (sanPhamDTO.getIdChatLieu() == null) {
            throw new IllegalArgumentException("Chất liệu không được để trống");
        }

        // Không cho phép nhập gia trực tiếp, để giá được tính tự động
        sanPhamDTO.setGia(null); // Đảm bảo giá không được gửi từ frontend

        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "san_pham"
            ));
            sanPhamDTO.setImageLink(uploadResult.get("secure_url").toString());
        }
        sanPhamDTO.setIdSP(id);
        SanPhamDTO sanPhamDaCapNhat = sanPhamService.capNhatSanPham(id, sanPhamDTO);
        return ResponseEntity.ok(sanPhamDaCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPham(@PathVariable Integer id) {
        sanPhamService.xoaSanPham(id);
        return ResponseEntity.noContent().build();
    }
}