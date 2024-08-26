package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品分类Service业务层处理
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> treeSelect(Long parentId) {

        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId));

        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, category.getId()));
                category.setHasChildren(count > 0);
            }
        }
        return categoryList;
    }

    @Override
    public List<Long> getAllCategoryIdList(Long id) {
        List<Long> idList = new ArrayList<Long>();
        List<Category> categoryList = this.getParentCategory(id, new ArrayList<Category>());
        int size = categoryList.size();
        for (int i = size - 1; i >= 0; i--) {
            idList.add(categoryList.get(i).getId());
        }
        return idList;
    }

    private List<Category> getParentCategory(Long id, ArrayList<Category> categoryList) {
        while (id > 0) {
            Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                    .eq(Category::getId, id)
                    .select(Category::getId, Category::getParentId));
            categoryList.add(category);
            return getParentCategory(category.getParentId(), categoryList);
        }
        return categoryList;
    }
}