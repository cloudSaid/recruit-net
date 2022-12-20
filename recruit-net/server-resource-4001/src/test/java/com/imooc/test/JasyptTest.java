package com.imooc.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    @Test
    public void testPwdEncrypt() {

        // 实例化加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        // 配置加密算法和秘钥
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword("fengjianyingyue");      // 用于加密的秘钥(盐)，可以是随机字符串，或者固定，一定要存储好，不要被其他人知道
        config.setAlgorithm("PBEWithMD5AndDES");    // 设置加密算法，默认
        encryptor.setConfig(config);

        // 对密码进行加密
        String myPwd = "imooc";
        String encryptedPwd = encryptor.encrypt(myPwd);
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println("+ 原密码为：" + myPwd);
        System.out.println("+ 加密后的密码为：" + encryptedPwd);
        System.out.println("++++++++++++++++++++++++++++++");
    }

//    UnoiHtPp84zMjjwLoJe/Kg==
//    i5d1R61fSBln/Z0gwGnzyg==

    @Test
    public void testPwdDecrypt() {

        // 实例化加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        // 配置加密算法和秘钥
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword("fengjianyingyue");      // 用于加密的秘钥(盐)，可以是随机字符串，或者固定，一定要存储好，不要被其他人知道
        config.setAlgorithm("PBEWithMD5AndDES");    // 设置加密算法，默认
        encryptor.setConfig(config);

        // 对密码进行解密
        String pendingPwd = "i5d1R61fSBln/Z0gwGnzyg==";
        String myPwd = encryptor.decrypt(pendingPwd);
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println("+ 解密后的密码为：" + myPwd);
        System.out.println("++++++++++++++++++++++++++++++");
    }

}
