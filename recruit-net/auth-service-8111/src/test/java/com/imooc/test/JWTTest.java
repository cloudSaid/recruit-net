package com.imooc.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTTest {




    // 定义秘钥，提供给jwt加密解密，一般都是由开发者或者公司定的规范，建议32位
  /*  public static final String USER_KEY = "imooc_123456789_123456789";

    @Test
    public void createJWT() {

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3. 通过jwt去生成一个token字符串
        Stu stu = new Stu(1001, "imooc 慕课网", 18);
        String stuJson = new Gson().toJson(stu);

        String myJWT = Jwts.builder()
                .setSubject(stuJson)         // 设置用户自定义数据
                .signWith(secretKey)    // 使用哪个秘钥对象进行jwt的生成
                .compact();             // 压缩并且生成jwt

        System.out.println(myJWT);
    }

    @Test
    public void checkJWT() {

        // 模拟假设前端传过来的jwt
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjEwMDEsXCJuYW1lXCI6XCJpbW9vYyDmhZXor77nvZFcIixcImFnZVwiOjE4fSJ9.THFIuA6VxihfflzDFE0u3_E2gFeeWrH-qQjFnpCgof4";

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3. 校验jwt
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();       // 构造解析器
        // 解析成功，可以获得Claims，从而去get相关的数据，如果此处抛出异常，则说明解析不通过，也就是token失效或者被篡改
        Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);      // 解析jwt

        String stuJson = jws.getBody().getSubject();
        Stu stu = new Gson().fromJson(stuJson, Stu.class);

        System.out.println(stu);
    }*/

}
