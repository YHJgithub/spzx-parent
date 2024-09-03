package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.Product;

import java.util.List;

/**
 * 商品Mapper接口
 */
public interface ProductMapper extends BaseMapper<Product> {


    List<Product> selectProductList(Product product);
}