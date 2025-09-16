package com.example.datnspct.Controller.client;

import com.example.datnspct.Service.HoaDonService;
import com.example.datnspct.dto.HoaDonDTO;
import com.example.datnspct.dto.OrderDetailResponseDTO;
import com.example.datnspct.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/api/hoadon")
public class HoaDonClientController {

    @Autowired
    private HoaDonService hoaDonService;
    @GetMapping("/customer/{idKH}")
    public ResponseEntity<List<HoaDonDTO>> getHoaDonByKhachHangId(@PathVariable Integer idKH) {
        try {
            // Giả sử bạn có một phương thức trong service để tìm hóa đơn theo id khách hàng
            List<HoaDonDTO> hoaDons = hoaDonService.findByKhachHangId(idKH);
            return ResponseEntity.ok(hoaDons);
        } catch (Exception e) {
            // Nếu có lỗi, trả về một danh sách rỗng hoặc một lỗi server
            // Ở đây trả về danh sách rỗng để frontend không bị lỗi
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    @GetMapping("/{idHD}")
    public ResponseEntity<?> getHoaDonDetail(@PathVariable("idHD") Integer idHD) {
        try {
            OrderDetailResponseDTO responseData = hoaDonService.getHoaDonDetail(idHD);
            return ResponseEntity.ok(responseData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
    @PostMapping
    public ResponseEntity<?> createHoaDon(@RequestBody OrderRequestDTO request) {
        try {
            List<String> errors = new ArrayList<>();
            // Kiểm tra dữ liệu đầu vào
            if (request == null) {
                errors.add("Yêu cầu không hợp lệ: Dữ liệu rỗng");
            } else {
                if (request.getIdTK() == null) errors.add("ID tài khoản (idTK) là bắt buộc");
                if (request.getShippingInfo() == null) errors.add("Thông tin vận chuyển (shippingInfo) là bắt buộc");
                else {
                    if (request.getShippingInfo().getFirstName() == null || request.getShippingInfo().getFirstName().isEmpty())
                        errors.add("Họ (firstName) là bắt buộc");
                    if (request.getShippingInfo().getLastName() == null || request.getShippingInfo().getLastName().isEmpty())
                        errors.add("Tên (lastName) là bắt buộc");
                    if (request.getShippingInfo().getPhone() == null || request.getShippingInfo().getPhone().isEmpty())
                        errors.add("Số điện thoại (phone) là bắt buộc");
                    else if (!request.getShippingInfo().getPhone().matches("^[0-9]{10}$"))
                        errors.add("Số điện thoại phải có 10 chữ số");
                    if (request.getShippingInfo().getAddress() == null || request.getShippingInfo().getAddress().isEmpty())
                        errors.add("Địa chỉ (address) là bắt buộc");
                }
                if (request.getPaymentMethod() == null) errors.add("Phương thức thanh toán (paymentMethod) là bắt buộc");
                if (request.getItems() == null || request.getItems().isEmpty())
                    errors.add("Danh sách sản phẩm (items) không được rỗng");
                else {
                    for (int i = 0; i < request.getItems().size(); i++) {
                        OrderRequestDTO.OrderItemDTO item = request.getItems().get(i);
                        if (item.getIdGHCT() == null) errors.add("ID giỏ hàng chi tiết (idGHCT) của sản phẩm " + (i + 1) + " là bắt buộc");
                        if (item.getIdSPCT() == null) errors.add("ID sản phẩm chi tiết (idSPCT) của sản phẩm " + (i + 1) + " là bắt buộc");
                        if (item.getSoLuong() == null || item.getSoLuong() <= 0)
                            errors.add("Số lượng (soLuong) của sản phẩm " + (i + 1) + " phải lớn hơn 0");
                        if (item.getDonGia() == null || item.getDonGia().compareTo(BigDecimal.ZERO) <= 0)
                            errors.add("Đơn giá (donGia) của sản phẩm " + (i + 1) + " phải lớn hơn 0");
                    }
                }
                if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0)
                    errors.add("Tổng tiền (total) phải lớn hơn 0");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Dữ liệu yêu cầu không hợp lệ", errors));
            }

            var hoaDon = hoaDonService.createHoaDon(request);
            HoaDonDTO dto = new HoaDonDTO();
            dto.setIdHD(hoaDon.getIdHD());
            dto.setMaHD(hoaDon.getMaHD());
            dto.setIdNhanVien(hoaDon.getIdNhanVien());
            dto.setNgayTao(hoaDon.getNgayTao());
            dto.setNgaySua(hoaDon.getNgaySua());
            dto.setTongTien(hoaDon.getTongTien());
            dto.setTrangThai(hoaDon.getTrangThai());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi khi tạo hóa đơn", Arrays.asList(e.getMessage().split("; "))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi không xác định", Arrays.asList(e.getMessage())));
        }
    }

    public static class ErrorResponse {
        private String message;
        private List<String> details;

        public ErrorResponse(String message, List<String> details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(List<String> details) {
            this.details = details;
        }
    }
    @PutMapping("/{idHD}/cancel")
    public ResponseEntity<?> cancelHoaDon(@PathVariable Integer idHD) {
        try {
            hoaDonService.cancelOrder(idHD);
            return ResponseEntity.ok(Map.of("message", "Đã hủy đơn hàng thành công."));
        } catch (EntityNotFoundException e) {
            // Nếu không tìm thấy, trả về lỗi 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Nếu trạng thái không hợp lệ, trả về lỗi 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Các lỗi khác, trả về lỗi 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Đã xảy ra lỗi không mong muốn."));
        }
    }
    @PutMapping("/{idHD}/address")
    public ResponseEntity<?> changeOrderAddress(
            @PathVariable Integer idHD,
            @RequestBody Map<String, Integer> payload) {

        try {
            Integer newAddressId = payload.get("addressId");
            if (newAddressId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng cung cấp 'addressId'."));
            }

            hoaDonService.changeOrderAddress(idHD, newAddressId);
            return ResponseEntity.ok(Map.of("message", "Đã cập nhật địa chỉ giao hàng thành công."));

        } catch (EntityNotFoundException e) {
            // Lỗi không tìm thấy (hóa đơn hoặc địa chỉ) -> trả về 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Lỗi trạng thái không hợp lệ hoặc địa chỉ không đúng khách hàng -> trả về 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Các lỗi không lường trước
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Đã xảy ra lỗi không mong muốn."));
        }
    }
}