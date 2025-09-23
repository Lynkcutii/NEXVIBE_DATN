package com.example.datnspct.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.datnspct.Model.Img;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Model.Size;
import com.example.datnspct.Repository.ImgRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SanPhamRepository;
import com.example.datnspct.Repository.MauSacRepository;
import com.example.datnspct.Repository.SizeRepository;
import com.example.datnspct.dto.SanPhamChiTietDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ImgRepository imgRepository;

    @Autowired
    private Cloudinary cloudinary;

    public SanPhamChiTietDTO taoSanPhamChiTiet(SanPhamChiTietDTO dto, MultipartFile[] imageFiles) throws IOException {
        SanPhamChiTiet spct = new SanPhamChiTiet();
        spct.setMaSPCT(generateMaSPCT());
        spct.setGia(dto.getGia());
        spct.setSoLuong(dto.getSoLuong());
        spct.setTrangThai(true);

        if (dto.getIdSP() == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }
        SanPham sanPham = sanPhamRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        spct.setSanPham(sanPham);

        if (dto.getIdMauSac() == null) {
            throw new IllegalArgumentException("Màu sắc không được để trống");
        }
        MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
        spct.setMauSac(mauSac);

        if (dto.getIdSize() == null) {
            throw new IllegalArgumentException("Kích thước không được để trống");
        }
        Size size = sizeRepository.findById(dto.getIdSize())
                .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
        spct.setSize(size);

        SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);

        List<String> imageLinks = new ArrayList<>();
        if (imageFiles != null && imageFiles.length > 0) {
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    if (imageFile.getSize() > 5 * 1024 * 1024) {
                        throw new IllegalArgumentException("Kích thước ảnh phải nhỏ hơn 5MB: " + imageFile.getOriginalFilename());
                    }
                    try {
                        Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                                "resource_type", "image",
                                "folder", "san_pham_chi_tiet",
                                "public_id", imageFile.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_")
                        ));
                        String imageLink = uploadResult.get("secure_url").toString();
                        Img img = new Img();
                        img.setSanPhamChiTiet(saved);
                        img.setLink(imageLink);
                        imgRepository.save(img);
                        imageLinks.add(imageLink);
                    } catch (IOException e) {
                        throw new IOException("Không thể upload ảnh: " + imageFile.getOriginalFilename(), e);
                    }
                }
            }
        }

        updateSanPhamStats(sanPham.getId());
        SanPhamChiTietDTO result = convertToDTO(saved);
        result.setImageLinks(imageLinks);
        return result;
    }

    public SanPhamChiTietDTO capNhatSanPhamChiTiet(Integer id, SanPhamChiTietDTO dto, MultipartFile[] imageFiles, List<String> deletedImages) throws IOException {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        spct.setGia(dto.getGia());
        spct.setSoLuong(dto.getSoLuong());
        spct.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);

        if (dto.getIdSP() == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }
        SanPham sanPham = sanPhamRepository.findById(dto.getIdSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        spct.setSanPham(sanPham);

        if (dto.getIdMauSac() == null) {
            throw new IllegalArgumentException("Màu sắc không được để trống");
        }
        MauSac mauSac = mauSacRepository.findById(dto.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
        spct.setMauSac(mauSac);

        if (dto.getIdSize() == null) {
            throw new IllegalArgumentException("Kích thước không được để trống");
        }
        Size size = sizeRepository.findById(dto.getIdSize())
                .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
        spct.setSize(size);

        // Lấy danh sách ảnh hiện tại
        List<Img> existingImages = imgRepository.findBySanPhamChiTietId(id);
        List<String> currentImageLinks = existingImages.stream()
                .map(Img::getLink)
                .collect(Collectors.toList());

        // Xử lý ảnh đã xóa
        if (deletedImages != null && !deletedImages.isEmpty()) {
            System.out.println("Deleted images received: " + deletedImages);
            List<Img> imagesToDelete = existingImages.stream()
                    .filter(img -> deletedImages.contains(img.getLink()))
                    .collect(Collectors.toList());
            for (Img img : imagesToDelete) {
                String publicId = extractPublicId(img.getLink());
                try {
                    Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    System.out.println("Cloudinary delete result: " + result);
                    imgRepository.delete(img);
                } catch (IOException e) {
                    System.err.println("Lỗi khi xóa ảnh từ Cloudinary: " + e.getMessage());
                }
            }
            // Cập nhật danh sách link hiện tại
            currentImageLinks = currentImageLinks.stream()
                    .filter(link -> !deletedImages.contains(link))
                    .collect(Collectors.toList());
        }

        // Thêm ảnh mới
        if (imageFiles != null && imageFiles.length > 0) {
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    if (imageFile.getSize() > 5 * 1024 * 1024) {
                        throw new IllegalArgumentException("Kích thước ảnh phải nhỏ hơn 5MB: " + imageFile.getOriginalFilename());
                    }
                    Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "san_pham_chi_tiet",
                            "public_id", imageFile.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_")
                    ));
                    String imageLink = uploadResult.get("secure_url").toString();
                    Img img = new Img();
                    img.setSanPhamChiTiet(spct);
                    img.setLink(imageLink);
                    imgRepository.save(img);
                    currentImageLinks.add(imageLink);
                }
            }
        }

        // Cập nhật linkAnhDauTien nếu cần
