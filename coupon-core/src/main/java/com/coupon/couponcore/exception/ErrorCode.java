package com.coupon.couponcore.exception;

public enum ErrorCode {
    INVALID_COUPON_ISSUE_QUANTITY("The number of coupons issued is invalid."),

    INVALID_COUPON_ISSUE_DATE("The coupon issuance period is invalid."),

    COUPON_NOT_EXIST("The coupon does not exist."),

    DUPLICATED_COUPON_ISSUE("The coupon has already been issued."),

    FAIL_COUPON_ISSUE_REQUEST("Failed to request coupon issuance.");

    public final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
