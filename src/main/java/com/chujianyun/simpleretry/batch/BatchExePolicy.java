package com.chujianyun.simpleretry.batch;

import com.chujianyun.simpleretry.RetryPolicy;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

/**
 * 批量执行策略
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-10 15:03
 */
@Data
@Builder
public class BatchExePolicy {

    /**
     * 每个批次执行的数量
     */
    private Integer eachBatchNum;

    /**
     * 间隔时间
     */
    private Duration intervalDuration;

    /**
     * 重试策略 仅 {@link FailPolicy#RETRY}时生效
     */
    private RetryPolicy retryPolicy;


    /**
     * 执行失败的策略
     */
    private FailPolicy failPolicy;

}
