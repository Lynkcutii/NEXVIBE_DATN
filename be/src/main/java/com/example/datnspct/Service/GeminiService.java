package com.example.datnspct.Service;
import com.example.datnspct.dto.ChatbotResponseDTO;
import com.example.datnspct.dto.SanPhamDTO;
import com.example.datnspct.dto.gemini.GeminiRequest;
import com.example.datnspct.dto.gemini.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired private RestTemplate restTemplate;
    @Autowired private SanPhamService sanPhamService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatbotResponseDTO getGenerativeResponse(String userInput) throws Exception {
        // Bước 1: Phân loại ý định và trích xuất từ khóa
        String intentPrompt = "Phân tích câu hỏi sau của khách hàng: \"" + userInput + "\". " +
                "Trả lời bằng một đối tượng JSON duy nhất. " +
                "Nếu câu hỏi là về tìm kiếm sản phẩm, trả về: {\"intent\": \"SEARCH_PRODUCT\", \"keyword\": \"<tên sản phẩm>\"}. " +
                "Nếu là câu hỏi thông thường, trả về: {\"intent\": \"GENERAL_QUESTION\"}.";

        String intentJson = callGemini(intentPrompt);
        intentJson = intentJson.replace("```json", "").replace("```", "").trim();
        Map<String, String> intentData = objectMapper.readValue(intentJson, Map.class);
        String intent = intentData.get("intent");

        ChatbotResponseDTO response = new ChatbotResponseDTO();

        if ("SEARCH_PRODUCT".equals(intent)) {
            String keyword = intentData.get("keyword");

            // Bước 2: Tìm kiếm sản phẩm trong CSDL
            List<SanPhamDTO> products = sanPhamService.searchProductsByKeyword(keyword);

            // Bước 3: Tổng hợp câu trả lời
            if (products.isEmpty()) {
                response.setAnswer("Xin lỗi, tôi không tìm thấy sản phẩm nào có tên giống '" + keyword + "'.");
            } else {
                String productListString = products.stream()
                        .map(p -> "- " + p.getTenSP() + " (Giá từ " + p.getMinPrice() + "đ)")
                        .collect(Collectors.joining("\n"));

                String finalPrompt = "Bạn là trợ lý ảo của shop NexVibe. Dựa vào danh sách sản phẩm tìm được sau:\n" +
                        productListString +
                        "\nHãy tạo một câu trả lời thân thiện để giới thiệu các sản phẩm này.";

                String textAnswer = callGemini(finalPrompt);

                response.setAnswer(textAnswer);
                response.setProducts(products);
            }
        } else {
            // Xử lý câu hỏi thông thường
            String generalPrompt = "Bạn là trợ lý ảo của shop NexVibe... Câu hỏi của khách hàng là: \"" + userInput + "\"";
            response.setAnswer(callGemini(generalPrompt));
        }

        return response;
    }

    // Hàm gọi API Gemini đã được tách ra
    private String callGemini(String prompt) {
        GeminiRequest.Part part = new GeminiRequest.Part();
        part.setText(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.setParts(Collections.singletonList(part));
        GeminiRequest request = new GeminiRequest();
        request.setContents(Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);
        String urlWithKey = apiUrl + "?key=" + apiKey;

        GeminiResponse response = restTemplate.postForObject(urlWithKey, entity, GeminiResponse.class);
        return response != null ? response.getFirstCandidateText() : "Xin lỗi, có lỗi xảy ra.";
    }
}
