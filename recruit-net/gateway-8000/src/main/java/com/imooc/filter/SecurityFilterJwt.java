package com.imooc.filter;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.google.gson.Gson;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.properties.ReleasePathProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-08 18:01
 */
@Component
@Slf4j
public class SecurityFilterJwt implements GlobalFilter, Ordered {

    @Autowired
    private ReleasePathProperties releasePaths;


    @Autowired
    private AntPathMatcher antPathMatcher;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获得当前请求路径
        String path = exchange.getRequest().getURI().getPath();
        //获得放行路径
        List<String> releasePath = releasePaths.getPath();
        //函数式编程过滤路径
        long count = releasePath.stream()
                .filter(releasepath -> antPathMatcher.matchStart(releasepath,path)).count();

        if (count > 0){
            //直接放行
            log.info("放行该用户");
            return chain.filter(exchange);
        }

        log.info("已经拦截该用户");
        //检验JWT

        return packingExchange(exchange,ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }

    public Mono<Void> packingExchange(ServerWebExchange exchange,
                                      ResponseStatusEnum responseStatusEnum){
        //获得响应
        ServerHttpResponse response = exchange.getResponse();
        //构造返回的result对象
        GraceJSONResult exception = GraceJSONResult.exception(responseStatusEnum);
        //设置响应状态码
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        //判断响应中是否包含该类型 没有则设置
        if (!response.getHeaders().containsKey("Content-Type")){
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        //将json转化为字符串
        String resultJson = new Gson().toJson(exception);
        //包装响应体
        DataBuffer wrap = response.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));

        //进一步包装响应
        return response.writeWith(Mono.just(wrap));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
