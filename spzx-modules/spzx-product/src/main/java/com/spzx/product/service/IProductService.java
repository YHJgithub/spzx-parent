package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.vo.SkuPrice;
import com.spzx.product.api.domain.vo.SkuQuery;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.ProductDetails;

import java.util.List;
import java.util.Map;

/**
 * 商品Service接口
 */
public interface IProductService extends IService<Product> {

    List<Product> selectProductList(Product product);

    int insertProduct(Product product);

    Product selectProductById(Integer id);

    int updateProduct(Product product);

    int deleteById(Integer[] ids);

    void updateAuditStatus(Long id, Integer auditStatus);

    void updateStatus(Long id, Integer status);

    List<ProductSku> getTopSale();

    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);

    ProductSku getProductSku(Long skuId);

    Product getProduct(Long id);

    SkuPrice getSkuPrice(Long skuId);

    ProductDetails getProductDetails(Long id);

    Map<String, Long> getSkuSpecValue(Long id);

    SkuStockVo getSkuStock(Long skuId);

    List<SkuPrice> getSkuPriceList(List<Long> skuIdList);
}