//package com.example.datnspct.support.exception;
//
//import com.mbamc.common.error.ResponseError;
//import lombok.Getter;
//
//@Getter
//public enum BadRequestError implements ResponseError {
//    PRODUCT_NOT_EXISTED(40004001, "Customer not existed"),
//    PRODUCT_GROUP_NOT_EXISTED(40004002, "Customer group not existed"),
//    PRODUCT_GROUP_CODE_EXISTED(40004003, "Customer group code existed"),
//    PRODUCT_GROUP_ID_ALLOW_ADD_CUSTOMER_INVALID(40004004, "The customer group id that allows adding customers is not valid"),
//    PHONE_NUMBER_IN_CUSTOMER_GROUP_EXISTED(40004005, "The phone number in the customer group already exists"),
//    EMAIL_IN_CUSTOMER_GROUP_EXISTED(40004006, "The email in the customer group already exists"),
//    CMV_VALUATION_REPORT_NOT_EXISTED(40004007, "Valuation report not existed"),
//    PRODUCT_GROUP_USED(40004002, "Customer group used"),
//    PRODUCT_GROUP_INACTIVE(40004009, "Customer group inactive"),
//    OTP_INVALID(7884354, "Otp invalid"),
//    INVALID_DATE(4041012, "Invalid date"),
//    PAYMENT_NOT_EXISTED(4040404, "Payment method not allowed"),
//    INVOICE_NOT_EXISTED(40050511, "Invoice method not allowed"),
//    PROMOTION_NOT_EXISTED(4121401, "Promotion method not allowed"),
//    CLIENT_NOT_EXISTED(4123123, "Client method not allowed"),
//
//    ;
//
//    private final Integer code;
//    private final String message;
//
//    BadRequestError(Integer code, String message) {
//        this.code = code;
//        this.message = message;
//    }
//
//    @Override
//    public String getName() {
//        return name();
//    }
//
//    @Override
//    public String getMessage() {
//        return message;
//    }
//
//    @Override
//    public int getStatus() {
//        return 400;
//    }
//
//    @Override
//    public Integer getCode() {
//        return code;
//    }
//}
