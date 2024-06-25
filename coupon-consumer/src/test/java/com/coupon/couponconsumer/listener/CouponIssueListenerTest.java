package com.coupon.couponconsumer.listener;

import com.coupon.couponconsumer.TestConfig;
import com.coupon.couponcore.repository.redis.RedisRepository;
import com.coupon.couponcore.service.CouponIssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    CouponIssueListener sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisRepository repository;

    @MockBean
    CouponIssueService couponIssueService;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }


    @Test
    @DisplayName("If there is no processing target in the coupon issuance queue, it is not issued.")
    void issue_1() throws JsonProcessingException {
        // when
        sut.issue();
        // then
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("If there is a processing target in the coupon issuance queue, it is issued.")
    void issue_2() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId = 1;
        int totalQuantity = Integer.MAX_VALUE;
        repository.issueRequest(couponId, userId, totalQuantity);

        // when
        sut.issue();
        // then
        verify(couponIssueService, times(1)).issue(couponId, userId);
    }

    @Test
    @DisplayName("Coupon issuance requests are processed in accordance with the order.")
    void issue_3() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;
        int totalQuantity = Integer.MAX_VALUE;
        repository.issueRequest(couponId, userId1, totalQuantity);
        repository.issueRequest(couponId, userId2, totalQuantity);
        repository.issueRequest(couponId, userId3, totalQuantity);

        // when
        sut.issue();
        // then
        InOrder inOrder = Mockito.inOrder(couponIssueService);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId2);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId3);
    }
}
