package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Product;
import com.spzx.product.api.domain.ProductSku;

import java.util.List;

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
}