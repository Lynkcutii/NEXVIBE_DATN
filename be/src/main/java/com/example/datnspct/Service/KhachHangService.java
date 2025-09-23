package com.example.datnspct.Service;

import com.example.datnspct.Model.DiaChiKhachHang;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.DiaChiKhachHangRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.DiaChiKhachHangDTO;
import com.example.datnspct.dto.KhachHangDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    // Ánh xạ thủ công: Từ Entity sang DTO
    private KhachHangDTO chuyenSangDTO(KhachHang khachHang) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setIdKH(khachHang.getIdKH());
        dto.setMaKH(khachHang.getMaKH());
        dto.setTenKH(khachHang.getTenKH());
        dto.setGioiTinh(khachHang.getGioiTinh());
        dto.setNgaySinh(khachHang.getNgaySinh());
        dto.setEmail(khachHang.getEmail());
        dto.setSdt(khachHang.getSdt());
        dto.setIdTK(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getIdTK() : null);
        dto.setTrangThai(khachHang.getTrangThai());
        dto.setDiaChi(khachHang.getDiaChi());
        return dto;
    }

    // Ánh xạ thủ công: Từ DTO sang Entity
    private KhachHang chuyenSangEntity(KhachHangDTO dto) {
        KhachHang khachHang = new KhachHang();
        khachHang.setIdKH(dto.getIdKH());
        khachHang.setMaKH(dto.getMaKH());
        khachHang.setTenKH(dto.getTenKH());
        khachHang.setGioiTinh(dto.getGioiTinh());
        khachHang.setNgaySinh(dto.getNgaySinh());
        khachHang.setEmail(dto.getEmail());
        khachHang.setSdt(dto.getSdt());
        khachHang.setDiaChi(dto.getDiaChi());
        if (dto.getIdTK() != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            khachHang.setTaiKhoan(taiKhoan);
        }
        khachHang.setTrangThai(dto.getTrangThai());
        return khachHang;
    }

    // Ánh xạ DiaChiKhachHang sang DiaChiKhachHangDTO
    private DiaChiKhachHangDTO chuyenDiaChiSangDTO(DiaChiKhachHang diaChi) {
        DiaChiKhachHangDTO dto = new DiaChiKhachHangDTO();
        dto.setIdDiaChi(diaChi.getIdDiaChi());
        dto.setIdKH(diaChi.getKhachHang().getIdKH());
        dto.setDiaChiCuThe(diaChi.getDiaChiCuThe());
        dto.setTinhThanh(diaChi.getTinhThanh());
        dto.setPhuongXa(diaChi.getPhuongXa());
        dto.setSoDienThoai(diaChi.getSoDienThoai());
        dto.setGhiChu(diaChi.getGhiChu());
        dto.setTrangThai(diaChi.getTrangThai());
        return dto;
    }

    // Ánh xạ DiaChiKhachHangDTO sang DiaChiKhachHang
    private DiaChiKhachHang chuyenDiaChiSangEntity(DiaChiKhachHangDTO dto, KhachHang khachHang) {
        DiaChiKhachHang diaChi = new DiaChiKhachHang();
        diaChi.setIdDiaChi(dto.getIdDiaChi());
        diaChi.setKhachHang(khachHang);
        diaChi.setDiaChiCuThe(dto.getDiaChiCuThe());
        diaChi.setTinhThanh(dto.getTinhThanh());
        diaChi.setPhuongXa(dto.getPhuongXa());
        diaChi.setSoDienThoai(dto.getSoDienThoai());
        diaChi.setGhiChu(dto.getGhiChu());
        diaChi.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        return diaChi;
    }

    // Tạo mới khách hàng
    @Transactional
    public KhachHangDTO taoKhachHang(KhachHangDTO dto) {
        KhachHang khachHang = chuyenSangEntity(dto);
        KhachHang khachHangDaLuu = khachHangRepository.save(khachHang);
        // Lưu danh sách địa chỉ nếu có
        if (dto.getDiaChiList() != null && !dto.getDiaChiList().isEmpty()) {
            for (DiaChiKhachHangDTO diaChiDTO : dto.getDiaChiList()) {
                DiaChiKhachHang diaChi = chuyenDiaChiSangEntity(diaChiDTO, khachHangDaLuu);
                diaChiKhachHangRepository.save(diaChi);
            }
        }
        return chuyenSangDTO(khachHangDaLuu);
    }

    // Lấy khách hàng theo ID
    public KhachHangDTO layKhachHangTheoId(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tìm thấy"));
        return chuyenSangDTO(khachHang);
    }

    // Lấy khách hàng theo IdTK
    public KhachHangDTO layKhachHangTheoTaiKhoanId(Integer idTK) {
        KhachHang khachHang = khachHangRepository.findByTaiKhoanIdTK(idTK)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tìm thấy với idTK=" + idTK));
        return chuyenSangDTO(khachHang);
    }

    // Lấy tất cả khách hàng
    public Page<KhachHangDTO> layTatCaKhachHang(Pageable pageable) {
        return khachHangRepository.findAll(pageable).map(this::chuyenSangDTO);
    }

    // Tìm kiếm khách hàng
    public Page<KhachHangDTO> searchKhachHang(String keyword, Pageable pageable) {
        return khachHangRepository.findByTenKHContainingIgnoreCaseOrSdtContaining(keyword, keyword, pageable)
                .map(this::chuyenSangDTO);
    }

    // Cập nhật khách hàng
    @Transactional
    public KhachHangDTO capNhatKhachHang(Integer id, KhachHangDTO dto) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        khachHang.setMaKH(dto.getMaKH());
        khachHang.setTenKH(dto.getTenKH());
        khachHang.setGioiTinh(dto.getGioiTinh());
        khachHang.setNgaySinh(dto.getNgaySinh());
        khachHang.setEmail(dto.getEmail());
        khachHang.setSdt(dto.getSdt());
        if (dto.getIdTK() != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getIdTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            khachHang.setTaiKhoan(taiKhoan);
        } else {
            khachHang.setTaiKhoan(null);
        }
        khachHang.setTrangThai(dto.getTrangThai());
        KhachHang khachHangDaCapNhat = khachHangRepository.save(khachHang);
        // Cập nhật danh sách địa chỉ
        if (dto.getDiaChiList() != null) {
            // Xóa các địa chỉ cũ
            diaChiKhachHangRepository.deleteByKhachHangIdKH(id);
            // Thêm các địa chỉ mới
            for (DiaChiKhachHangDTO diaChiDTO : dto.getDiaChiList()) {
                DiaChiKhachHang diaChi = chuyenDiaChiSangEntity(diaChiDTO, khachHangDaCapNhat);
                diaChiKhachHangRepository.save(diaChi);
            }
        }
        return chuyenSangDTO(khachHangDaCapNhat);
    }

    // Xóa khách hàng
    @Transactional
    public void xoaKhachHang(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        // Xóa các địa chỉ liên quan
        diaChiKhachHangRepository.deleteByKhachHangIdKH(id);
        khachHangRepository.delete(khachHang);
    }

    // Thêm địa chỉ mới cho khách hàng
    @Transactional
    public DiaChiKhachHangDTO themDiaChi(Integer idKH, DiaChiKhachHangDTO diaChiDTO) {
        KhachHang khachHang = khachHangRepository.findById(idKH)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        DiaChiKhachHang diaChi = chuyenDiaChiSangEntity(diaChiDTO, khachHang);
        DiaChiKhachHang diaChiDaLuu = diaChiKhachHangRepository.save(diaChi);
        return chuyenDiaChiSangDTO(diaChiDaLuu);
    }

    // Cập nhật địa chỉ
    @Transactional
    public DiaChiKhachHangDTO capNhatDiaChi(Integer idDiaChi, DiaChiKhachHangDTO diaChiDTO) {
        DiaChiKhachHang diaChi = diaChiKhachHangRepository.findById(idDiaChi)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        KhachHang khachHang = khachHangRepository.findById(diaChiDTO.getIdKH())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        diaChi.setKhachHang(khachHang);
        diaChi.setDiaChiCuThe(diaChiDTO.getDiaChiCuThe());
        diaChi.setTinhThanh(diaChiDTO.getTinhThanh());
        diaChi.setPhuongXa(diaChiDTO.getPhuongXa());
        diaChi.setSoDienThoai(diaChiDTO.getSoDienThoai());
        diaChi.setGhiChu(diaChiDTO.getGhiChu());
        diaChi.setTrangThai(diaChiDTO.getTrangThai() != null ? diaChiDTO.getTrangThai() : true);
        DiaChiKhachHang diaChiDaCapNhat = diaChiKhachHangRepository.save(diaChi);
        return chuyenDiaChiSangDTO(diaChiDaCapNhat);
    }

    // Xóa địa chỉ
    @Transactional
    public void xoaDiaChi(Integer idDiaChi) {
        DiaChiKhachHang diaChi = diaChiKhachHangRepository.findById(idDiaChi)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        diaChiKhachHangRepository.delete(diaChi);
    }

    // Lấy danh sách địa chỉ theo IdKH
    public List<DiaChiKhachHangDTO> layDiaChiTheoKhachHangId(Integer idKH) {
        return diaChiKhachHangRepository.findByKhachHangIdKH(idKH)
                .stream()
                .map(this::chuyenDiaChiSangDTO)
                .collect(Collectors.toList());
    }
}