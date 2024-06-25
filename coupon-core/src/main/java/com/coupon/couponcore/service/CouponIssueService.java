package com.coupon.couponcore.service;

import com.coupon.couponcore.exception.CouponIssueException;
import com.coupon.couponcore.model.Coupon;
import com.coupon.couponcore.model.CouponIssue;
import com.coupon.couponcore.model.event.CouponIssueCompleteEvent;
import com.coupon.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.coupon.couponcore.repository.mysql.CouponIssueRepository;
import com.coupon.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.coupon.couponcore.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.coupon.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCouponWithLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);  // User Date Save
        publishCouponEvent(coupon);
    }

    @Transactional(readOnly = true)
    // Check Coupon Availability
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() -> {
            throw new CouponIssueException(COUPON_NOT_EXIST, "Coupon policy does not exist. %s".formatted(couponId));
        });
    }

    // Control Critical Section
    @Transactional
    public Coupon findCouponWithLock(long couponId) {
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow(() -> {
            throw new CouponIssueException(COUPON_NOT_EXIST, "Coupon policy does not exist. %s".formatted(couponId));
        });
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId, userId);
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(couponIssue);
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        if (issue != null) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "This coupon has already been issued. user_id: %d, coupon_id: %d".formatted(userId, couponId));
        }
    }

    private void publishCouponEvent(Coupon coupon) {
        if (coupon.isIssueComplete()) {
            applicationEventPublisher.publishEvent(new CouponIssueCompleteEvent(coupon.getId()));
        }
    }
}
