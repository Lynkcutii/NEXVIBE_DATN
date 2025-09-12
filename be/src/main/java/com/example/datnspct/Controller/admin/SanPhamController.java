package com.example.datnspct.Controller.admin;

import com.example.datnspct.Service.SanPhamService;
import com.example.datnspct.dto.SanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/san-pham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private Cloudinary cloudinary;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi khi xử lý ảnh: " + e.getMessage()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Ảnh sản phẩm là bắt buộc khi tạo mới");
        }

        // Tải ảnh lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "san_pham"
        ));
        sanPhamDTO.setImageLink(uploadResult.get("secure_url").toString());

        // Không cho phép nhập giá trực tiếp
        sanPhamDTO.setGia(null);
        SanPhamDTO created = sanPhamService.taoSanPham(sanPhamDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<SanPhamDTO>> getAllSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tenSP,
            @RequestParam(required = false) Boolean status,
            @RequestParam(name = "danhMuc", required = false) Integer[] danhMuc,
            @RequestParam(name = "thuongHieu", required = false) Integer[] thuongHieu,
            @RequestParam(name = "chatLieu", required = false) Integer[] chatLieu,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        System.out.println("Tham số nhận được: tenSP=" + tenSP + ", danhMuc=" + Arrays.toString(danhMuc) +
                ", thuongHieu=" + Arrays.toString(thuongHieu) + ", chatLieu=" + Arrays.toString(chatLieu) +
                ", minPrice=" + minPrice + ", maxPrice=" + maxPrice);
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamDTO> sanPhamPage = sanPhamService.findByFilters(pageable, tenSP, status, danhMuc, thuongHieu, chatLieu, minPrice, maxPrice);
        return ResponseEntity.ok(sanPhamPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> laySanPhamTheoId(@PathVariable Integer id) {
        SanPhamDTO sanPhamDTO = sanPhamService.laySanPhamTheoId(id);
        if (sanPhamDTO == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id);
        }
        return ResponseEntity.ok(sanPhamDTO);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SanPhamDTO> capNhatSanPham(
            @PathVariable Integer id,
            @RequestPart("data") SanPhamDTO sanPhamDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "deletedImage", required = false) String deletedImage) throws IOException {
        // Log để debug
        System.out.println("Received SanPhamDTO for update: " + sanPhamDTO);
        System.out.println("Received imageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));
        System.out.println("Received deletedImage: " + deletedImage);

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

        // Gán ID sản phẩm từ path variable
        sanPhamDTO.setIdSP(id);

        // Xử lý ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "san_pham"
            ));
            sanPhamDTO.setImageLink(uploadResult.get("secure_url").toString());
            // Xóa ảnh cũ trên Cloudinary nếu có
            if (deletedImage != null && !deletedImage.isEmpty()) {
                String publicId = extractPublicIdFromUrl(deletedImage);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } else if (deletedImage != null && !deletedImage.isEmpty()) {
            // Nếu xóa ảnh hiện tại, đặt imageLink là null (hoặc giá trị mặc định nếu cần)
            sanPhamDTO.setImageLink(null);
            String publicId = extractPublicIdFromUrl(deletedImage);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } else if (sanPhamDTO.getImageLink() == null || sanPhamDTO.getImageLink().isEmpty()) {
            // Lấy imageLink hiện tại từ cơ sở dữ liệu nếu không có ảnh mới và imageLink từ DTO là null
            SanPhamDTO existingSanPham = sanPhamService.laySanPhamTheoId(id);
            if (existingSanPham == null || existingSanPham.getImageLink() == null || existingSanPham.getImageLink().isEmpty()) {
                throw new IllegalArgumentException("Ảnh sản phẩm là bắt buộc");
            }
            sanPhamDTO.setImageLink(existingSanPham.getImageLink());
        }

        // Không cho phép nhập giá trực tiếp
        sanPhamDTO.setGia(null);
        SanPhamDTO updated = sanPhamService.capNhatSanPham(id, sanPhamDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaSanPham(@PathVariable Integer id) {
        sanPhamService.xoaSanPham(id);
        return ResponseEntity.noContent().build();
    }

    private String extractPublicIdFromUrl(String url) {
        // Ví dụ URL: https://res.cloudinary.com/your_cloud_name/image/upload/v1234567890/san_pham/sample.jpg
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        return "san_pham/" + fileName.substring(0, fileName.lastIndexOf("."));
    }
}