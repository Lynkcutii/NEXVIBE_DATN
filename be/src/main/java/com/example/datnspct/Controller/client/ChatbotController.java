package com.example.datnspct.Controller.client;
import com.example.datnspct.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.datnspct.dto.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<ChatbotResponseDTO> askBot(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        // Nếu câu hỏi rỗng, trả về một DTO với chỉ có answer
        if (question == null || question.trim().isEmpty()) {
            ChatbotResponseDTO errorResponse = new ChatbotResponseDTO();
            errorResponse.setAnswer("Vui lòng nhập một câu hỏi.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {

            ChatbotResponseDTO response = geminiService.getGenerativeResponse(question);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Rất quan trọng để xem lỗi ở console backend

            // Nếu có lỗi, trả về một DTO với chỉ có answer báo lỗi
            ChatbotResponseDTO errorResponse = new ChatbotResponseDTO();
            errorResponse.setAnswer("Xin lỗi, bot AI đang gặp sự cố. Vui lòng thử lại sau.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
