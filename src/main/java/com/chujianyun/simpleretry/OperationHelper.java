package com.chujianyun.simpleretry;

import com.chujianyun.simpleretry.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 操作封装
 *
 * @author: 明明如月 liuwangyanghudu@163.com
 * @date: 2019-04-05 02:09
 */
@Slf4j
public class OperationHelper {

    /**
     * 带重试的操作执行
     *
     * @param operation       执行的操作
     * @param maxAttemptTimes 最大重试次数
     * @param <T>             返回值类型
     * @return 返回值
     * @throws Exception 业务异常或者超过最大重试次数后的最后一次尝试抛出的异常
     */
    public static <T> T executeWithRetry(Operation<T> operation, int maxAttemptTimes) throws Exception {
        return executeWithRetry(operation, maxAttemptTimes, 0, null);
    }

    /**
     * 带重试和延时的操作执行
     *
     * @param operation       执行的操作
     * @param maxAttemptTimes 最大重试次数
     * @param timeDelay       延时
     * @param timeUnit        时间单位
     * @param <T>             返回值类型
     * @return 返回值
     * @throws Exception 业务异常或者超过最大重试次数后的最后一次尝试抛出的异常
     */
    public static <T> T executeWithRetry(Operation<T> operation, int maxAttemptTimes, int timeDelay, TimeUnit timeUnit) throws Exception {

        if (maxAttemptTimes < 0) {
            throw new IllegalArgumentException("max attempt times must not be negative");
        }
        int count = 1;

        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                /* ---------------- 不需要重试的异常 -------------- */
                //业务异常不需要重试
                if (e instanceof BusinessException) {
                    throw e;
                }
                log.debug("OperationHelper#executeWithRetry", e);

                /* ---------------- 重试 -------------- */
                //累计
                count++;
                if (count > maxAttemptTimes) {
                    throw e;
                }
                // 延时
                if (timeDelay >= 0 && timeUnit != null) {
                    try {
                        log.debug("延时{}毫秒", timeUnit.toMillis(timeDelay));
                        timeUnit.sleep(timeDelay);
                    } catch (InterruptedException ex) {
                        //ignore
                    }
                }

                log.debug("第{}次重试", count - 1);
            }
        }
    }


}
