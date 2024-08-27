package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

/**
 * 分类品牌Mapper接口
 */
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {

    List<CategoryBrand> list(CategoryBrand categoryBrand);

    List<Brand> selectBrandListByCategoryId(Integer categoryId);
}

