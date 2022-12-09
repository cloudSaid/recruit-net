package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

/**
 * @author Leo
 * @version 1.0
 * @description: 跨域配置
 * @date 2022-12-09 18:34
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfig = new CorsConfiguration();
        //允许所有域名进行跨域调用
        corsConfig.addAllowedOriginPattern("*");
        //设置是否发送cookie信息
        corsConfig.setAllowCredentials(true);
        //允许所有的方法跨域访问
        corsConfig.addAllowedMethod("*");
        //设置允许的header
        corsConfig.addAllowedHeader("*");
        //为url添加映射的路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfig);

        return new CorsWebFilter(source);
    }

}
