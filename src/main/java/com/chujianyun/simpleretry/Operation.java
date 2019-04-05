package com.chujianyun.simpleretry;

/**
 * 操作接口方法
 *
 * @author: 明明如月 liuwangyanghudu@163.com
 * @date: 2019-04-05 02:09
 */
public interface Operation<T> {
    T execute() throws Exception;
}
