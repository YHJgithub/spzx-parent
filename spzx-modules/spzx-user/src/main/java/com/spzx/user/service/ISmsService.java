package com.spzx.user.service;

import java.util.Map;

public interface ISmsService {
    void send(String phone, String templateCode, Map<String, Object> param);
}