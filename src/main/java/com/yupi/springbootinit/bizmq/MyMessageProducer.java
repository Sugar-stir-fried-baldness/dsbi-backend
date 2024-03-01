package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author:tzy
 * @Description :
 * @Date:2024/2/816:08
 */
@Component
public class MyMessageProducer {

    //在`springboot`中写入配置之后，项目会自动生成一个`rabbitTemplate`对象
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     *
     * @param exchange 发送到哪个交换机
     * @param routeKey 发送到哪个路由键
     * @param message 发送什么消息
     */
    public void sendMessage(String exchange , String routeKey , String message){
        //指定交换机，指定路由键，指定消息
        rabbitTemplate.convertAndSend(exchange , routeKey , message);
    }

}
