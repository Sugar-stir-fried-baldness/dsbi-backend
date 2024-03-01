package com.yupi.springbootinit.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author:tzy
 * @Description :
 * @Date:2024/1/2021:49
 */
class TaskStatusTest {

    @Test
    void values() {
    }

    @Test
    void valueOf() {
        System.out.println(TaskStatus.RUNNING.name());
    }
}