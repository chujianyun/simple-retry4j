package com.chujianyun.simpleretry;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 重试策略
 *
 * @author: 明明如月 liuwangyanghdu@163.com
 * @date: 2019-04-05 10:06
 */
@Data
public class RetryPolicy {

    /**
     * 最大重试次数（如果不设置则默认不满足重试的异常或策略则无限重试）
     */
    private Integer maxRetries;

    /**
     * 延时时间
     */
    private Duration delayDuration;

    /**
     * 不需要重试的异常列表
     */
    private List<Class<? extends Exception>> abortExceptions;

    /**
     * 不需要重试的条件列表(满足其中一个则不重试,如果要传入泛型条件是返回值或者其父类类型)
     */
    private List<Predicate> abortConditions;


    public RetryPolicy(Builder builder) {

        this.maxRetries = builder.maxRetries;
        this.delayDuration = builder.delayDuration;

        List<Class<? extends Exception>> abortExceptions = builder.abortExceptions;
        if (CollectionUtils.isEmpty(abortExceptions)) {
            this.abortExceptions = new ArrayList<>();
        } else {
            this.abortExceptions = abortExceptions;
        }

        List<Predicate> abortConditions = builder.abortConditions;
        if (CollectionUtils.isEmpty(abortConditions)) {
            this.abortConditions = new ArrayList<>();
        } else {
            this.abortConditions = abortConditions;
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer maxRetries;

        private Duration delayDuration;

        private List<Class<? extends Exception>> abortExceptions = new ArrayList<>();

        private List<Predicate> abortConditions = new ArrayList<>();


        /**
         * 设置最大重试次数（如果不设置则默认不满足重试的异常或策略则无限重试）
         */
        public Builder maxRetries(Integer maxRetries) {
            if (maxRetries == null || maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries must not be null or negative");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * 重试的时间间隔
         */
        public Builder delayDuration(Duration delayDuration) {
            if (delayDuration == null || delayDuration.isNegative()) {
                throw new IllegalArgumentException("delayDuration must not be null or negative");
            }

            this.delayDuration = delayDuration;
            return this;
        }

        /**
         * 设置不重试的策略列表
         */
        public Builder abortConditions(List<Predicate> predicates) {
            if (CollectionUtils.isNotEmpty(predicates)) {
                predicates.forEach(this::abortCondition);
            }
            return this;
        }

        /**
         * 新增不重试的策略
         */
        public Builder abortCondition(Predicate predicate) {
            if (predicate != null) {
                this.abortConditions.add(predicate);
            }
            return this;
        }

        /**
         * 设置不重试的异常列表
         */
        public Builder abortExceptions(List<Class<? extends Exception>> abortExceptions) {
            if (CollectionUtils.isNotEmpty(abortExceptions)) {
                abortExceptions.forEach(this::abortException);
            }
            return this;
        }

        /**
         * 新增不重试的异常
         */
        public Builder abortException(Class<? extends Exception> exception) {
            if (exception != null) {
                this.abortExceptions.add(exception);
            }
            return this;
        }

        public RetryPolicy build() {
            return new RetryPolicy(this);
        }

    }

}
