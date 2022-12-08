package com.imooc.api.Intercept.InterceptorConfigs;

import com.imooc.api.Intercept.CurrentUserInterceptor;
import com.imooc.api.Intercept.JurisdictionInterceptor;
import com.imooc.api.Intercept.SMSInterceptor;
import org.springframework.cloud.util.ConditionalOnBootstrapEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-06 20:19
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 限制短信验证码拦截器
     * @return
     */
    @Bean
    public SMSInterceptor registerSMSInterceptor(){
        return new SMSInterceptor();
    }

    @Bean
    public CurrentUserInterceptor currentUserInterceptor(){
        return new CurrentUserInterceptor();
    }


    /*
    * /passport/getSMSCode
releases.path[1]=/passport/login
releases.path[2]=/passport/logout
    * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(registerSMSInterceptor())
                .addPathPatterns("/passport/getSMSCode");
        registry.addInterceptor(currentUserInterceptor())
                .addPathPatterns("/c/hello");
    }
}
