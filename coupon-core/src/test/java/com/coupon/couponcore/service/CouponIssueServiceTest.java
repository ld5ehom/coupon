package com.coupon.couponcore.service;

import com.coupon.couponcore.TestConfig;
import com.coupon.couponcore.exception.CouponIssueException;
import com.coupon.couponcore.exception.ErrorCode;
import com.coupon.couponcore.model.Coupon;
import com.coupon.couponcore.model.CouponIssue;
import com.coupon.couponcore.model.CouponType;
import com.coupon.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.coupon.couponcore.repository.mysql.CouponIssueRepository;
import com.coupon.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.coupon.couponcore.exception.ErrorCode.*;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    CouponIssueService sut;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clean() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("If coupon issuance history exists, an exception is returned.")
    void saveCouponIssue_1() {
        // given
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(couponIssue);
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId());
        });
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATED_COUPON_ISSUE);
    }

    @Test
    @DisplayName("If there is no coupon issuance history, a coupon is issued.")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;
        // when
        CouponIssue result = sut.saveCouponIssue(couponId, userId);
        // then
        Assertions.assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("If there are no issues with the issuance quantity, expiration date, or duplicate issuance, the coupon will be issued.")
    void issue_1() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("First-come, first-served coupon test")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        // when
        sut.issue(coupon.getId(), userId);
        // then
        Coupon couponResult = couponJpaRepository.findById(coupon.getId()).get();
        Assertions.assertEquals(couponResult.getIssuedQuantity(), 1);

        CouponIssue couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
        Assertions.assertNotNull(couponIssueResult);
    }


    @Test
    @DisplayName("If there is an issue with the issuance quantity, return an exception.")
    void issue_2() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("First-come, first-served coupon test")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("If there is an issue with the issuance deadline, return an exception.")
    void issue_3() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("First-come, first-served coupon test")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("If there is a problem with duplicate issuance verification, return an exception.")
    void issue_4() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("First-come, first-served coupon test")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(couponIssue);

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), DUPLICATED_COUPON_ISSUE);
    }

    @Test
    @DisplayName("If the coupon does not exist, return an exception.")
    void issue_5() {
        // given
        long userId = 1;
        long couponId = 1;

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });
        Assertions.assertEquals(exception.getErrorCode(), COUPON_NOT_EXIST);
    }
}