package com.imooc.mq;

import com.google.gson.Gson;
import com.imooc.api.mq.RabbitMqConfig;
import com.imooc.pojo.bo.RegistLoginBO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Leo
 * @version 1.0
 * @description: 短信队列消费者
 * @date 2022-12-20 13:41
 */
@Component
@Slf4j
public class RabbitMQSMSConsumer {

    @RabbitListener(queues = {RabbitMqConfig.SMS_QUEUE})
    public void watchQueue(String payload, Message message, Channel channel) throws IOException {
        try {
            String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("路由为 : " + receivedRoutingKey);

            RegistLoginBO registLoginBO = new Gson().fromJson(payload, RegistLoginBO.class);
            log.info("手机号码为:" + registLoginBO.getMobile());
            log.info("验证码为:" + registLoginBO.getSmsCode());
                            //消息投递标签                                        批量确认所有消费者获得的消息
            channel.basicAck(message.getMessageProperties().getContentLength(),true);
        }catch (Exception e){
            /*
            reququ : 是否重回队列
             */
            channel.basicNack(message.getMessageProperties().getContentLength(),true,true);
        }
    }

}
