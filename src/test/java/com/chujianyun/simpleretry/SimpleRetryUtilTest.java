package com.chujianyun.simpleretry;

import com.chujianyun.simpleretry.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 重试测试
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-04 10:42
 */
@Slf4j
public class SimpleRetryUtilTest {


    @Test(expected = Exception.class)
    public void executeWithRetry_Exception() throws Exception {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .build();

        SimpleRetryUtil.executeWithRetry(() -> {
            throw new Exception();
        }, retryPolicy);
    }

    @Test(expected = BusinessException.class)
    public void executeWithRetry_BusinessException() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofSeconds(5))
                .build();

        SimpleRetryUtil.executeWithRetry(() -> {
            throw new BusinessException();
        }, retryPolicy);
    }

    @Test
    public void executeWithAbortException() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofSeconds(5))
                .abortException(IllegalArgumentException.class)
                .abortException(BusinessException.class)
                .build();

        Integer result = SimpleRetryUtil.executeWithRetry(() -> {
            // 随机数为奇数时报参数异常，会重试
            Random random = new Random();
            int nextInt = random.nextInt(100);
            if ((nextInt & 1) == 1) {
                log.debug("生成的随机数{}为奇数，异常在不重试异常列表中，报错后不会触发重试", nextInt);
                throw new IllegalArgumentException();
            }
            return random.nextInt(5);
        }, retryPolicy);
        log.debug("最终返回值{}", result);
    }

    @Test
    public void executeWithAbortException2() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofSeconds(5))
                .abortException(BusinessException.class)
                .build();


        Integer result = SimpleRetryUtil.executeWithRetry(() -> {
            // 随机数为奇数时报参数异常，会重试
            Random random = new Random();
            int nextInt = random.nextInt(100);
            if ((nextInt & 1) == 1) {
                log.debug("生成的随机数{}为奇数，异常不在不重试异常列表中，报错后仍然会触发重试", nextInt);
                throw new NullPointerException();
            }
            return random.nextInt(5);
        }, retryPolicy);
        log.debug("最终返回值{}", result);
    }

    @Test
    public void executeWithAbortCondition() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofSeconds(5))
                .abortCondition(Objects::nonNull)
                .build();

        Integer result = SimpleRetryUtil.executeWithRetry(() -> {
            // 随机数为奇数时报参数异常，会重试
            Random random = new Random();
            int nextInt = random.nextInt(100);
            if ((nextInt & 1) == 1) {
                log.debug("随机数为{}，返回null，会触发重试", nextInt);
                return null;
            }
            return random.nextInt(5);
        }, retryPolicy);
        log.debug("最终返回值{}", result);
    }


}