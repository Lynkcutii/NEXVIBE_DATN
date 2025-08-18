package com.example.datnspct.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoMoPaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String orderInfo;
    private String extraData;
    private String redirectUrl;
    private String ipnUrl;
}
