package com.yupi.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.TaskStatus;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author:tzy
 * @Description : 消费者
 * @Date:2024/2/816:08
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;
    //写一个方法来接受咱们要处理的消息
    //channel 是干嘛的：负责和rabbitMq通信的，
    // 为什么要引入channel呢，因为咱们要使用channel的ack和nack方法来手动确认和拒绝这个消息
    //接受或拒绝消息的时候，必须指定接受或拒绝 哪个消息 ，需要指定...
//    只要写了RabbitListener注解，他就会给你的方法填充


    /**
     *
     * @param message 需要处理的消息
     * @param channel 负责和rabbitMq通信的，
     *                为什么要引入channel呢，因为咱们要使用channel的ack和nack方法来手动确认和拒绝这个消息
     *
     * @param deliveryTag  接受或拒绝消息的时候，必须指定接受或拒绝 哪个消息 ，需要指定...
     */
    // 使用@SneakyThrows注解简化异常处理
    @SneakyThrows
    //RabbitListener注解，他就会给你的方法填充。queues 表示你要监听的消息队列
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message , Channel channel , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("receiveMessage message={}",message);
        if( StringUtils.isNotBlank(message) ){
            //消息为空，拒绝掉    ，只拒绝当前消息，是否要重新放到队列里
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if(chart == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");

        }

        //先修改图表任务状态为“执行中”，等执行成功后，修改为“已完成”，保存图表结果，执行失败后，修改为失败，记录任务失败信息
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(TaskStatus.RUNNING.name());
        boolean b = chartService.updateById(updateChart);
        //更新失败
        if(!b){
            channel.basicNack(deliveryTag,false,false);

            //todo将数据库中数据标注为失败
            handleChartUpdateError(chart.getId(), "图表运行状态 更新失败");
            return;
        }
        //获取 从ai那边传过来的信息
        String res = aiManager.doChat( CommonConstant.BI_MODEL_ID, buildUserInput(chart));
        String[] split = res.split("【【【【【");
        if (split.length < 3) {
            channel.basicNack(deliveryTag,false,false);

            handleChartUpdateError(chart.getId(),"ai生成错误");
            return;
        }
        // ai生成的代码
        String code = split[1].trim();
        String genResult = split[2].trim();
        //AI生成后，修改数据库状态
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(code);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(TaskStatus.SUCCEED.name());
        boolean updateResult = chartService.updateById(updateChartResult);
        if(!updateResult){//更新失败
            channel.basicNack(deliveryTag,false,false);

            handleChartUpdateError(chart.getId(),"图表成功状态 更新失败");
//                return;
        }

        log.info("receiveMessage message = {}",message);
        //手动ack ，一次只确认一条 。 任务成功，消息确认
        channel.basicAck(deliveryTag , false);
    }

    //处理死信队列
    private void handleDlExchange(){

    }



    /**
     * 构造用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart){
        StringBuilder userInput = new StringBuilder();
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        if (chartType != null) {
            userGoal += ",请使用:" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        //从excel上读取数据
        userInput.append(csvData).append("\n");

        return userInput.toString();
    }

    /**
     * 处理图表更新状态失败
     * @param chartId
     * @param execMessage
     */
    private void handleChartUpdateError(long chartId , String execMessage){
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus(TaskStatus.FAILED.name());
        chart.setExecMessage(execMessage);
        boolean b = chartService.updateById(chart);
        if(!b){
            log.error("更新图表状态失败"+ chartId +","+execMessage);
        }
    }

}
