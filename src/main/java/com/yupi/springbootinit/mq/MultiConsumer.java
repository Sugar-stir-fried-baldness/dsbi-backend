package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        //建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        for (int i = 0; i < 2; i++) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 设置预取计数为 1，这样RabbitMQ就会在给消费者新消息之前等待先前的消息被确认
            channel.basicQos(1);

            //定义了如何处理消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                try {
                    // 处理工作
                    System.out.println(" [x] Received '" +"编号："+finalI +":"+ message + "'");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false );
                    //停20 秒，模拟机器处理能力有限
                    Thread.sleep(20000);
                    //告诉服务器哪条消息被确认了 (快递取信封取快递标签),
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //消费失败
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            //开启消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}