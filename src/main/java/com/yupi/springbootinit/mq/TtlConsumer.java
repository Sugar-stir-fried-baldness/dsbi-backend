package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TtlConsumer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        //创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        //创建频道
        Channel channel = connection.createChannel();

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-message-ttl", 5000);//5s
        //声明队列,args 指定参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, args);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        //定义了一个接口。
        // 定义了如何处理消息，从delivery 中得到一个对象，再从中得到消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        //消费消息，会持续阻塞，每当有消息来了，就会调用回调中的方法 deliverCallback
        //autoAck：消息取出来之后，自动就算他完成消费了，从消息队列中把它移除
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }
}