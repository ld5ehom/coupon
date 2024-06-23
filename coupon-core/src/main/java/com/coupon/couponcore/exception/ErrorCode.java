package com.coupon.couponcore.exception;

public enum ErrorCode {
//  쿠폰 발급 수량이 유효하지 않습니다
    INVALID_COUPON_ISSUE_QUANTITY("The number of coupons issued is invalid."),

//  쿠폰 발급 기간이 유효하지 않습니다
    INVALID_COUPON_ISSUE_DATE("The coupon issuance period is invalid."),

//  존재하지 않는 쿠폰
    COUPON_NOT_EXIST("The coupon does not exist."),

//  이미 발급된 쿠폰입니다
    DUPLICATED_COUPON_ISSUE("The coupon has already been issued."),

//  쿠폰 발급 요청에 실패
    FAIL_COUPON_ISSUE_REQUEST("Failed to request coupon issuance.");

    public final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
