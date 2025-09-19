package com.example.datnspct.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoMoPaymentResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String responseTime;
    private String message;
    private String resultCode;
    private String payUrl;
    private String qrCodeUrl;
    private String deeplink;
    private String deeplinkMiniApp;
}
