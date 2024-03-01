package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信交换机
 */
public class DlxDirectConsumer {

    //业务系统中的交换机，
    private static final String EXCHANGE_NAME = "direct2_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dlx_direct_exchange";


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //1
        //指定死信队列参数1
        Map<String, Object> args = new HashMap<String, Object>();
        //消息队列要绑定哪个交换机
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //指定死信要转发到哪个死信队列
        args.put("x-dead-letter-routing-key", "waibao");


        //指定死信队列参数2
        Map<String, Object> args2 = new HashMap<String, Object>();
        //消息队列要绑定哪个交换机
        args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //指定死信要转发到哪个死信队列
        args2.put("x-dead-letter-routing-key", "laoban");

        //1

        //创建一个队列，分配队列名称
        String queueName = "dog_queue";
        //创建交换机
        channel.queueDeclare(queueName, true, false, false, args);
        channel.queueBind(queueName, EXCHANGE_NAME, "dog");//给 direct2_exchange 中的 dog_queue 绑定 dog 路由键

        String queueName2 = "cat_queue";
        //创建交换机
        channel.queueDeclare(queueName2, true, false, false, args2);
        channel.queueBind(queueName2, EXCHANGE_NAME, "cat");//给 direct2_exchange 绑定 cat 路由键


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //员工小鱼从任务队列中取消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [老板] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [外包 ] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };


        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
        channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {});
    }
}