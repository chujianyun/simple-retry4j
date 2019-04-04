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
     * @throws Exception
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
     * @throws Exception
     */
    public static <T> T executeWithRetry(Operation<T> operation, int maxAttemptTimes, int timeDelay, TimeUnit timeUnit) throws Exception {

        if (maxAttemptTimes < 1) {
            throw new IllegalArgumentException("max attempt times must not less than one");
        }
        int count = 1;

        while (true) {
            try {
                return operation.execute();
            } catch (BusinessException businessException) {
                log.debug("OperationHelper#businessException", businessException);
                throw new BusinessException();
            } catch (Exception e) {
                log.debug("OperationHelper#executeWithRetry", e);
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
