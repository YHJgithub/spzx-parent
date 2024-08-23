package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品分类Service业务层处理
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

}