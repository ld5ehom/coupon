package com.coupon.couponcore.model;

import com.coupon.couponcore.exception.CouponIssueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.coupon.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.coupon.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

class CouponTest {

    @Test
    @DisplayName("Returns true if the issued quantity remains.")
    // Quantity Check
    void availableIssueQuantity_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();
        // when
        boolean result = coupon.availableIssueQuantity();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Returns false if the issued quantity is exhausted.")
    void availableIssueQuantity_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();
        // when
        boolean result = coupon.availableIssueQuantity();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Returns true if the maximum issued quantity is not set.")
    void availableIssueQuantity_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(100)
                .build();
        // when
        boolean result = coupon.availableIssueQuantity();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Returns false if the issuance period has not started.")
    // Date Check
    void availableIssueDate_1() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        boolean result = coupon.availableIssueDate();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Returns true if it falls within the issuance period.")
    void availableIssueDate_2() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        boolean result = coupon.availableIssueDate();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Returns false when the issuance period ends.")
    void availableIssueDate_3() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        // when
        boolean result = coupon.availableIssueDate();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Issuance is successful if the issued quantity and issuance period are valid.")
    // Issued Check
    void issue_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        coupon.issue();
        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("If the issued quantity is exceeded, an exception is returned.")
    void issue_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("If it is not within the issuance period, an exception is returned.")
    void issue_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("Returns true when the issuance period ends.")
    void isIssueComplete_1() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .totalQuantity(100)
                .issuedQuantity(0)
                .build();
        // when
        boolean result = coupon.isIssueComplete();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Returns true if there is no remaining issuance quantity.")
    void isIssueComplete_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        boolean result = coupon.isIssueComplete();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("If the issuance deadline and quantity are valid, returns false.")
    void isIssueComplete_3() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .totalQuantity(100)
                .issuedQuantity(0)
                .build();
        // when
        boolean result = coupon.isIssueComplete();
        // then
        Assertions.assertFalse(result);
    }
}