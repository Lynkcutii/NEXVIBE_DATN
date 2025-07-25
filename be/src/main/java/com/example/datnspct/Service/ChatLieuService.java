package com.example.datnspct.Service;

import com.example.datnspct.dto.ChatLieuDTO;
import com.example.datnspct.Model.ChatLieu;
import com.example.datnspct.Repository.ChatLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatLieuService {
    @Autowired
    private ChatLieuRepository repository;

    // Entity -> DTO
    private ChatLieuDTO toDTO(ChatLieu entity) {
        ChatLieuDTO dto = new ChatLieuDTO();
        dto.setIdChatLieu(entity.getIdChatLieu());
        dto.setMaChatLieu(entity.getMaChatLieu());
        dto.setTenChatLieu(entity.getTenChatLieu());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    // DTO -> Entity
    private ChatLieu toEntity(ChatLieuDTO dto) {
        ChatLieu entity = new ChatLieu();
        entity.setIdChatLieu(dto.getIdChatLieu());
        entity.setMaChatLieu(dto.getMaChatLieu());
        entity.setTenChatLieu(dto.getTenChatLieu());
        entity.setTrangThai(dto.getTrangThai());
        return entity;
    }

    // Tạo mới
    public ChatLieuDTO create(ChatLieuDTO dto) {
        ChatLieu entity = toEntity(dto);
        ChatLieu saved = repository.save(entity);
        return toDTO(saved);
    }

    // Lấy theo ID
    public ChatLieuDTO getById(Integer id) {
        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu"));
        return toDTO(entity);
    }

    // Lấy tất cả
    public List<ChatLieuDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Cập nhật
    public ChatLieuDTO update(Integer id, ChatLieuDTO dto) {
        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu"));
        entity.setMaChatLieu(dto.getMaChatLieu());
        entity.setTenChatLieu(dto.getTenChatLieu());
        entity.setTrangThai(dto.getTrangThai());
        ChatLieu updated = repository.save(entity);
        return toDTO(updated);
    }

    // Xóa
    public void delete(Integer id) {
        ChatLieu entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu"));
        repository.delete(entity);
    }
} 