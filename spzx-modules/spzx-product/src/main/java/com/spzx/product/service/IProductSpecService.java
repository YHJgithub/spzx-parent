package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductSpec;

import java.util.List;

/**
 * 商品规格Service接口
 */
public interface IProductSpecService extends IService<ProductSpec> {


    List<ProductSpec> list(ProductSpec productSpec);

    ProductSpec getInfo(Integer id);

    List<ProductSpec> selectProductSpecListByCategoryId(Integer categoryId);
}