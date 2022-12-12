package com.imooc;

import com.imooc.api.SMSUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient  // 开启服务的注册与发现功能
@MapperScan(basePackages = "com.imooc.mapper")
@EnableAsync
@EnableRetry
public class AuthApplication {


    public static void main(String[] args) {

        SpringApplication.run(AuthApplication.class, args);


    }

    @Bean
    public SMSUtils smsUtils(){
        return new SMSUtils();
    }

}
