package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        //2个
        Channel channel1 = connection.createChannel();
        Channel channel2  = connection.createChannel();
        //声明交换机 ，如果有 名称和原来一样
        channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //创建队列，这里用获取当前队列，也就是 当你不指定队列名称的话，随机分配一个队列名称
        String queueName = "小王的工作队列";
        //创建交换机
        channel1.queueDeclare(queueName, true, false, false, null);
        channel1.queueBind(queueName, EXCHANGE_NAME, "");

        String queueName2 = "小李的工作队列";
        channel2.queueDeclare(queueName2, true, false, false, null);
        channel2.queueBind(queueName2, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //两个消费队列
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小王] Received '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小李] Received '" + message + "'");
        };

        //监听小王和小李 ，这里用 autoAck自动应答比较省事
        channel1.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {});
    }
}