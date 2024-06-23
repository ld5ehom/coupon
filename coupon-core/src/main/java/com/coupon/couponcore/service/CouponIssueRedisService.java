package com.coupon.couponcore.service;

import com.coupon.couponcore.exception.CouponIssueException;
import com.coupon.couponcore.repository.redis.RedisRepository;
import com.coupon.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.coupon.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;
import static com.coupon.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static com.coupon.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantity(CouponRedisEntity coupon, long userId) {
        if (!availableUserIssueQuantity(coupon.id(), userId)) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "Exceeds the quantity that can be issued. couponId : %s, userId: %s".formatted(coupon.id(), userId));
        }
        if (!availableTotalIssueQuantity(coupon.totalQuantity(), coupon.id())) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY, "Exceeds the quantity that can be issued. couponId : %s, userId : %s".formatted(coupon.id(), userId));
        }
    }

    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }
        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}
