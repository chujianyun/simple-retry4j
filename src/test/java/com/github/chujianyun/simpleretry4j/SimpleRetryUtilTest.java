package com.github.chujianyun.simpleretry4j;

import com.github.chujianyun.simpleretry4j.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;

/**
 * 重试测试
 *
 * @author: 明明如月 liuwangyangedu@163.com
 * @date: 2019-04-04 10:42
 */
@Slf4j
@RunWith(PowerMockRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleRetryUtilTest {

    @Mock
    private Callable<Integer> callable;

    @Mock
    private Consumer<List<Integer>> consumer;

    /**
     * 提供两种设置延时时间的方法
     */
    @Test
    public void delayDuration() {
        RetryPolicy retryPolicy1 = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(5))
                .build();

        RetryPolicy retryPolicy2 = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(5, TimeUnit.MILLISECONDS)
                .build();
        Assert.assertEquals(retryPolicy1.getDelayDuration(), retryPolicy2.getDelayDuration());
    }

    /**
     * 模拟异常重试
     */
    @Test(expected = Exception.class)
    public void executeWithRetry_Exception() throws Exception {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .build();

        Mockito.doThrow(new Exception("test")).when(callable).call();

        SimpleRetryUtil.executeWithRetry(callable, retryPolicy);
    }

    /**
     * 模拟异常重试
     */
    @Test(expected = BusinessException.class)
    public void executeWithRetry_BusinessException() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(100))
                .build();

        Mockito.doThrow(new BusinessException()).when(callable).call();

        SimpleRetryUtil.executeWithRetry(callable, retryPolicy);
    }

    /**
     * 模拟终止异常不重试
     */
    @Test(expected = IllegalArgumentException.class)
    public void executeWithAbortException() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(100))
                .abortException(IllegalArgumentException.class)
                .abortException(BusinessException.class)
                .build();

            Mockito.doThrow(new IllegalArgumentException()).doReturn(1).when(callable).call();

            Integer result = SimpleRetryUtil.executeWithRetry(callable, retryPolicy);
            log.debug("最终返回值{}", result);
    }

    /**
     * 模拟不在终止异常触发重试
     */
    @Test
    public void executeWithAbortException2() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(100))
                .abortException(BusinessException.class)
                .build();

        Mockito.doThrow(new NullPointerException()).doReturn(1).when(callable).call();

        Integer result = SimpleRetryUtil.executeWithRetry(callable, retryPolicy);
        log.debug("最终返回值{}", result);
    }

    /**
     * 满足条件的返回值不重试的设置
     */
    @Test
    public void executeWithAbortCondition() throws Exception {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(100))
                .abortCondition(Objects::nonNull)
                .build();

        //前两次返回null 需要重试
        Mockito.doReturn(null).doReturn(null).doReturn(1).when(callable).call();

        Integer result = SimpleRetryUtil.executeWithRetry(callable, retryPolicy);
        log.debug("最终返回值{}", result);
    }

    /**
     * 测试无返回值的情况
     */
    @Test
    public void consumerTest() throws Exception {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .delayDuration(Duration.ofMillis(100))
                .build();
        List<Integer> data = new ArrayList<>(4);
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);

        Mockito.doThrow(new RuntimeException("测试")).doThrow(new RuntimeException("测试2")).doAnswer(invocationOnMock -> {
            Object param = invocationOnMock.getArgument(0);
            System.out.println("消费成功，列表个数" + ((List) param).size());
            return param;
        }).when(consumer).accept(any());

        SimpleRetryUtil.executeWithRetry(consumer, data, retryPolicy);
    }


}