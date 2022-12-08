package com.mashibing.serviceverificationcode;

import com.google.gson.Gson;
import com.imooc.pojo.test.Stu;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
class ServiceVerificationcodeApplicationTests {


    private final static String SECRET_KEY = "adbadbadbadbadbadbadb-asdbabsdbadn";



    public static void main(String[] args) {
        String jwt = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJuYW1lXCI6XCLlsI_njotcIixcImFnZVwiOjE4fSJ9.hwZYMASW6oc-mZ2iG4k0VcVp_wZHMpguJS6k0WWGD0DaFeaTVMNg0xcWfJ5gQhH2";
        //对密钥进行base64编码
        String base64Key = Base64.toBase64String(SECRET_KEY.getBytes());
        // 生成密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64Key.getBytes());


        //解析jwt
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();//构造解析器对象
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
        String subject = claimsJws.getBody().getSubject();
        Stu stu = new Gson().fromJson(subject, Stu.class);

        System.out.println(stu);


       /* Stu stu = new Stu(1,"小王",18);
        String stuJson = new Gson().toJson(stu);

        String compact = Jwts.builder()
                .setSubject(stuJson)
                .signWith(secretKey)
                .compact();

        System.out.println(compact);*/

    }




}
