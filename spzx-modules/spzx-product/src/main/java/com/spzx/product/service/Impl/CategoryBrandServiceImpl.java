package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.ICategoryBrandService;
import com.spzx.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类品牌Service业务层处理
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService {

    @Autowired
    CategoryBrandMapper categoryBrandMapper;

    @Autowired
    ICategoryService categoryService;

    @Override
    public List<CategoryBrand> list(CategoryBrand categoryBrand) {
        return categoryBrandMapper.list(categoryBrand);
    }

    @Override
    public CategoryBrand getInfo(Integer id) {
        CategoryBrand categoryBrand = categoryBrandMapper.selectById(id);
        // 获取 一级、二级、三级，全部id
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(categoryBrand.getCategoryId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }

    @Override
    public int add(CategoryBrand categoryBrand) {
        long count = categoryBrandMapper.selectCount(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId())
                .eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
        if (count > 0) {
            throw new ServiceException("该分类已加该品牌");
        }
        return categoryBrandMapper.insert(categoryBrand);
    }

    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {
        CategoryBrand categoryBrandDB = categoryBrandMapper.selectOne(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId())
                .eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
        if (categoryBrandDB != null && categoryBrand.getId().intValue() != categoryBrandDB.getId().intValue()) {
            throw new ServiceException("数据已经存在");
        }
        return categoryBrandMapper.updateById(categoryBrand);
    }

}