//        Img firstImage = imgRepository.findFirstBySanPhamChiTietId(id);
//        if (firstImage != null) {
//            spct.setLinkAnhDauTien(firstImage.getLink());
//        } else if (!currentImageLinks.isEmpty()) {
//            spct.setLinkAnhDauTien(currentImageLinks.get(0));
//        } else {
//            spct.setLinkAnhDauTien("");
//        }

        SanPhamChiTiet updated = sanPhamChiTietRepository.save(spct);
        updateSanPhamStats(sanPham.getId());

        SanPhamChiTietDTO result = convertToDTO(updated);
        result.setImageLinks(currentImageLinks);
        return result;
    }

    public void xoaSanPhamChiTiet(Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        Integer idSP = spct.getSanPham().getId();

        // Xóa tất cả ảnh liên quan
        List<Img> images = imgRepository.findBySanPhamChiTietId(id);
        for (Img img : images) {
            String publicId = extractPublicId(img.getLink());
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                System.err.println("Lỗi khi xóa ảnh từ Cloudinary: " + e.getMessage());
            }
        }
        imgRepository.deleteBySanPhamChiTietId(id);
        sanPhamChiTietRepository.deleteById(id);

        updateSanPhamStats(idSP);
    }

    private void updateSanPhamStats(Integer idSP) {
        SanPham sanPham = sanPhamRepository.findById(idSP)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        List<SanPhamChiTiet> chiTiets = sanPhamChiTietRepository.findBySanPhamId(idSP);
        if (!chiTiets.isEmpty()) {
            Integer tongSoLuong = chiTiets.stream()
                    .mapToInt(SanPhamChiTiet::getSoLuong)
                    .sum();
            sanPham.setTongSoLuongSanPham(tongSoLuong);

            BigDecimal giaTrungBinh = chiTiets.stream()
                    .map(SanPhamChiTiet::getGia)
                    .filter(gia -> gia != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(chiTiets.size()), java.math.RoundingMode.HALF_UP);
            sanPham.setGia(giaTrungBinh);

            // Cập nhật ảnh chính nếu chưa có
            List<String> imageLinks = imgRepository.findBySanPhamChiTietId(chiTiets.get(0).getId())
                    .stream()
                    .map(Img::getLink)
                    .collect(Collectors.toList());
            if (sanPham.getImg() == null && !imageLinks.isEmpty()) {
                sanPham.setImg(imageLinks.get(0));
            }
        } else {
            sanPham.setTongSoLuongSanPham(0);
            sanPham.setGia(BigDecimal.ZERO);
            sanPham.setImg(null);
        }
        sanPhamRepository.save(sanPham);
    }

    public SanPhamChiTietDTO laySanPhamChiTietTheoId(Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chi tiết không tồn tại"));
        SanPhamChiTietDTO dto = convertToDTO(spct);
        List<String> imageLinks = imgRepository.findBySanPhamChiTietId(id)
                .stream()
                .map(Img::getLink)
                .collect(Collectors.toList());
        dto.setImageLinks(imageLinks);
        return dto;
    }

    public List<SanPhamChiTietDTO> laySanPhamChiTietTheoSanPham(Integer idSP) {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findBySanPhamId(idSP);
        return spctList.stream().map(spct -> {
            SanPhamChiTietDTO dto = convertToDTO(spct);
            List<String> imageLinks = imgRepository.findBySanPhamChiTietId(spct.getId())
                    .stream()
                    .map(Img::getLink)
                    .collect(Collectors.toList());
            dto.setImageLinks(imageLinks);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<SanPhamChiTietDTO> layTatCaSanPhamChiTiet() {
        List<SanPhamChiTiet> spctList = sanPhamChiTietRepository.findAll();
        return spctList.stream().map(spct -> {
            SanPhamChiTietDTO dto = convertToDTO(spct);
            List<String> imageLinks = imgRepository.findBySanPhamChiTietId(spct.getId())
                    .stream()
                    .map(Img::getLink)
                    .collect(Collectors.toList());
            dto.setImageLinks(imageLinks);
            return dto;
        }).collect(Collectors.toList());
    }

    public Page<SanPhamChiTietDTO> findWithFilters(String keyword, String mauSac, String size,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   Pageable pageable) {
        keyword = keyword != null ? keyword.trim() : null;
        mauSac = mauSac != null ? mauSac.trim().toLowerCase() : null;
        size = size != null ? size.trim().toLowerCase() : null;

        Page<SanPhamChiTiet> page = sanPhamChiTietRepository.findByFilters(
                keyword,
                (java.util.List<Integer>) null,
                (String) null,
                mauSac,
                (String) null,
                size,
                minPrice,
                maxPrice,
                pageable);
        return page.map(spct -> {
            SanPhamChiTietDTO dto = convertToDTO(spct);
            List<String> imageLinks = imgRepository.findBySanPhamChiTietId(spct.getId())
                    .stream()
                    .map(Img::getLink)
                    .collect(Collectors.toList());
            dto.setImageLinks(imageLinks);
            return dto;
        });
    }

    private SanPhamChiTietDTO convertToDTO(SanPhamChiTiet spct) {
        SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
        dto.setId(spct.getId());
        dto.setIdSP(spct.getSanPham() != null ? spct.getSanPham().getId() : null);
        dto.setMaSPCT(spct.getMaSPCT());
        dto.setTenSP(spct.getSanPham() != null ? spct.getSanPham().getTenSP() : null);
        dto.setGia(spct.getGia());
        dto.setSoLuong(spct.getSoLuong());
        dto.setTrangThai(spct.getTrangThai());
        dto.setTenMauSac(spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : null);
        dto.setTenSize(spct.getSize() != null ? spct.getSize().getTenSize() : null);
        dto.setIdMauSac(spct.getMauSac() != null ? spct.getMauSac().getIdMauSac() : null);
        dto.setIdSize(spct.getSize() != null ? spct.getSize().getIdSize() : null);
        Img firstImage = imgRepository.findFirstBySanPhamChiTietId(spct.getId());
        dto.setLinkAnhDauTien(firstImage != null ? firstImage.getLink() : null);
        return dto;
    }

    private String generateMaSPCT() {
        Long count = sanPhamChiTietRepository.count();
        return String.format("SPCT%03d", count + 1);
    }

    private String extractPublicId(String link) {
        // Extract public_id from Cloudinary URL (e.g., https://res.cloudinary.com/cloud-name/image/upload/v123/folder/public_id.jpg)
        String[] parts = link.split("/");
        String fileName = parts[parts.length - 1]; // Lấy phần file name (public_id.jpg)
        return fileName.split("\\.")[0]; // Lấy public_id (loại bỏ phần .jpg)
    }
}