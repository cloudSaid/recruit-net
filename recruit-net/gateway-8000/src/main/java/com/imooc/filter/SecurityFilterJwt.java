package com.imooc.filter;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.properties.ReleasePathProperties;
import com.imooc.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
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
public class SecurityFilterJwt extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ReleasePathProperties releasePaths;

    private static final String HEADER_USER_TOKEN = "headerUserToken";

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AntPathMatcher antPathMatcher;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获得当前请求路径
        String path = exchange.getRequest().getURI().getPath();
        //获得放行路径
        List<String> releasePath = releasePaths.getUrls();
        //函数式编程过滤路径
        long count = releasePath.stream()
                .filter(releasepath -> antPathMatcher.matchStart(releasepath,path)).count();

        if (count > 0){
            //直接放行
            log.info("放行该用户");
            return chain.filter(exchange);
        }

        log.info("已经拦截该用户");
        // 判断header中是否有token，对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);

        // 判空header中的令牌
        if (StringUtils.isNotBlank(userToken)) {
            String[] tokenArr = userToken.split(JWTUtils.at);
            if (tokenArr.length < 2) {
                return packingExchange(exchange, ResponseStatusEnum.UN_LOGIN);
            }

            // 获得jwt的令牌与前缀
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            // 判断并且处理用户信息
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(jwt, exchange, chain, APP_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(jwt, exchange, chain, SAAS_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)) {
                return dealJWT(jwt, exchange, chain, ADMIN_USER_JSON);
            }

//            return dealJWT(jwt, exchange, chain, APP_USER_JSON);
        }

        // 不放行，token校验在jwt校验的自身代码逻辑中，到达此处表示都是漏掉的可能没有配置在excludeList
//        GraceException.display(ResponseStatusEnum.UN_LOGIN);
//        return chain.filter(exchange);
        return packingExchange(exchange, ResponseStatusEnum.UN_LOGIN);
    }


    public Mono<Void> dealJWT(String jwt, ServerWebExchange exchange, GatewayFilterChain chain, String key) {
        try {
            String userJson = jwtUtils.checkJWT(jwt);
            ServerWebExchange serverWebExchange = setNewHeader(exchange, key, userJson);
            return chain.filter(serverWebExchange);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return packingExchange(exchange, ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return packingExchange(exchange, ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }
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

    public ServerWebExchange setNewHeader(ServerWebExchange exchange,
                                          String headerKey,
                                          String headerValue){

        // 重新构建新的request
        ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .header(headerKey, headerValue)
                .build();
        // 替换原来的request
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return newExchange;
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
