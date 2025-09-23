package com.example.datnspct.Service;

import com.example.datnspct.Model.DiaChiKhachHang;
import com.example.datnspct.Model.KhachHang;
import com.example.datnspct.Repository.DiaChiKhachHangRepository;
import com.example.datnspct.Repository.KhachHangRepository;
import com.example.datnspct.dto.DiaChiKhachHangDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaChiKhachHangService {

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository; // Cần repo này

    public List<DiaChiKhachHangDTO> findByKhachHangId(Integer idKH) {
        List<DiaChiKhachHang> diaChis = diaChiKhachHangRepository.findByKhachHangIdKH(idKH);
        return diaChis.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    // --- HÀM HELPER
    private void resetDefaultAddresses(Integer idKH) {
        List<DiaChiKhachHang> allAddresses = diaChiKhachHangRepository.findByKhachHangIdKH(idKH);
        for (DiaChiKhachHang addr : allAddresses) {
            if (Boolean.TRUE.equals(addr.getTrangThai())) {
                addr.setTrangThai(Boolean.FALSE);
                diaChiKhachHangRepository.save(addr);
            }
        }
    }

    // =======================================================
    // === PHƯƠNG THỨC BỊ THIẾU: TẠO MỚI ĐỊA CHỈ ===
    // =======================================================
    @Transactional
    public DiaChiKhachHangDTO createAddress(DiaChiKhachHangDTO dto) {
        KhachHang khachHang = khachHangRepository.findById(dto.getIdKH())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + dto.getIdKH()));

        // Nếu địa chỉ mới được đặt làm mặc định, hãy bỏ mặc định của các địa chỉ cũ
        if (Boolean.TRUE.equals(dto.getTrangThai())) {
            resetDefaultAddresses(dto.getIdKH());
        }

        DiaChiKhachHang newAddress = new DiaChiKhachHang();
        newAddress.setKhachHang(khachHang);
        newAddress.setDiaChiCuThe(dto.getDiaChiCuThe());
        newAddress.setPhuongXa(dto.getPhuongXa());
        newAddress.setTinhThanh(dto.getTinhThanh());
        newAddress.setSoDienThoai(dto.getSoDienThoai());
        newAddress.setTrangThai(dto.getTrangThai());

        DiaChiKhachHang savedAddress = diaChiKhachHangRepository.save(newAddress);
        return convertToDto(savedAddress);
    }


    // =======================================================
    // === PHƯƠNG THỨC BỊ THIẾU: CẬP NHẬT ĐỊA CHỈ ===
    // =======================================================
    @Transactional
    public DiaChiKhachHangDTO updateAddress(Integer idDiaChi, DiaChiKhachHangDTO dto) {
        DiaChiKhachHang existingAddress = diaChiKhachHangRepository.findById(idDiaChi)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ với ID: " + idDiaChi));

        // Nếu địa chỉ này được đặt làm mặc định
        if (Boolean.TRUE.equals(dto.getTrangThai())) {
            // Lấy idKH từ địa chỉ đã tồn tại để đảm bảo an toàn
            resetDefaultAddresses(existingAddress.getKhachHang().getIdKH());
        }

        existingAddress.setDiaChiCuThe(dto.getDiaChiCuThe());
        existingAddress.setPhuongXa(dto.getPhuongXa());
        existingAddress.setTinhThanh(dto.getTinhThanh());
        existingAddress.setSoDienThoai(dto.getSoDienThoai());
        existingAddress.setTrangThai(dto.getTrangThai());

        DiaChiKhachHang updatedAddress = diaChiKhachHangRepository.save(existingAddress);
        return convertToDto(updatedAddress);
    }
    // Hàm helper để convert Entity sang DTO
    private DiaChiKhachHangDTO convertToDto(DiaChiKhachHang diaChi) {
        DiaChiKhachHangDTO dto = new DiaChiKhachHangDTO();
        dto.setIdDiaChi(diaChi.getIdDiaChi());
        dto.setDiaChiCuThe(diaChi.getDiaChiCuThe());
        dto.setPhuongXa(diaChi.getPhuongXa());
        dto.setPhuongXa(diaChi.getPhuongXa());
        dto.setTinhThanh(diaChi.getTinhThanh());
        dto.setSoDienThoai(diaChi.getSoDienThoai());
        dto.setTrangThai(diaChi.getTrangThai());
        if (diaChi.getKhachHang() != null) {
            dto.setIdKH(diaChi.getKhachHang().getIdKH());
        }
        return dto;
    }
}