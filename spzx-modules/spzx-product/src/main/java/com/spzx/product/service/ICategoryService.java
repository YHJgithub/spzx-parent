package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.domain.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品分类Service接口
 */
public interface ICategoryService extends IService<Category> {

    List<Category> treeSelect(Long id);

    List<Long> getAllCategoryIdList(Long categoryId);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);

    List<CategoryVo> getOneCategory();

    List<CategoryVo> tree();
}