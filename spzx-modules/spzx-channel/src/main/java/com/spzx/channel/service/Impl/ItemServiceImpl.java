package com.spzx.channel.service.Impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.SkuPrice;
import com.spzx.product.api.domain.vo.SkuStockVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class ItemServiceImpl implements IItemService {

    @Autowired
    private RemoteProductService remoteProductService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public ItemVo item(Long skuId) {

        // 远程调用商品微服务接口之前 提前知道用户访问商品SKUID是否存在与布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
        if (!bloomFilter.contains(skuId)) {
            log.error("用户查询商品sku不存在：{}", skuId);
            // 查询数据不存在直接返回空对象
            throw new ServiceException("用户查询商品sku不存在");
        }

        ItemVo itemVo = new ItemVo();
        // 1.获取Sku
        CompletableFuture<ProductSku> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
            if (productSkuResult.getCode() == R.FAIL) {
                throw new ServiceException(productSkuResult.getMsg());
            }
            ProductSku productSku = productSkuResult.getData();
            itemVo.setProductSku(productSku);
            return productSku;
        }, threadPoolExecutor);


        // 2.获取product
        CompletableFuture<Void> productCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<Product> productResult = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
            if (productResult.getCode() == R.FAIL) {
                throw new ServiceException(productResult.getMsg());
            }
            Product product = productResult.getData();
            itemVo.setProduct(product);
            itemVo.setSliderUrlList(Arrays.asList(product.getSliderUrls().split(",")));
            itemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));
        }, threadPoolExecutor);

        // 3.获取价格
        CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
            R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
            if (skuPriceResult.getCode() == R.FAIL) {
                throw new ServiceException(skuPriceResult.getMsg());
            }
            SkuPrice skuPrice = skuPriceResult.getData();
            itemVo.setSkuPrice(skuPrice);
        }, threadPoolExecutor);

        // 4.获取商品详情  
        CompletableFuture<Void> productDetailsCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<ProductDetails> productDetailsResult = remoteProductService.getProductDetails(productSku.getProductId(), SecurityConstants.INNER);
            if (productDetailsResult.getCode() == R.FAIL) {
                throw new ServiceException(productDetailsResult.getMsg());
            }
            ProductDetails productDetails = productDetailsResult.getData();
            itemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
        }, threadPoolExecutor);


        // 5.获取商品规格对应商品skuId信息
        CompletableFuture<Void> skuSpecValueCompletableFuture = skuCompletableFuture.thenAcceptAsync(productSku -> {
            R<Map<String, Long>> skuSpecValueResult = remoteProductService.getSkuSpecValue(productSku.getProductId(), SecurityConstants.INNER);
            if (skuSpecValueResult.getCode() == R.FAIL) {
                throw new ServiceException(skuSpecValueResult.getMsg());
            }
            itemVo.setSkuSpecValueMap(skuSpecValueResult.getData());
        }, threadPoolExecutor);

        // 6.获取库存
        CompletableFuture<Void> skuStockVoCompletableFuture = CompletableFuture.runAsync(() -> {
            R<SkuStockVo> skuStock = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
            if (skuStock.getCode() == R.FAIL) {
                throw new ServiceException(skuStock.getMsg());
            }
            itemVo.setSkuStockVo(skuStock.getData());
        }, threadPoolExecutor);

        CompletableFuture.allOf(
                skuCompletableFuture,
                productCompletableFuture,
                skuPriceCompletableFuture,
                productDetailsCompletableFuture,
                skuSpecValueCompletableFuture,
                skuStockVoCompletableFuture
        ).join();

        return itemVo;
    }
}
