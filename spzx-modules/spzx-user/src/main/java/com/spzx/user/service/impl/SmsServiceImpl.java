package com.spzx.user.service.impl;

import com.spzx.common.core.utils.HttpUtils;
import com.spzx.user.service.ISmsService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements ISmsService {
    @Override
    public void send(String phone, String templateCode, Map<String, Object> param) {
        String host = "https://zwp.market.alicloudapi.com";
        String path = "/sms/sendv2";
        String method = "GET";
        String appcode = "76e4e538bc23459ca8d0b8afbe385073";
        Map<String, String> headers = new HashMap<String, String>();
        // 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("content", "【智能云】您的验证码是" + param.get("code").toString() + "。如非本人操作，请忽略本短信");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            // 获取response的body
            // System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
