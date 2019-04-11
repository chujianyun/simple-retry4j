package com.chujianyun.simpleretry.batch;

import com.chujianyun.simpleretry.SimpleRetryUtil;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 分批执行工具类
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-10 14:54
 */
public class BatchExecUtil {

    /**
     * 批量消费/执行某方法,支持指定每个批次的数量和延时
     *
     * @param consumer       消费方法
     * @param data           数据
     * @param batchExePolicy 批量执行的策略
     */
    public static <T> void batchExec(Consumer<List<T>> consumer, List<T> data, BatchExePolicy batchExePolicy) throws Exception {
        checkParams(consumer, data, batchExePolicy);

        Integer eachBatchNum = batchExePolicy.getEachBatchNum();

        Duration intervalDuration = batchExePolicy.getIntervalDuration();

        FailPolicy failPolicy = batchExePolicy.getFailPolicy();

        int currentIndex = 0;
        while (currentIndex < data.size() - 1) {
            // 获取当前批次的数据
            List<T> batchData = data.stream().skip(currentIndex).limit(eachBatchNum).collect(Collectors.toList());
            // 每个批次执行
            if (failPolicy.equals(FailPolicy.IGNORE)) {
                try {
                    consumer.accept(batchData);
                } catch (Exception e) {
                    // ignore策略忽略当前这轮的错误
                }
            } else if (failPolicy.equals(FailPolicy.ABORT_AND_THROW)) {
                try {
                    consumer.accept(batchData);
                } catch (Exception e) {
                    // 抛异常，终止
                    throw e;
                }
            } else if (failPolicy.equals(FailPolicy.RETRY)) {
                SimpleRetryUtil.executeWithRetry(consumer, batchData, batchExePolicy.getRetryPolicy());
            }

            // 设置每个批次执行的间隔
            if (intervalDuration != null) {
                Thread.sleep(intervalDuration.toMinutes());
            }
            // 设置下一个批次的其实索引
            currentIndex += eachBatchNum;
        }

    }

    private static <T> void checkParams(Consumer<List<T>> consumer, List<T> data, BatchExePolicy batchExePolicy) {

        if (consumer == null || data == null) {
            throw new NullPointerException("消费方法或数据不能为空");
        }

        Integer eachBatchNum = batchExePolicy.getEachBatchNum();
        if (eachBatchNum <= 0) {
            throw new IllegalArgumentException("每个批次的数量不能为负数");
        }

        Duration intervalDuration = batchExePolicy.getIntervalDuration();
        if (intervalDuration != null && intervalDuration.isNegative()) {
            throw new IllegalArgumentException("延时不能为负数");
        }
    }
}
