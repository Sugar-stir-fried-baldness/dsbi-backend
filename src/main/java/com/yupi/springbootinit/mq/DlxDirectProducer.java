package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class DlxDirectProducer {

    private static final String DEAD_EXCHANGE_NAME = "dlx_direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //建立连接，连接本地的rabbitMq
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");


            //创建一个队列，分配队列名称
            String queueName = "laoban_dlx_queue";
            //创建交换机
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, DEAD_EXCHANGE_NAME, "laoban");

            String queueName2 = "waibao_dlx_queue";
            //创建交换机
            channel.queueDeclare(queueName2, true, false, false, null);
            channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "waibao");


            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String userInput = scanner.nextLine();
                String[] strings = userInput.split(" ");
                if (strings.length < 1) {
                    continue;
                }
                String message = strings[0];
                //指定一下，消息绑定了哪个路由键
                String routingKey =strings[1] ;

                //把消息发送给交换机
                //（ 交换机名称 ，交换机的路由规则（由于是给所有的路由发广播，不用写），消息的其他属性   ）
                channel.basicPublish(DEAD_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'with routing:'" + routingKey + "'");
            }

        }
    }
    //..
}