package com.imooc.api.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-20 2:49
 */
@Configuration
public class RabbitMqDeadConfig {

    public static final String SMS_EXCHANGE_DEAD = "sms_exchange_dead";

    public static final String SMS_QUEUE_DEAD = "sms_queue_dead";

    public static final String ROUTING_KEY = "mq.sms.dead";

    //创建交换机
    @Bean(SMS_EXCHANGE_DEAD)
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE_DEAD)
                .durable(true)
                .build();
    }

    //创建队列
    @Bean(SMS_QUEUE_DEAD)
    public Queue queue(){
        return QueueBuilder.durable(SMS_QUEUE_DEAD).build();
    }

    //创建绑定关系
    @Bean
    public Binding smsBinding(@Qualifier(SMS_EXCHANGE_DEAD) Exchange exchange,
                              @Qualifier(SMS_QUEUE_DEAD) Queue queue){

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY)
                .noargs();
    }


}
