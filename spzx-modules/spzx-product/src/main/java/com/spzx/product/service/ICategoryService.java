package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Category;

import java.util.List;

/**
 * 商品分类Service接口
 */
public interface ICategoryService extends IService<Category> {

    List<Category> treeSelect(Long id);

    List<Long> getAllCategoryIdList(Long categoryId);
}