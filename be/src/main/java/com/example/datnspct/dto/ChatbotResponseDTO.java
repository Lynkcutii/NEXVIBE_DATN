package com.example.datnspct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi serialize
public class ChatbotResponseDTO {
    private String answer; // Câu trả lời dạng text
    private List<SanPhamDTO> products; // Danh sách sản phẩm gợi ý (có thể null)
}
