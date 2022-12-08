package com.imooc.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-08 16:23
 */

@Component
@Data
@PropertySource("classpath:Jwt.properties")
@ConfigurationProperties(prefix = "auth")
public class JWTProperties {

    private String jwt;

}
