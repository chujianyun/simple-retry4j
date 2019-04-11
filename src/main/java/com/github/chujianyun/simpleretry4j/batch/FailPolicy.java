package com.github.chujianyun.simpleretry4j.batch;

/**
 * 出错策略
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-10 15:22
 */
enum FailPolicy {

    /**
     * 终止整个批量操作并抛异常
     */
    ABORT_AND_THROW(),

    /**
     * 忽略当前批次继续执行
     */
    IGNORE(),

    /**
     * 执行重试策略
     */
    RETRY()
}
