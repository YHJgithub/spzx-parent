package com.spzx.cart.service.Impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.product.api.RemoteProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;

    private String getCartKey(Long userId) {
        // 定义key user:cart:userId
        return "user:cart:" + userId;
    }

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();

        // 构建“用户”购物车hash结构key  user:cart:用户ID
        String cartKey = getCartKey(userId);

        // 创建Hash结构绑定操作对象（方便对hash进行操作）
        BoundHashOperations<String, String, CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);

        // 判断用户购物车中是否包含该商品 如果包含：数量进行累加(某件商品数量上限99) 不包含：新增购物车商品


    }

}
