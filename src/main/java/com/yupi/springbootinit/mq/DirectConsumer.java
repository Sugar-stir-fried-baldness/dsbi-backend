package com.yupi.springbootinit.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //创建一个队列，分配队列名称
        String queueName = "小鱼的工作队列";
        //创建交换机
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "小鱼");

        String queueName2 = "小皮的工作队列";
        //创建交换机
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueBind(queueName2, EXCHANGE_NAME, "小皮");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //员工小鱼从任务队列中取消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小鱼] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小皮 ] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };


        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {});
    }
}