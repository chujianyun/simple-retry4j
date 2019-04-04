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

    public static <T> T executeWithRetry(Operation<T> operation, int maxAttempts) throws Exception {
        return executeWithRetry(operation, maxAttempts, 0, null);
    }

    public static <T> T executeWithRetry(Operation<T> operation, int maxAttempts, int time, TimeUnit timeUnit) throws Exception {

        if (maxAttempts < 1) {
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
                if (count > maxAttempts) {
                    throw e;
                }

                // 延时
                if (time >= 0 && timeUnit != null) {
                    try {
                        log.debug("延时{}毫秒", timeUnit.toMillis(time));
                        timeUnit.sleep(time);
                    } catch (InterruptedException ex) {
                        //ignore
                    }
                }

                log.debug("第{}次重试", count - 1);
            }
        }
    }


}
