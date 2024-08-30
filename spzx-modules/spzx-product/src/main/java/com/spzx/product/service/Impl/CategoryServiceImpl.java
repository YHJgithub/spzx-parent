package com.spzx.product.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
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

    @Override
    public void exportData(HttpServletResponse response) {
        try {
            // 设置响应结果类型
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("分类数据", "utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 查询数据库中的数据
            List<Category> categoryList = categoryMapper.selectList(null);
            ArrayList<CategoryExcelVo> categoryExcelVoList = new ArrayList<>(categoryList.size());
            // 将从数据库中查询到的Category对象转换成CategoryExcelVo对象
            for (Category category : categoryList) {
                CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                BeanUtils.copyProperties(category, categoryExcelVo);
                categoryExcelVoList.add(categoryExcelVo);
            }
            // 写出数据到浏览器端
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class)
                    .sheet("分类数据").doWrite(categoryExcelVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importData(MultipartFile file) {
        try {
            List<CategoryExcelVo> categoryExcelVoList = EasyExcel.read(file.getInputStream())
                    .head(CategoryExcelVo.class).sheet().doReadSync();
            if (!CollectionUtils.isEmpty(categoryExcelVoList)) {
                ArrayList<Category> categoryList = new ArrayList<>(categoryExcelVoList.size());
                for (CategoryExcelVo categoryExcelVo : categoryExcelVoList) {
                    Category category = new Category();
                    BeanUtils.copyProperties(categoryExcelVo, category);
                    categoryList.add(category);
                }
                this.saveBatch(categoryList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}