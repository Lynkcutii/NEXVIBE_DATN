package com.example.datnspct.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Integer idTK;
    private ShippingInfoDTO shippingInfo;
    private Integer paymentMethod;
    private String voucherCode;
    private BigDecimal total;
    private List<OrderItemDTO> items;

    @Data
    public static class ShippingInfoDTO {
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private String notes;
    }

    @Data
    public static class OrderItemDTO {
        private Integer idGHCT;
        private Integer idSPCT;
        private Integer soLuong;
        private BigDecimal donGia;
    }
}