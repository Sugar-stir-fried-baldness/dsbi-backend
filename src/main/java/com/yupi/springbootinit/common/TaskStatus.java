package com.yupi.springbootinit.common;

/**
 * @Author:tzy
 * @Description : 线程的任务状态枚举
 * @Date:2024/1/2021:37
 */
public enum TaskStatus {
    /**
     * 等待
     */
    WAIT,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 成功
     */
    SUCCEED,

    /**
     * 失败
     */
    FAILED;
}
