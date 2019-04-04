package com.chujianyun.simpleretry;

import com.chujianyun.simpleretry.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 重试测试
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-04 10:42
 */
@Slf4j
public class OperationHelperTest {


    @Test(expected = Exception.class)
    public void executeWithRetry_Exception() throws Exception {
        OperationHelper.executeWithRetry(() -> {
            throw new Exception();
        }, 3, 5, TimeUnit.SECONDS);
    }

    @Test(expected = BusinessException.class)
    public void executeWithRetry_BusinessException() throws Exception {
        OperationHelper.executeWithRetry(() -> {
            throw new BusinessException();
        }, 3, 5, TimeUnit.SECONDS);
    }

    @Test
    public void executeWithRetry() throws Exception {
        Integer result = OperationHelper.executeWithRetry(() -> {
            // 随机数为奇数时报参数异常，会重试
            Random random = new Random();
            int nextInt = random.nextInt(100);
            if ((nextInt & 1) == 1) {
                log.debug("生成的随机数{}为奇数，报错后会触发重试", nextInt);
                throw new IllegalArgumentException();
            }
            return random.nextInt(5);
        }, 3, 5, TimeUnit.SECONDS);
        log.debug("最终返回值{}", result);
    }

}