package com.coupon.couponcore.repository.redis;

import com.coupon.couponcore.exception.CouponIssueException;
import com.coupon.couponcore.exception.ErrorCode;

public enum CouponIssueRequestCode {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);


    CouponIssueRequestCode(int code) {
    }

    public static CouponIssueRequestCode find(String code) {
        int codeValue = Integer.parseInt(code);
        if (codeValue == 1) return SUCCESS;
        if (codeValue == 2) return DUPLICATED_COUPON_ISSUE;
        if (codeValue == 3) return INVALID_COUPON_ISSUE_QUANTITY;
        throw new IllegalArgumentException("This code doesn't exist. %s".formatted(code));
    }

    public static void checkRequestResult(CouponIssueRequestCode code) {
        if (code == INVALID_COUPON_ISSUE_QUANTITY) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "Exceeds the quantity that can be issued");
        }
        if (code == DUPLICATED_COUPON_ISSUE) {
            throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "This coupon has already been issued.");
        }
    }
}
