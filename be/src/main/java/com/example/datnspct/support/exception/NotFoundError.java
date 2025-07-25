//package com.example.datnspct.support.exception;
//
//import com.mbamc.common.error.ResponseError;
//
//public enum NotFoundError implements ResponseError {
//
//    PRODUCT_ID_IS_NULL(40404003, "User id is null: {}");
//
//    private final Integer code;
//    private final String message;
//
//    NotFoundError(Integer code, String message) {
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
//        return 404;
//    }
//
//    @Override
//    public Integer getCode() {
//        return code;
//    }
//}
