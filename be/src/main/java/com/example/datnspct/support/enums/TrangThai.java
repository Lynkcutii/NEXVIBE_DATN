package com.example.datnspct.support.enums;


public enum TrangThai {
    INACTIVE(0),
    ACTIVE(1);

    private final int value;

    TrangThai(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TrangThai fromValue(int value) {
        for (TrangThai status : TrangThai.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
