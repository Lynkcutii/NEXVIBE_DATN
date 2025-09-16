package com.example.datnspct.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Integer idTK;
    private ShippingInfo shippingInfo;
    private Integer paymentMethod;
    private BigDecimal total;
    private Integer idKM;
    private Integer idPT;
    private List<OrderItemDTO> items;
    private String loaiHoaDon;

    @Data
    public static class OrderItemDTO {
        private Integer idGHCT;
        private Integer idSPCT;
        private Integer soLuong;
        private BigDecimal donGia;
        private Integer idVoucher; // Thêm dòng này để truyền id voucher
    }
    @Data
    public static class ShippingInfo {
        private Integer addressId;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private String notes;
        private String email;
    }
}