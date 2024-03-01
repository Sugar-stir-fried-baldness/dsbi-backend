package com.yupi.springbootinit.mapper;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author:tzy
 * @Description :
 * @Date:2024/1/1414:14
 */
class ChartMapperTest {
    @Resource
    private ChartMapper chartMapper;
    @Test
    void queryChartData() {
        String chartId = "1659210482555121666";
        String querySql = String.format("select * from chart_%s", chartId);
        List<Map<String, Object>> queryChartData = chartMapper.queryChartData(querySql);
        System.out.println(queryChartData);
    }
}