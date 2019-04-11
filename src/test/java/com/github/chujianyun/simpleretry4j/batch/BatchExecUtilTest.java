package com.github.chujianyun.simpleretry4j.batch;


import com.github.chujianyun.simpleretry4j.RetryPolicy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-10 16:38
 */
@RunWith(MockitoJUnitRunner.class)
public class BatchExecUtilTest {

    @Mock
    private Consumer<List<Integer>> consumer;

    private List<Integer> data = new ArrayList<>();

    /**
     * 构造一个void的函数，打印参数
     */
    private Answer answer = (invocationOnMock -> {
        Object data = invocationOnMock.getArgument(0);
        System.out.println(data);
        return data;
    });

    @Before
    public void init() {

        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);
    }

    /**
     * 第一次扔异常被忽略   第二次正常打印
     */
    @Test
    public void batchExec() throws Exception {

        Mockito.doThrow(new RuntimeException("test"))
                .doAnswer(invocationOnMock -> {
                    Object data = invocationOnMock.getArgument(0);
                    System.out.println(data);
                    return data;
                }).when(consumer).accept(any());


        BatchExePolicy batchExePolicy = BatchExePolicy.builder()
                .eachBatchNum(2)
                .intervalDuration(Duration.ofSeconds(4))
                .failPolicy(FailPolicy.IGNORE)
                .build();

        BatchExecUtil.batchExec(consumer, data, batchExePolicy);
    }

    /**
     * 先抛出异常，导致后续都不执行
     */
    @Test(expected = RuntimeException.class)
    public void batchExecThrow() throws Exception {

        Mockito.doThrow(new RuntimeException("test2")).doAnswer(answer).when(consumer).accept(any());

        BatchExePolicy batchExePolicy = BatchExePolicy.builder()
                .eachBatchNum(2)
                .intervalDuration(Duration.ofSeconds(4))
                .failPolicy(FailPolicy.ABORT_AND_THROW)
                .build();

        BatchExecUtil.batchExec(consumer, data, batchExePolicy);
    }

    /**
     * 先抛出异常由于报错导致会重试
     */
    @Test
    public void batchExecRetry() throws Exception {

        // 第一次异常，第二次调用正常，第三次和第四次异常，第五次和以后正常
        Mockito.doThrow(new RuntimeException("ex1"))
                .doAnswer(answer)
                .doThrow(new RuntimeException("ex2"))
                .doThrow(new RuntimeException("ex3"))
                .doAnswer(answer)
                .when(consumer).accept(any());

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .abortException(IllegalArgumentException.class)
                .delayDuration(Duration.ofSeconds(2))
                .build();

        BatchExePolicy batchExePolicy = BatchExePolicy.builder()
                .eachBatchNum(2)
                .intervalDuration(Duration.ofSeconds(4))
                .failPolicy(FailPolicy.RETRY)
                .retryPolicy(retryPolicy)
                .build();

        BatchExecUtil.batchExec(consumer, data, batchExePolicy);
    }

}