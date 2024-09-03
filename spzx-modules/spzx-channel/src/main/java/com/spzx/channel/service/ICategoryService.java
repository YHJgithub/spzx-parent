package com.spzx.channel.service;

import com.spzx.product.api.domain.vo.CategoryVo;

import java.util.List;

/**
 * 商品分类Service接口
 */
public interface ICategoryService
{
    public List<CategoryVo> tree();
}