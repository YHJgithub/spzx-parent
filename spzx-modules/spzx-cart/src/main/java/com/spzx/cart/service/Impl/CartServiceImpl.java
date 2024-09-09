package com.spzx.cart.service.Impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.SkuPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String hashKey = skuId.toString();
        Integer threshold = 99;
        if (hashOps.hasKey(hashKey)) {
            // 说明购该商品已在购物车中，进行累加，但不能超过上限99
            CartInfo cartInfo = hashOps.get(hashKey);
            int totalCount = cartInfo.getSkuNum() + skuNum;
            cartInfo.setSkuNum(totalCount > threshold ? threshold : totalCount);
            hashOps.put(hashKey, cartInfo);
        } else {
            // 判断购物车商品种类（不同SKU）总数大于50件
            Long count = hashOps.size();
            if (++count > 50) {
                throw new RuntimeException("商品种类数量超过上限");
            }
            // 说明购物车没有该商品，构建购物车对象，存入Redis
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum > threshold ? threshold : skuNum);
            cartInfo.setUpdateTime(new Date());
            cartInfo.setCreateTime(new Date());
            // 远程调用商品服务获取商品sku基本信息
            R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
            if (R.FAIL == productSkuResult.getCode()) {
                throw new ServiceException(productSkuResult.getMsg());
            }
            ProductSku productSku = productSkuResult.getData();
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setSkuId(productSku.getId());
            cartInfo.setThumbImg(productSku.getThumbImg());
            // 远程调用商品服务获取商品实时价格
            R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
            if (R.FAIL == skuPriceResult.getCode()) {
                throw new ServiceException(skuPriceResult.getMsg());
            }
            SkuPrice skuPrice = skuPriceResult.getData();
            cartInfo.setCartPrice(skuPrice.getSalePrice());
            cartInfo.setSkuPrice(skuPrice.getSalePrice());
            // 将购物车商品存入Redis
            hashOps.put(hashKey, cartInfo);
        }

    }

    @Override
    public List<CartInfo> getCartList() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取数据
        List<CartInfo> catInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(catInfoList)) {
            List<CartInfo> infoList = catInfoList.stream()
                    .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                    .toList();
            // 获取skuId列表
            List<Long> skuIdList = infoList.stream().map(CartInfo::getSkuId).toList();
            // 查询商品的实时价格
            R<List<SkuPrice>> skuPriceListResult = remoteProductService.getSkuPriceList(skuIdList, SecurityConstants.INNER);
            if (R.FAIL == skuPriceListResult.getCode()) {
                throw new ServiceException(skuPriceListResult.getMsg());
            }
            Map<Long, BigDecimal> skuIdToPriceMap =
                    skuPriceListResult.getData().stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
            infoList.forEach(item -> {
                item.setSkuPrice(skuIdToPriceMap.get(item.getSkuId()));
            });
            return infoList;
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteCart(Long skuId) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        hashOperations.delete(skuId.toString());
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        // 修改缓存
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        // 先获取用户选择的商品
        if (hashOperations.hasKey(skuId.toString())) {
            CartInfo cartInfoUpd = hashOperations.get(skuId.toString());
            // cartInfoUpd 写会缓存
            cartInfoUpd.setIsChecked(isChecked);
            // 更新缓存
            hashOperations.put(skuId.toString(), cartInfoUpd);
        }
    }

    @Override
    public void allCheckCart(Integer isChecked) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();
        cartInfoList.forEach(item -> {
            CartInfo cartInfoUpd = hashOperations.get(item.getSkuId().toString());
            cartInfoUpd.setIsChecked(isChecked);

            // 更新缓存
            hashOperations.put(item.getSkuId().toString(), cartInfoUpd);
        });
    }

    @Override
    public void clearCart() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        redisTemplate.delete(cartKey);
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        String cartKey = this.getCartKey(userId);
        List<CartInfo> cartCachInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartCachInfoList)) {
            for (CartInfo cartInfo : cartCachInfoList) {
                // 获取选中的商品！
                if (cartInfo.getIsChecked().intValue() == 1) {
                    cartInfoList.add(cartInfo);
                }
            }
        }
        return cartInfoList;
    }
}
