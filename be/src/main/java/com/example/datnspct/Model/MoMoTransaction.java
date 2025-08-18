package com.example.datnspct.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MoMoTransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoMoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdMoMo")
    private Integer idMoMo;

    @Column(name = "IdHD")
    private Integer idHD;

    @Column(name = "OrderId", nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(name = "RequestId", nullable = false, unique = true, length = 100)
    private String requestId;

    @Column(name = "Amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "OrderInfo", nullable = false, length = 255)
    private String orderInfo;

    @Column(name = "RedirectUrl", length = 500)
    private String redirectUrl;

    @Column(name = "IpnUrl", length = 500)
    private String ipnUrl;

    @Column(name = "PayUrl", length = 1000)
    private String payUrl;

    @Column(name = "QrCodeUrl", length = 1000)
    private String qrCodeUrl;

    @Column(name = "DeepLink", length = 1000)
    private String deepLink;

    @Column(name = "TransId", length = 100)
    private String transId;

    @Column(name = "ResponseCode", length = 10)
    private String responseCode;

    @Column(name = "Message", length = 255)
    private String message;

    @Column(name = "LocalMessage", length = 255)
    private String localMessage;

    @Column(name = "PayType", length = 50)
    private String payType;

    @Column(name = "TransactionStatus", nullable = false, length = 50)
    private String transactionStatus = "PENDING";

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NgayCapNhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "ExtraData", columnDefinition = "NVARCHAR(MAX)")
    private String extraData;

    @Column(name = "Signature", length = 500)
    private String signature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdHD", referencedColumnName = "IdHD", insertable = false, updatable = false)
    private HoaDon hoaDon;
}
