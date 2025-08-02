package com.example.datnspct.Service;

import com.example.datnspct.Model.NhanVien;
import com.example.datnspct.Model.TaiKhoan;
import com.example.datnspct.Repository.NhanVienRepository;
import com.example.datnspct.Repository.login.TaiKhoanRepository;
import com.example.datnspct.dto.NhanVienDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Ánh xạ từ Entity sang DTO
    private NhanVienDTO convertToDTO(NhanVien nhanVien) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setIdNV(nhanVien.getIdNV());
        dto.setMaNV(nhanVien.getMaNV());
        dto.setTenNV(nhanVien.getTenNV());
        dto.setGioiTinh(nhanVien.getGioiTinh());
        dto.setNgaySinh(nhanVien.getNgaySinh());
        dto.setSdt(nhanVien.getSdt());
        dto.setDiaChi(nhanVien.getDiaChi());
        dto.setEmail(nhanVien.getEmail());
        dto.setTrangThai(nhanVien.getTrangThai());
        if (nhanVien.getTaiKhoan() != null) {
            dto.setTaiKhoan(nhanVien.getTaiKhoan());
        } else {
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setChucVu("UNKNOWN");
            dto.setTaiKhoan(taiKhoan);
        }
        return dto;
    }

    // Ánh xạ từ DTO sang Entity
    private NhanVien convertToEntity(NhanVienDTO dto) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setIdNV(dto.getIdNV());
        nhanVien.setMaNV(dto.getMaNV());
        nhanVien.setTenNV(dto.getTenNV());
        nhanVien.setGioiTinh(dto.getGioiTinh());
        nhanVien.setSdt(dto.getSdt());
        nhanVien.setDiaChi(dto.getDiaChi());
        nhanVien.setEmail(dto.getEmail());
        nhanVien.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true); // Mặc định trangThai là true
        nhanVien.setTaiKhoan(dto.getTaiKhoan());
        return nhanVien;
    }

    // Tạo mới
    public NhanVienDTO create(NhanVienDTO dto) {
        // Tự sinh maNV
        long count = nhanVienRepository.count();
        String maNV = String.format("NV%03d", count + 1); // Ví dụ: NV001, NV002, ...
        dto.setMaNV(maNV);
        // Đặt trạng thái mặc định là true nếu không được cung cấp
        if (dto.getTrangThai() == null) {
            dto.setTrangThai(true);
        }

        // Tạo TaiKhoan nếu cần
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setChucVu(dto.getTaiKhoan().getChucVu());
        taiKhoan = taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = convertToEntity(dto);
        nhanVien.setTaiKhoan(taiKhoan);
        NhanVien saved = nhanVienRepository.save(nhanVien);
        return convertToDTO(saved);
    }

    // Lấy theo ID
    public NhanVienDTO getById(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        return convertToDTO(nhanVien);
    }

    // Lấy tất cả
    public List<NhanVienDTO> getAll(Boolean trangThai, String keyword) {
        List<NhanVien> nhanViens = nhanVienRepository.findByTrangThaiAndTenNVOrSdt(
                trangThai,
                keyword == null || keyword.isEmpty() ? null : keyword
        );
        return nhanViens.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public NhanVienDTO update(Integer id, NhanVienDTO dto) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        nhanVien.setMaNV(dto.getMaNV());
        nhanVien.setTenNV(dto.getTenNV());
        nhanVien.setGioiTinh(dto.getGioiTinh());
        nhanVien.setNgaySinh(dto.getNgaySinh());
        nhanVien.setSdt(dto.getSdt());
        nhanVien.setDiaChi(dto.getDiaChi());
        nhanVien.setEmail(dto.getEmail());
        nhanVien.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        nhanVien.setTaiKhoan(dto.getTaiKhoan());
        NhanVien updated = nhanVienRepository.save(nhanVien);
        return convertToDTO(updated);
    }

    // Chuyển đổi trạng thái
    public NhanVienDTO toggleStatus(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        nhanVien.setTrangThai(!nhanVien.getTrangThai());
        NhanVien updated = nhanVienRepository.save(nhanVien);
        return convertToDTO(updated);
    }
}