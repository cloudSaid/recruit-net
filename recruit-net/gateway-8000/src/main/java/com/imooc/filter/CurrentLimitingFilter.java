package com.imooc.filter;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.properties.ReleasePathProperties;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.imooc.utils.IPUtil.getRequestIp;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-08 18:01
 */
@Component
@Slf4j
public class CurrentLimitingFilter extends BaseInfoProperties implements GlobalFilter, Ordered {


    @Autowired
    private ReleasePathProperties releasePaths;


    @Autowired
    private AntPathMatcher antPathMatcher;

    @Value("${blackIP.continueCounts}")
    private long continueCounts;
    @Value("${blackIP.timeInterval}")
    private long timeInterval;
    @Value("${blackIP.limitTimes}")
    private long limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获得当前请求路径
        String path = exchange.getRequest().getURI().getPath();
        //获得放行路径
        List<String> releasePath = releasePaths.getIpLimitUrls();

        long count = releasePath.stream()
                .filter(releasepath -> antPathMatcher.matchStart(releasepath,path)).count();

        if (count == 0){
            //直接放行
            log.info("放行该用户");
            return chain.filter(exchange);
        }

        return doLimit(exchange, chain);

    }

    public Mono<Void> doLimit(ServerWebExchange exchange,
                              GatewayFilterChain chain) {

        // 根据request获得请求ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        /**
         * 需求：
         * 判断ip在20秒内请求的次数是否超过3次
         * 如果超过，则限制访问30秒
         * 等待30秒静默以后，才能够回复访问
         */
        // 正常的ip
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单，如果存在，则表示目前被关小黑屋
        final String ipRedisLimitedKey = "gateway-ip:limit:" + ip;

        // 获得当前ip，查询还剩下多少的小黑屋时间
        long limitLeftTimes = redis.ttl(ipRedisLimitedKey);
        if (limitLeftTimes > 0) {
            // 终止请求，返回错误
            return packingExchange(exchange,
                    ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 在redis中获得ip的累加次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        // 判断如果是第一次进来，也就是0开始的，则初期访问就是1，需要设置间隔的时间，也就是连续请求的间隔时间
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }

        // 如果还能取得请求的次数，说明用户的连续请求落在限定的[timeInterval秒]之内
        // 一旦请求次数超过限定的连续访问[continueCounts]次数，则需要限制当前ip
        if (requestCounts > continueCounts) {
            // 限制ip访问的时间[limitTimes]
            redis.set(ipRedisLimitedKey, ipRedisLimitedKey, limitTimes);
            // 终止请求，返回错误
            return packingExchange(exchange,
                    ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
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
