package com.imooc.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSUtils {
    @Autowired
    private TencentCloudProperties tencentCloudProperties;

    public void sendSMS(String phone, String code) throws Exception {

    }

//    public static void main(String[] args) {
//        try {
//            new SMSUtils().sendSMS("18812345612", "7896");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}


