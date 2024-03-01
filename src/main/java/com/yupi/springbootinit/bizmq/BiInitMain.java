package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author:tzy
 * @Description : 用于创建测试程序用到的交换机和队列（只用程序启动前执行一次）
 * @Date:2024/2/1215:16
 */
public class BiInitMain {
    public static void main(String[] args) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            //创建一个队列，分配队列名称
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            //创建交换机
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);

        } catch (Exception e) {

        }

    }
}
