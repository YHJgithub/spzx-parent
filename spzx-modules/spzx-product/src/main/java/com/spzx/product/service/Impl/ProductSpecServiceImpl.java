package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.IProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品规格Service业务层处理
 */
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {

    @Autowired
    private ProductSpecMapper productSpecMapper;

}