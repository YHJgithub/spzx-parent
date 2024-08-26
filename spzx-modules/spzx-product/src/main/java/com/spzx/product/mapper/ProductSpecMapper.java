package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.ProductSpec;

import java.util.List;

/**
 * 商品规格Mapper接口
 */
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {


    List<ProductSpec> list(ProductSpec productSpec);
}