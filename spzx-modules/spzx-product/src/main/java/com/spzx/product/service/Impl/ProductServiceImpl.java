package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Product;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品Service业务层处理
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;


}