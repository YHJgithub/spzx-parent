package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.ICategoryBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类品牌Service业务层处理
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService {

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

}