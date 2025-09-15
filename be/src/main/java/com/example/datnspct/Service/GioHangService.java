package com.example.datnspct.Service;

import com.example.datnspct.Model.GioHang;
import com.example.datnspct.Model.GioHangCT;
import com.example.datnspct.Model.Img;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.MauSac;
import com.example.datnspct.Model.SanPham;
import com.example.datnspct.Model.SanPhamChiTiet;
import com.example.datnspct.Model.Size;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.GioHangCTRepository;
import com.example.datnspct.Repository.GioHangRepository;
import com.example.datnspct.Repository.ImgRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.MauSacRepository;
import com.example.datnspct.Repository.SanPhamChiTietRepository;
import com.example.datnspct.Repository.SizeRepository;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.GioHangCTDTO;
import com.example.datnspct.dto.GioHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private GioHangCTRepository gioHangCTRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ImgRepository imgRepository;

    private GioHang toEntity(GioHangDTO dto) {
        GioHang entity = new GioHang();
        entity.setIdGH(dto.getIdGH());
        entity.setMaGH(dto.getMaGH());
        entity.setKhachHang(dto.getIdKH() != null ? khachHangRepository.findById(dto.getIdKH()).orElse(null) : null);
        entity.setTaiKhoan(dto.getIdTK() != null ? taiKhoanRepository.findById(dto.getIdTK()).orElse(null) : null);
        entity.setTrangThai(dto.getTrangThai());
        entity.setNgayTao(dto.getNgayTao());
        entity.setNgaySua(dto.getNgaySua());
        return entity;
    }

    private GioHangDTO toDTO(GioHang entity) {
        GioHangDTO dto = new GioHangDTO();
        dto.setIdGH(entity.getIdGH());
        dto.setMaGH(entity.getMaGH());
        dto.setIdKH(entity.getKhachHang() != null ? entity.getKhachHang().getIdKH() : null);
        dto.setIdTK(entity.getTaiKhoan() != null ? entity.getTaiKhoan().getIdTK() : null);
        dto.setTrangThai(entity.getTrangThai());
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgaySua(entity.getNgaySua());
        List<GioHangCT> chiTiets = entity.getGioHangChiTiets();
        System.out.println("toDTO: Fetched chiTiets for idGH=" + entity.getIdGH() + ", count=" + (chiTiets != null ? chiTiets.size() : 0));
        if (chiTiets != null && !chiTiets.isEmpty()) {
            chiTiets.forEach(ct -> {
                System.out.println("toDTO: Processing ChiTiet idGHCT=" + ct.getIdGHCT() + ", idSPCT=" + (ct.getSanPhamChiTiet() != null ? ct.getSanPhamChiTiet().getId() : "null"));
            });
            dto.setChiTiets(chiTiets.stream()
                    .filter(ct -> {
                        try {
                            toGioHangCTDTO(ct);
                            return true;
                        } catch (Exception e) {
                            System.out.println("toDTO: Skipping invalid ChiTiet idGHCT=" + ct.getIdGHCT() + ", error=" + e.getMessage());
                            return false;
                        }
                    })
                    .map(this::toGioHangCTDTO)
                    .collect(Collectors.toList()));
        } else {
            System.out.println("toDTO: No chiTiets found for idGH=" + entity.getIdGH());
            dto.setChiTiets(List.of());
        }
        System.out.println("toDTO: Returning ChiTiets count=" + dto.getChiTiets().size());
        return dto;
    }

    private GioHangCTDTO toGioHangCTDTO(GioHangCT chiTiet) {
        GioHangCTDTO dto = new GioHangCTDTO();
        dto.setIdGHCT(chiTiet.getIdGHCT());
        dto.setIdGH(chiTiet.getGioHang() != null ? chiTiet.getGioHang().getIdGH() : null);
        SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();

        if (spct == null) {
            System.out.println("toGioHangCTDTO: SanPhamChiTiet is null for idGHCT=" + chiTiet.getIdGHCT());
            dto.setTenSP("Sản phẩm không xác định");
            dto.setLink("https://placehold.co/150");
            dto.setMauSac("Mặc định");
            dto.setKichThuoc("M");
            dto.setSoLuong(chiTiet.getSoLuong());
            dto.setDonGia(chiTiet.getDonGia() != null ? chiTiet.getDonGia() : BigDecimal.valueOf(0));
            dto.setSanPhamCTSoLuong(0); // Gán mặc định nếu spct null
            return dto;
        }

        dto.setIdSPCT(spct.getId());
        SanPham sanPham = spct.getSanPham();

        if (sanPham == null) {
            System.out.println("toGioHangCTDTO: SanPham is null for idSPCT=" + spct.getId());
            dto.setTenSP("Sản phẩm không xác định");
        } else {
            dto.setTenSP(sanPham.getTenSP());
        }

        Img firstImage = imgRepository.findFirstBySanPhamChiTietId(spct.getId());
        dto.setLink(firstImage != null ? firstImage.getLink() : "https://placehold.co/150");

        MauSac mauSac = spct.getMauSac();
        dto.setMauSac(mauSac != null ? mauSac.getTenMauSac() : "Mặc định");

        Size size = spct.getSize();
        dto.setKichThuoc(size != null ? size.getTenSize() : "M");

        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setDonGia(chiTiet.getDonGia() != null ? chiTiet.getDonGia() : BigDecimal.valueOf(0));
        dto.setSanPhamCTSoLuong(spct.getSoLuong()); // Thêm số lượng tồn kho

        System.out.println("toGioHangCTDTO: Converted idGHCT=" + dto.getIdGHCT() + ", tenSP=" + dto.getTenSP() + ", link=" + dto.getLink() + ", sanPhamCTSoLuong=" + dto.getSanPhamCTSoLuong());
        return dto;
    }

    @Transactional
    public GioHangDTO getByTaiKhoanId(Integer idTK) {
        System.out.println("getByTaiKhoanId: Starting for idTK=" + idTK);
        TaiKhoan tk = taiKhoanRepository.findById(idTK)
                .orElseThrow(() -> {
                    RuntimeException e = new RuntimeException("Tài khoản không tồn tại: idTK=" + idTK);
                    System.out.println("getByTaiKhoanId: Error - " + e.getMessage());
                    return e;
                });

        GioHang gioHang = gioHangRepository.findByTaiKhoanIdTK(idTK)
                .orElseThrow(() -> {
                    RuntimeException e = new RuntimeException("Không tìm thấy giỏ hàng cho idTK=" + idTK);
                    System.out.println("getByTaiKhoanId: Error - " + e.getMessage());
                    return e;
                });

        GioHangDTO dto = toDTO(gioHang);
        System.out.println("getByTaiKhoanId: Returning GioHangDTO with id=" + dto.getIdGH() + ", chiTiets count=" + dto.getChiTiets().size());
        return dto;
    }

    public GioHangDTO getOrCreateByTaiKhoan(String taiKhoan) {
        System.out.println("getOrCreateByTaiKhoan: Starting for taiKhoan=" + taiKhoan);
        TaiKhoan tk = taiKhoanRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> {
                    RuntimeException e = new RuntimeException("Tài khoản không tồn tại: " + taiKhoan);
                    System.out.println("getOrCreateByTaiKhoan: Error - " + e.getMessage());
                    return e;
                });

        KhachHang khachHang = khachHangRepository.findByTaiKhoanIdTK(tk.getIdTK())
                .orElseGet(() -> {
                    System.out.println("getOrCreateByTaiKhoan: Creating new KhachHang for taiKhoan=" + taiKhoan);
                    KhachHang newKhachHang = new KhachHang();
                    newKhachHang.setMaKH("KH-" + taiKhoan + "-" + System.currentTimeMillis());
                    newKhachHang.setTenKH("Khách Hàng " + taiKhoan);
                    newKhachHang.setSdt("0123456789");
                    newKhachHang.setTaiKhoan(tk);
                    newKhachHang.setTrangThai(true);
                    KhachHang saved = khachHangRepository.save(newKhachHang);
                    System.out.println("getOrCreateByTaiKhoan: Created KhachHang with id=" + saved.getIdKH());
                    return saved;
                });

        GioHang gioHang = gioHangRepository.findByTaiKhoanIdTK(tk.getIdTK())
                .orElseGet(() -> {
                    System.out.println("getOrCreateByTaiKhoan: Creating new GioHang for taiKhoan=" + taiKhoan);
                    GioHang newGioHang = new GioHang();
                    newGioHang.setMaGH("GH-" + taiKhoan + "-" + System.currentTimeMillis());
                    newGioHang.setKhachHang(khachHang);
                    newGioHang.setTaiKhoan(tk);
                    newGioHang.setTrangThai(true);
                    newGioHang.setNgayTao(LocalDateTime.now());
                    newGioHang.setNgaySua(LocalDateTime.now());
                    GioHang saved = gioHangRepository.save(newGioHang);
                    System.out.println("getOrCreateByTaiKhoan: Created GioHang with id=" + saved.getIdGH());
                    return saved;
                });

        if (gioHang.getKhachHang() == null) {
            System.out.println("getOrCreateByTaiKhoan: Updating GioHang with KhachHang for taiKhoan=" + taiKhoan);
            gioHang.setKhachHang(khachHang);
            gioHang.setNgaySua(LocalDateTime.now());
            gioHang = gioHangRepository.save(gioHang);
        }

        GioHangDTO dto = toDTO(gioHang);
        System.out.println("getOrCreateByTaiKhoan: Returning GioHangDTO with id=" + dto.getIdGH());
        return dto;
    }

    public void addToCart(String taiKhoan, Integer idSpct, Integer soLuong, String mauSac, String kichThuoc) {
        System.out.println("addToCart: Starting for taiKhoan=" + taiKhoan + ", idSpct=" + idSpct + ", soLuong=" + soLuong);
        TaiKhoan tk = taiKhoanRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> {
                    RuntimeException e = new RuntimeException("Tài khoản không tồn tại: " + taiKhoan);
                    System.out.println("addToCart: Error - " + e.getMessage());
                    return e;
                });

        KhachHang khachHang = khachHangRepository.findByTaiKhoanIdTK(tk.getIdTK())
                .orElseGet(() -> {
                    System.out.println("addToCart: Creating new KhachHang for taiKhoan=" + taiKhoan);
                    KhachHang newKhachHang = new KhachHang();
                    newKhachHang.setMaKH("KH-" + taiKhoan + "-" + System.currentTimeMillis());
                    newKhachHang.setTenKH("Khách Hàng " + taiKhoan);
                    newKhachHang.setSdt("0123456789");
                    newKhachHang.setTaiKhoan(tk);
                    newKhachHang.setTrangThai(true);
                    KhachHang saved = khachHangRepository.save(newKhachHang);
                    System.out.println("addToCart: Created KhachHang with id=" + saved.getIdKH());
                    return saved;
                });

        GioHang gioHang = gioHangRepository.findByTaiKhoanIdTK(tk.getIdTK())
                .orElseGet(() -> {
                    System.out.println("addToCart: Creating new GioHang for taiKhoan=" + taiKhoan);
                    GioHang newGioHang = new GioHang();
                    newGioHang.setMaGH("GH-" + taiKhoan + "-" + System.currentTimeMillis());
                    newGioHang.setKhachHang(khachHang);
                    newGioHang.setTaiKhoan(tk);
                    newGioHang.setTrangThai(true);
                    newGioHang.setNgayTao(LocalDateTime.now());
                    newGioHang.setNgaySua(LocalDateTime.now());
                    GioHang saved = gioHangRepository.save(newGioHang);
                    System.out.println("addToCart: Created GioHang with id=" + saved.getIdGH());
                    return saved;
                });

        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(idSpct)
                .orElseThrow(() -> {
                    RuntimeException e = new RuntimeException("Không tìm thấy sản phẩm chi tiết: " + idSpct);
                    System.out.println("addToCart: Error - " + e.getMessage());
                    return e;
                });

        GioHangCT gioHangCT = gioHangCTRepository.findByGioHangIdGHAndSanPhamChiTietId(gioHang.getIdGH(), idSpct)
                .orElseGet(() -> {
                    System.out.println("addToCart: Creating new GioHangCT for GioHang id=" + gioHang.getIdGH());
                    GioHangCT newChiTiet = new GioHangCT();
                    newChiTiet.setGioHang(gioHang);
                    newChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
                    newChiTiet.setSoLuong(0);
                    newChiTiet.setDonGia(sanPhamChiTiet.getGia());
                    return newChiTiet;
                });

        gioHangCT.setSoLuong(gioHangCT.getSoLuong() + soLuong);
        gioHangCT.setDonGia(sanPhamChiTiet.getGia());
        gioHangCTRepository.save(gioHangCT);
        System.out.println("addToCart: Saved GioHangCT with idGH=" + gioHang.getIdGH() + ", idSpct=" + idSpct);
    }

    public GioHangDTO create(GioHangDTO dto) {
        GioHang entity = toEntity(dto);
        GioHang saved = gioHangRepository.save(entity);
        return toDTO(saved);
    }

    public GioHangDTO getById(Integer id) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        return toDTO(entity);
    }

    public List<GioHangDTO> getAll() {
        return gioHangRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public GioHangDTO update(Integer id, GioHangDTO dto) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        entity.setMaGH(dto.getMaGH());
        entity.setKhachHang(dto.getIdKH() != null ? khachHangRepository.findById(dto.getIdKH()).orElse(null) : null);
        entity.setTaiKhoan(dto.getIdTK() != null ? taiKhoanRepository.findById(dto.getIdTK()).orElse(null) : null);
        entity.setTrangThai(dto.getTrangThai());
        entity.setNgaySua(LocalDateTime.now());
        GioHang updated = gioHangRepository.save(entity);
        return toDTO(updated);
    }

    public void delete(Integer id) {
        GioHang entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        gioHangRepository.delete(entity);
    }
}