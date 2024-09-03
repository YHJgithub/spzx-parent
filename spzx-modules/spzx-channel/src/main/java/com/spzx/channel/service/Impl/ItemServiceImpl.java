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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
@Slf4j
public class ItemServiceImpl implements IItemService {

    @Autowired
    private RemoteProductService remoteProductService;

    @Override
    public ItemVo item(Long skuId) {
        ItemVo itemVo = new ItemVo();
        // 1.获取Sku
        R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
        if (productSkuResult.getCode() == R.FAIL) {
            throw new ServiceException(productSkuResult.getMsg());
        }
        ProductSku productSku = productSkuResult.getData();

        itemVo.setProductSku(productSku);

        // 2.获取product
        R<com.spzx.product.api.domain.Product> productResult = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
        if (productResult.getCode() == R.FAIL) {
            throw new ServiceException(productResult.getMsg());
        }
        Product product = productResult.getData();
        itemVo.setProduct(product);
        itemVo.setSliderUrlList(Arrays.asList(product.getSliderUrls().split(",")));
        itemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));

        // 3.获取价格
        R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
        if (skuPriceResult.getCode() == R.FAIL) {
            throw new ServiceException(skuPriceResult.getMsg());
        }
        SkuPrice skuPrice = skuPriceResult.getData();
        itemVo.setSkuPrice(skuPrice);

        // 4.获取商品详情  
        R<ProductDetails> productDetailsResult = remoteProductService.getProductDetails(productSku.getProductId(), SecurityConstants.INNER);
        if (productDetailsResult.getCode() == R.FAIL) {
            throw new ServiceException(productDetailsResult.getMsg());
        }
        ProductDetails productDetails = productDetailsResult.getData();
        itemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));

        // 5.获取商品规格对应商品skuId信息
        R<Map<String, Long>> skuSpecValueResult = remoteProductService.getSkuSpecValue(productSku.getProductId(), SecurityConstants.INNER);
        if (skuSpecValueResult.getCode() == R.FAIL) {
            throw new ServiceException(skuSpecValueResult.getMsg());
        }
        itemVo.setSkuSpecValueMap(skuSpecValueResult.getData());

        // 6.获取库存
        R<SkuStockVo> skuStock = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
        if (skuStock.getCode() == R.FAIL) {
            throw new ServiceException(skuStock.getMsg());
        }
        itemVo.setSkuStockVo(skuStock.getData());

        return itemVo;
    }
}
