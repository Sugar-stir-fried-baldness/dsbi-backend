package com.yupi.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @Author:tzy
 * @Description : 消费者
 * @Date:2024/2/816:08
 */
@Component
@Slf4j
public class MyMessageConsumer {



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
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message , Channel channel , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("receiveMessage message = {}",message);
        //手动ack ，一次只确认一条
        channel.basicAck(deliveryTag , false);
    }
}
