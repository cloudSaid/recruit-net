package com.imooc.api.mq;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-20 2:49
 */
@Configuration
public class RabbitMqConfig {

    public static final String SMS_EXCHANGE = "sms_exchange";

    public static final String SMS_QUEUE = "sms_queue";

    //创建交换机
    @Bean(SMS_EXCHANGE)
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE)
                .durable(true)
                .build();
    }

    //创建队列
    @Bean(SMS_QUEUE)
    public Queue queue(){
        Map<String, Object> args = new HashMap<>(2);
//       x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", RabbitMqDeadConfig.SMS_EXCHANGE_DEAD);
//       x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", RabbitMqDeadConfig.ROUTING_KEY);
        return QueueBuilder.durable(SMS_QUEUE).withArguments(args).build();
    }

    //创建绑定关系
    @Bean
    public Binding smsBinding(@Qualifier(SMS_EXCHANGE) Exchange exchange,
                              @Qualifier(SMS_QUEUE) Queue queue){

        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("mq.sms.#")
                .noargs();
    }


}
