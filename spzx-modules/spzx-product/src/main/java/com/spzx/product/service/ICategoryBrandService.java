package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

/**
 * 分类品牌Service接口
 */
public interface ICategoryBrandService extends IService<CategoryBrand> {

    List<CategoryBrand> list(CategoryBrand categoryBrand);

    CategoryBrand getInfo(Integer id);

    int add(CategoryBrand categoryBrand);

    int updateCategoryBrand(CategoryBrand categoryBrand);

    List<Brand> selectBrandListByCategoryId(Integer categoryId);
}